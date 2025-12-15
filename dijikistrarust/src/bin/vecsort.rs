#[derive(Debug, Eq, PartialEq)]
pub struct Pair {
    id: usize,
    cost: i32,
}

impl Ord for Pair {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        self.cost.cmp(&other.cost)
            .then_with(|| other.id.cmp(&self.id))
    }
}

impl PartialOrd for Pair {
    fn partial_cmp(&self, other: &Self) -> Option<std::cmp::Ordering> {
        Some(self.cmp(other))
    }
}

fn main() {
    let mut items = vec![
        Pair {id: 1, cost: 10},
        Pair {id: 10, cost: -1},
        Pair {id: 3, cost: 5},
        Pair {id: 4, cost: 100},
        Pair {id: 2, cost: 100},
    ];

    items.sort();
    println!("{:?}", items);
}
