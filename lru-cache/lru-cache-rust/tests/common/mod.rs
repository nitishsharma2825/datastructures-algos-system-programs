use lrucacherust::LRUCache;

// Files in subdir of tests directory don't get compiled as separate crates
// can be used for helper code
pub fn setup(capacity: usize) -> LRUCache {
    LRUCache::new(capacity)
}