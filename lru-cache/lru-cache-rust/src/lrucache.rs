use std::{cell::RefCell, collections::HashMap, rc::{Rc, Weak}};

struct Node {
    key: String,
    value: String,
    left: Option<Weak<RefCell<Node>>>, // prevent cycles using weak
    right: Option<Rc<RefCell<Node>>>,
}

pub struct LRUCache {
    index: HashMap<String, Rc<RefCell<Node>>>,
    capacity: usize,
    head: Rc<RefCell<Node>>,
    tail: Rc<RefCell<Node>>,
}

impl LRUCache {
    pub fn new(capacity: usize) -> Self {
        let head = Rc::new(RefCell::new(Node { key: String::new(), value: String::new(), left: None, right: None }));
        let tail = Rc::new(RefCell::new(Node { key: String::new(), value: String::new(), left: None, right: None }));
        
        head.borrow_mut().right = Some(tail.clone());
        tail.borrow_mut().left = Some(Rc::downgrade(&head));

        LRUCache {
            index: HashMap::new(),
            capacity: capacity,
            head: head,
            tail: tail
        }
    }

    pub fn get(&self, key: &str) -> Option<String> {
        let val = self.index.get(key).cloned();
        match val {
            Some(c_val) => {
                self.remove_node(&c_val);
                self.insert_at_front(&c_val);
                Some(c_val.borrow().value.clone())
            },
            None => None,
        }
    }

    pub fn set(&mut self, key: String, val: String) {
        let cur_val = self.index.get(&key).cloned();
        match cur_val {
            Some(c_val) => {
                c_val.borrow_mut().value = val;
                self.remove_node(&c_val);
                self.insert_at_front(&c_val)
            },
            None => {
                if self.index.len() == self.capacity {
                    let node_to_remove = self.tail.borrow().left.as_ref().unwrap().upgrade().unwrap();
                    let old_key = node_to_remove.borrow().key.clone();
                    assert!(node_to_remove.borrow().key != "", "can't be head node");
                    self.remove_node(&node_to_remove);
                    self.index.remove(&old_key);
                }

                let new_node = Rc::new(RefCell::new(Node { key: key.clone(), value: val, left: None, right: None }));
                self.insert_at_front(&new_node);
                self.index.insert(key, new_node);
            },
        }
    }

    fn remove_node(&self, node: &Rc<RefCell<Node>>) {
        let (prev_node, next_node) = {
            let n = node.borrow();
            (n.left.clone(), n.right.clone())
        };

        let prev_node = prev_node.unwrap().upgrade().unwrap();
        let next_node = next_node.unwrap();

        prev_node.borrow_mut().right = Some(next_node.clone());
        next_node.borrow_mut().left = Some(Rc::downgrade(&prev_node));
    }

    fn insert_at_front(&self, node: &Rc<RefCell<Node>>) {
        let next_to_head = self.head.borrow().right.clone().unwrap();

        {
            let mut n = node.borrow_mut();
            n.right = Some(next_to_head.clone());
            n.left = Some(Rc::downgrade(&self.head));
        }

        next_to_head.borrow_mut().left = Some(Rc::downgrade(node));
        self.head.borrow_mut().right = Some(node.clone());
    }
}