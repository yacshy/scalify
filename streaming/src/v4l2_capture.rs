//! V4L2 MMAP 采集外接摄像头 MJPEG 帧。
//!
//! 纯 Rust + libc::ioctl, 不依赖 V4L2 crate。
//! 所有 ioctl 值和 struct offset 都从 linux/videodev2.h (gcc 实测) 确认。

use std::{ffi::CString, io, os::unix::io::RawFd, ptr};

use libc::{close, mmap, munmap, open};

// ——————————————— 设备探测 ———————————————

pub fn find_external_camera_path() -> Option<String> {
    let dir = std::fs::read_dir("/sys/class/video4linux").ok()?;
    let mut candidates: Vec<(u32, String)> = Vec::new();
    for entry in dir.flatten() {
        let path = entry.path();
        let real = std::fs::canonicalize(&path).ok()?;
        if !real.components().any(|c| c.as_os_str().to_string_lossy().starts_with("usb")) {
            continue;
        }
        let mut cursor: Option<&std::path::Path> = Some(&real);
        while let Some(p) = cursor {
            if p.join("idVendor").is_file() && p.join("idProduct").is_file() {
                let removable = std::fs::read_to_string(p.join("removable"))
                    .map(|s| s.trim() == "removable")
                    .unwrap_or(false);
                if removable {
                    let name = path.file_name()?.to_string_lossy().into_owned();
                    // videoN → 数字
                    let num: u32 = name.trim_start_matches("video").parse().unwrap_or(9999);
                    candidates.push((num, format!("/dev/{}", name)));
                }
                break;
            }
            cursor = p.parent();
        }
    }
    // 排序挑最小设备号（每个摄像头两个节点，capture 号 < metadata 号）
    candidates.sort_by_key(|(n, _)| *n);
    candidates.first().map(|(_, d)| d.clone())
}

// ——————————————— ioctl 常量 (从 linux/videodev2.h 实测确认) ———————————————

const O_RDWR: i32 = 2;

const VIDEOCAPTURE: u32 = 1;
const VIDEOMEMORYMMAP: u32 = 1;

/// sizeof v4l2_format = 208, nr = 0
const VIDIOC_QUERYCAP:  libc::c_ulong = 0x80685600;
/// sizeof v4l2_format = 208, nr = 5
const VIDIOC_S_FMT:     libc::c_ulong = 0xc0d05605;
/// sizeof v4l2_requestbuffers = 20, nr = 8
const VIDIOC_REQBUFS:   libc::c_ulong = 0xc0145608;
/// sizeof v4l2_buffer = 88, nr = 9
const VIDIOC_QUERYBUF:  libc::c_ulong = 0xc0585609;
/// sizeof v4l2_buffer = 88, nr = 17
const VIDIOC_DQBUF:     libc::c_ulong = 0xc0585611;
/// sizeof v4l2_buffer = 88, nr = 15
const VIDIOC_QBUF:      libc::c_ulong = 0xc058560f;
/// sizeof __u32 = 4, direction = _IOW, nr = 18
const VIDIOC_STREAMON:  libc::c_ulong = 0x40045612;
/// sizeof __u32 = 4, direction = _IOW, nr = 19
const VIDIOC_STREAMOFF: libc::c_ulong = 0x40045613;

// ——————————————— v4l2_format field offsets (实测) ———————————————
// struct v4l2_format {
//     __u32 type;       // offset 0, size 4
//     // 4 bytes padding
//     union { ... } fmt; // offset 8
//         struct v4l2_pix_format pix;  // 从 union fmt 起始：
//             __u32 width;              // offset 0 (→ 整体 8)
//             __u32 height;             // offset 4 (→ 整体 12)
//             __u32 pixelformat;        // offset 8 (→ 整体 16)
//             __u32 field;              // offset 12 (→ 整体 20)
//             __u32 bytesperline;       // offset 16 (→ 整体 24)
//             __u32 sizeimage;          // offset 20 (→ 整体 28)
//             __u32 colorspace;         // offset 24 (→ 整体 32)
//             __u32 priv;               // offset 28 (→ 整体 36)
//             __u32 flags;              // offset 32 (→ 整体 40)
// }
const FMT_TYPE_OFF: usize = 0;
const FMT_FMT_OFF: usize = 8;
// v4l2_pix_format 相对于 union fmt 起始的 offset
const PIX_WIDTH_OFF: usize = FMT_FMT_OFF + 0;
const PIX_HEIGHT_OFF: usize = FMT_FMT_OFF + 4;
const PIX_PIXELFMT_OFF: usize = FMT_FMT_OFF + 8;
const PIX_BYTESPERLINE_OFF: usize = FMT_FMT_OFF + 16;

