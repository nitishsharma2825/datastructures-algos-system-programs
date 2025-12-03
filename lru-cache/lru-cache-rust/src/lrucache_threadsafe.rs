use std::{collections::HashMap, sync::{Arc, Mutex, Weak}};

struct Node {
    key: String,
    value: String,
    left: Option<Weak<Mutex<Node>>>, // prevent cycles using weak
    right: Option<Arc<Mutex<Node>>>,
}

pub struct LRUCache {
    inner: Arc<Mutex<LRUCacheInner>>,
}
struct LRUCacheInner {
    index: HashMap<String, Arc<Mutex<Node>>>,
    capacity: usize,
    head: Arc<Mutex<Node>>,
    tail: Arc<Mutex<Node>>,
}

impl LRUCache {
    pub fn new(capacity: usize) -> Self {
        let head = Arc::new(Mutex::new(Node { key: String::new(), value: String::new(), left: None, right: None }));
        let tail = Arc::new(Mutex::new(Node { key: String::new(), value: String::new(), left: None, right: None }));

        {
            let mut h = head.lock().unwrap();
            h.right = Some(tail.clone());
        }

        {
            let mut t = tail.lock().unwrap();
            t.left = Some(Arc::downgrade(&head));
        }

        let inner = LRUCacheInner {
            index: HashMap::new(),
            capacity: capacity,
            head: head,
            tail: tail
        };

        Self { inner: Arc::new(Mutex::new(inner)) }
    }

    pub fn get(&self, key: &str) -> Option<String> {
        let mut inner = self.inner.lock().unwrap();

        let val = inner.index.get(key).cloned();
        match val {
            Some(c_val) => {
                Self::remove_node(&mut inner,&c_val);
                Self::insert_at_front(&mut inner, &c_val);
                Some(c_val.lock().unwrap().value.clone())
            },
            None => None,
        }
    }

    pub fn set(&mut self, key: String, val: String) {
        let mut inner = self.inner.lock().unwrap();

        let cur_val = inner.index.get(&key).cloned();
        match cur_val {
            Some(c_val) => {
                c_val.lock().unwrap().value = val;
                Self::remove_node(&mut inner, &c_val);
                Self::insert_at_front(&mut inner, &c_val)
            },
            None => {
                if inner.index.len() == inner.capacity {
                    let node_to_remove = inner.tail.lock().unwrap().left.as_ref().unwrap().upgrade().unwrap();
                    let old_key = node_to_remove.lock().unwrap().key.clone();
                    assert!(node_to_remove.lock().unwrap().key != "", "can't be head node");
                    Self::remove_node(&mut inner, &node_to_remove);
                    inner.index.remove(&old_key);
                }

                let new_node = Arc::new(Mutex::new(Node { key: key.clone(), value: val, left: None, right: None }));
                Self::insert_at_front(&mut inner, &new_node);
                inner.index.insert(key, new_node);
            },
        }
    }

    fn remove_node(inner: &mut LRUCacheInner, node: &Arc<Mutex<Node>>) {
        let (prev_node, next_node) = {
            let n = node.lock().unwrap();
            (n.left.clone(), n.right.clone())
        };

        let prev_node = prev_node.unwrap().upgrade().unwrap();
        let next_node = next_node.unwrap();

        prev_node.lock().unwrap().right = Some(next_node.clone());
        next_node.lock().unwrap().left = Some(Arc::downgrade(&prev_node));
    }

    fn insert_at_front(inner: &mut LRUCacheInner, node: &Arc<Mutex<Node>>) {
        let next_to_head = inner.head.lock().unwrap().right.clone().unwrap();

        {
            let mut n = node.lock().unwrap();
            n.right = Some(next_to_head.clone());
            n.left = Some(Arc::downgrade(&inner.head));
        }

        next_to_head.lock().unwrap().left = Some(Arc::downgrade(node));
        inner.head.lock().unwrap().right = Some(node.clone());
    }
}