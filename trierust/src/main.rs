use trierust::trie::TrieNode;

fn main() {
    let mut root = TrieNode::new('#');
    root.insert("nitish");
    let found = root.search("nitish");
    println!("{found}");
}
