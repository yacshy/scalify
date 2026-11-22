struct User {
  name: String,
}

struct TupleA(i32, i32, &'static str);

fn main() {
  let user = User {
    name: String::from("yacshy"),
  };

  println!("{}", user.name);

  let tuple_data = TupleA(1, 2, "hello");

  println!("{}", tuple_data.0);
  println!("{}", tuple_data.1);
  println!("{}", tuple_data.2);
}
