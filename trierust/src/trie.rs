use std::collections::HashMap;

pub struct TrieNode {
    _current: char,
    childrens: HashMap<char, TrieNode>,
    is_end: bool,
}

impl TrieNode {
    pub fn new(a: char) -> Self {
        TrieNode { _current: a, childrens: HashMap::new(), is_end: false }
    }

    pub fn insert(&mut self, word: &str) {
        let mut cur = self;
        for c in word.chars() {
            if !cur.childrens.contains_key(&c) {
                let next_node = TrieNode::new(c);
                cur.childrens.insert(c, next_node);
            }
            cur = cur.childrens.get_mut(&c).unwrap();
        }
        cur.is_end = true;
    }

    pub fn search(&self, word: &str) -> bool {
        let mut cur = self;
        for c in word.chars() {
            if cur.childrens.contains_key(&c) {
                cur = cur.childrens.get(&c).unwrap();
            } else {
                return false;
            }
        }

        return cur.is_end;
    }

    pub fn delete(&mut self, word: &str) {
        Self::delete_helper(self, word, 0);
    }

    pub fn delete_helper(node: &mut TrieNode, word: &str, index: usize) -> bool {
        if index == word.len() {
            node.is_end = false;

            return node.childrens.is_empty();
        }

        let c = word.chars().nth(index).unwrap();
        let should_delete_child = {
            if node.childrens.contains_key(&c) {
                Self::delete_helper(node.childrens.get_mut(&c).unwrap(), word, index)
            } else {
                return false;
            }
        };

        if should_delete_child {
            node.childrens.remove(&c);
        }

        !node.is_end && node.childrens.is_empty()
    }

}