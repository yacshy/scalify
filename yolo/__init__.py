from ultralytics import YOLO

# Load a pretrained YOLO model (recommended for training)
model = YOLO("yolo26n.pt")


# Perform object detection on an image using the model
results = model("https://ultralytics.com/images/bus.jpg")

for result in results:
    result.show()