use std::env;

fn main() {
  let env_var: String = match env::var("Flutter") {
    Ok(var) => var,
    Err(e) => {
      panic!("can not find WebStorm env var");
    }
  };
  println!("{}", env_var);
}
