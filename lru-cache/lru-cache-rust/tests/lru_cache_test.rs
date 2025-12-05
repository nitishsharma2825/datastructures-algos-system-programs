use crate::common::setup;

mod common;

// Each file in tests directory is a separate crate
// Need the project to be binary + library crate. So can use library crate here.
#[test]
fn lru_cache_integration_test() {
    let mut cache = setup(10);
    cache.set(String::from("nitish"), String::from("sharma"));
    let ans = cache.get("nitish").unwrap();
    assert_eq!(ans, String::from("sharma"));
}