// ——————————————— v4l2_buffer field offsets (实测) ———————————————
// sizeof = 88
const BUF_INDEX_OFF: usize = 0;
const BUF_TYPE_OFF: usize = 4;
const BUF_BYTESUSED_OFF: usize = 8;
const BUF_MEMORY_OFF: usize = 60;
const BUF_M_OFFSET_OFF: usize = 64;
const BUF_LENGTH_OFF: usize = 72;
// timestamp struct timeval { tv_sec(8) + tv_usec(8) } at offset 24
const BUF_TIMESTAMP_OFF: usize = 24;

// ———————————————— 采集器 ————————————————

pub struct V4l2Capture {
    fd: RawFd,
    buffers: Vec<MmapBuffer>,
    width: u32,
    height: u32,
}

struct MmapBuffer {
    ptr: *mut u8,
    len: usize,
}

unsafe impl Send for V4l2Capture {}

impl V4l2Capture {
    pub fn open(path: &str, width: u32, height: u32) -> io::Result<Self> {
        let c_path = CString::new(path).unwrap();
        let fd = unsafe { open(c_path.as_ptr(), O_RDWR) };
        if fd < 0 { return Err(io::Error::last_os_error()); }

        // 1) SET_FMT: MJPG
        let mut fmt = [0u8; 208];
        unsafe {
            ptr::write_unaligned(fmt.as_mut_ptr().add(FMT_TYPE_OFF) as *mut u32, VIDEOCAPTURE);
            ptr::write_unaligned(fmt.as_mut_ptr().add(PIX_PIXELFMT_OFF) as *mut u32,
                                 u32::from_le_bytes(*b"MJPG"));
            ptr::write_unaligned(fmt.as_mut_ptr().add(PIX_WIDTH_OFF) as *mut u32, width);
            ptr::write_unaligned(fmt.as_mut_ptr().add(PIX_HEIGHT_OFF) as *mut u32, height);
        }
        if unsafe { libc::ioctl(fd, VIDIOC_S_FMT, &mut fmt as *mut u8) } < 0 {
            unsafe { close(fd); }
            return Err(io::Error::last_os_error());
        }
        let real_w = unsafe { ptr::read_unaligned(fmt.as_ptr().add(PIX_WIDTH_OFF) as *const u32) };
        let real_h = unsafe { ptr::read_unaligned(fmt.as_ptr().add(PIX_HEIGHT_OFF) as *const u32) };

        // 2) REQBUFS: 4 x MMAP
        let mut req = [0u8; 20];
        unsafe {
            ptr::write_unaligned(req.as_mut_ptr().add(0) as *mut u32, 4u32);  // count
            ptr::write_unaligned(req.as_mut_ptr().add(4) as *mut u32, VIDEOCAPTURE);
            ptr::write_unaligned(req.as_mut_ptr().add(8) as *mut u32, VIDEOMEMORYMMAP);
        }
        if unsafe { libc::ioctl(fd, VIDIOC_REQBUFS, &mut req as *mut u8) } < 0 {
            unsafe { close(fd); }
            return Err(io::Error::last_os_error());
        }

        // 3) QUERYBUF + MMAP + QBUF
        let page_size = unsafe { libc::sysconf(30) /* _SC_PAGESIZE */ } as usize;
        let mut buffers = Vec::with_capacity(4);
        for i in 0..4u32 {
            let mut buf = [0u8; 88];
            unsafe {
                ptr::write_unaligned(buf.as_mut_ptr().add(BUF_INDEX_OFF) as *mut u32, i);
                ptr::write_unaligned(buf.as_mut_ptr().add(BUF_TYPE_OFF) as *mut u32, VIDEOCAPTURE);
                ptr::write_unaligned(buf.as_mut_ptr().add(BUF_MEMORY_OFF) as *mut u32, VIDEOMEMORYMMAP);
            }
            if unsafe { libc::ioctl(fd, VIDIOC_QUERYBUF, &mut buf as *mut u8) } < 0 {
                unsafe { close(fd); }
                return Err(io::Error::last_os_error());
            }
            let offset = unsafe { ptr::read_unaligned(buf.as_ptr().add(BUF_M_OFFSET_OFF) as *const u32) };
            let buflen = unsafe { ptr::read_unaligned(buf.as_ptr().add(BUF_LENGTH_OFF) as *const u32) } as usize;
            let map_len = buflen.max(page_size);
            let p = unsafe {
                mmap(ptr::null_mut(), map_len, libc::PROT_READ | libc::PROT_WRITE,
                     libc::MAP_SHARED, fd, offset as libc::off_t)
            };
            if p == libc::MAP_FAILED {
                unsafe { close(fd); }
                return Err(io::Error::last_os_error());
            }
            buffers.push(MmapBuffer { ptr: p as *mut u8, len: map_len });

            let mut qbuf = [0u8; 88];
            unsafe {
                ptr::write_unaligned(qbuf.as_mut_ptr().add(BUF_INDEX_OFF) as *mut u32, i);
                ptr::write_unaligned(qbuf.as_mut_ptr().add(BUF_TYPE_OFF) as *mut u32, VIDEOCAPTURE);
                ptr::write_unaligned(qbuf.as_mut_ptr().add(BUF_MEMORY_OFF) as *mut u32, VIDEOMEMORYMMAP);
            }
            if unsafe { libc::ioctl(fd, VIDIOC_QBUF, &mut qbuf as *mut u8) } < 0 {
                unsafe { close(fd); }
                return Err(io::Error::last_os_error());
            }
        }

        // 4) STREAMON
        let stream_type: u32 = VIDEOCAPTURE;
        if unsafe { libc::ioctl(fd, VIDIOC_STREAMON, &stream_type as *const u32) } < 0 {
            for b in &buffers { unsafe { munmap(b.ptr as *mut libc::c_void, b.len); } }
            unsafe { close(fd); }
            return Err(io::Error::last_os_error());
        }

        tracing::info!(path, w = real_w, h = real_h, "V4L2 MJPEG capture started");
        Ok(Self { fd, buffers, width: real_w, height: real_h })
    }

