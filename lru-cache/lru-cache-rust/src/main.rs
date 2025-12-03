use crate::lrucache::LRUCache;

mod lrucache;

fn main() {
    let mut lru_cache = LRUCache::new(2);
    lru_cache.set(String::from("nitish"), "sharma".to_string());
    lru_cache.set(String::from("nitish2"), "sharma2".to_string());
    lru_cache.set(String::from("nitish3"), "sharma3".to_string());
    let ans2 = lru_cache.get("nitish2").unwrap();
    println!("{ans2}");
    let ans3 = lru_cache.get("nitish3").unwrap();
    println!("{ans3}");
    let ans4 = lru_cache.get("nitish").unwrap();
    println!("{ans4}");
}
