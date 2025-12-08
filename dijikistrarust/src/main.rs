use std::{collections::BinaryHeap, i32};

#[derive(Debug)]
pub struct Edge {
    dest: i32,
    starttime: i32,
    endtime: i32,
}

#[derive(Debug, PartialEq, Eq)]
pub struct Pair {
    node: i32,
    time: i32,
}

impl Ord for Pair {
    fn cmp(&self, other: &Self) -> std::cmp::Ordering {
        // by default binary heap is max-heap, so reverse it
        other.time.cmp(&self.time)
            .then_with(|| self.node.cmp(&other.node))
    }
}

impl PartialOrd for Pair {
    fn partial_cmp(&self, other: &Self) -> Option<std::cmp::Ordering> {
        Some(self.cmp(other))
    }
}

pub struct Solution;

impl Solution {
    pub fn min_time(n: i32, edges: Vec<Vec<i32>>) -> i32 {
        let mut adjl: Vec<Vec<Edge>> = Vec::with_capacity(n as usize);
        for _ in 0..n {
            adjl.push(Vec::new());
        }

        for edge in edges {
            let src = edge[0] as usize;
            adjl[src].push(Edge { dest: edge[1], starttime: edge[2], endtime: edge[3] });
        }

        let mut pq: BinaryHeap<Pair> = BinaryHeap::new();
        let mut visited = vec![false; n as usize];
        let mut dist = vec![i32::MAX; n as usize];

        dist[0] = 0;
        pq.push(Pair { node: 0, time: 0 });

        while let Some( Pair { node, time}) = pq.pop() {
            visited[node as usize] = true;

            if time > dist[node as usize] {
                continue;
            }

            for e in &adjl[node as usize] {
                let dest = e.dest;

                if visited[dest as usize] {
                    continue;
                }

                if dist[node as usize] > e.endtime {
                    continue;
                } 

                let mut best = dist[node as usize] + 1;
                if dist[node as usize] < e.starttime {
                    best = e.starttime + 1;
                }

                if best < dist[dest as usize] {
                    dist[dest as usize] = best;
                    pq.push(Pair { node: dest, time: best });
                }
            }
        }

        println!("{:?}", dist);

        if dist[(n - 1) as usize] == i32::MAX {
            return -1;
        }

        dist[(n-1) as usize]
    }
}

fn main() {
    let n = 3;
    let test = vec![vec![0, 1, 0, 1], vec![1, 2, 2, 5]];
    let ans = Solution::min_time(n, test);
    println!("{}", ans);
}


// Ord -> PartialOrd, Eq, PartialEq