    pub fn width(&self) -> u32 { self.width }
    pub fn height(&self) -> u32 { self.height }

    /// 阻塞取一帧 MJPEG 数据 + pts (微秒)
    pub fn read_frame(&self) -> io::Result<(Vec<u8>, u64)> {
        let mut buf = [0u8; 88];
        unsafe {
            ptr::write_unaligned(buf.as_mut_ptr().add(BUF_TYPE_OFF) as *mut u32, VIDEOCAPTURE);
            ptr::write_unaligned(buf.as_mut_ptr().add(BUF_MEMORY_OFF) as *mut u32, VIDEOMEMORYMMAP);
        }
        if unsafe { libc::ioctl(self.fd, VIDIOC_DQBUF, &mut buf as *mut u8) } < 0 {
            return Err(io::Error::last_os_error());
        }
        let idx = unsafe { ptr::read_unaligned(buf.as_ptr().add(BUF_INDEX_OFF) as *const u32) } as usize;
        let mmap_ptr = self.buffers[idx].ptr;

        // 扫 JPEG EOI (0xFFD9) 找真实长度
        let max = self.buffers[idx].len.min(4_000_000);
        let slice = unsafe { std::slice::from_raw_parts(mmap_ptr, max) };
        let mut end = 0;
        for i in 1..max {
            if slice[i-1] == 0xFF && slice[i] == 0xD9 { end = i + 1; break; }
        }
        if end == 0 { end = 4096; }

        // pts: struct timeval { tv_sec(i64) at offset 24, tv_usec(i64) at offset 32 }
        let sec  = unsafe { ptr::read_unaligned(buf.as_ptr().add(BUF_TIMESTAMP_OFF) as *const i64) } as u64;
        let usec = unsafe { ptr::read_unaligned(buf.as_ptr().add(BUF_TIMESTAMP_OFF + 8) as *const i64) } as u64;
        let pts = sec * 1_000_000 + usec;

        let frame = unsafe { std::slice::from_raw_parts(mmap_ptr, end) }.to_vec();

        let mut qbuf = [0u8; 88];
        unsafe {
            ptr::write_unaligned(qbuf.as_mut_ptr().add(BUF_INDEX_OFF) as *mut u32, idx as u32);
            ptr::write_unaligned(qbuf.as_mut_ptr().add(BUF_TYPE_OFF) as *mut u32, VIDEOCAPTURE);
            ptr::write_unaligned(qbuf.as_mut_ptr().add(BUF_MEMORY_OFF) as *mut u32, VIDEOMEMORYMMAP);
        }
        if unsafe { libc::ioctl(self.fd, VIDIOC_QBUF, &mut qbuf as *mut u8) } < 0 {
            return Err(io::Error::last_os_error());
        }
        Ok((frame, pts))
    }
}

impl Drop for V4l2Capture {
    fn drop(&mut self) {
        let stream_type: u32 = VIDEOCAPTURE;
        unsafe { libc::ioctl(self.fd, VIDIOC_STREAMOFF, &stream_type as *const u32); }
        for b in &self.buffers {
            unsafe { munmap(b.ptr as *mut libc::c_void, b.len); }
        }
        unsafe { close(self.fd); }
    }
}
