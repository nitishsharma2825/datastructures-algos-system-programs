import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache {
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next; // default is null for object references
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;
    private HashMap<Integer, Node> map;
    private Node head;
    private Node tail;
    private final ReentrantLock lock;
//    private final ReentrantReadWriteLock rwLock;
//    private final Lock readLock;
//    private final Lock writeLock;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
        lock = new ReentrantLock();
        // rwLock = new ReentrantReadWriteLock();
        // readLock = rwLock.readLock();
        // writeLock = rwLock.writeLock();
    }

    public int get(int key) {
        // Since get also modifies the structures, its similar to write
        // A readWrite lock thus does not help
        // readLock.lock();
        lock.lock();
        try {
            if (map.containsKey(key)) {
                Node cur = map.get(key);
                removeNode(cur);
                insertAtFront(cur);
                return cur.value;
            }
        } finally {
            lock.unlock();
        }

        return -1;
    }

    public void put(int key, int value) {
        // writeLock.lock();
        lock.lock();
        try {
            if (map.containsKey(key)) {
                // key is already present
                Node cur = map.get(key);
                cur.value = value;
                // remove this node from current position and move to front
                removeNode(cur);
                insertAtFront(cur);
            } else {
                // key is new
                if (this.map.size() == capacity) {
                    // there is no capacity
                    // remove the last node
                    Node nodeToRemove = tail.prev;
                    assert nodeToRemove.key != -1;
                    removeNode(nodeToRemove);
                    // remove the map entry
                    map.remove(nodeToRemove.key);
                }
                // there is capacity
                // insert at end
                Node newNode = new Node(key, value);
                insertAtFront(newNode);
                // set the map entry
                map.put(key, newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    private void removeNode(Node nodeToRemove) {
        Node prevNode = nodeToRemove.prev;
        Node nextNode = nodeToRemove.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private void insertAtFront(Node newNode) {
        newNode.next = head.next;
        newNode.prev = head;
        head.next = newNode;
        newNode.next.prev = newNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node cur = head.next;
        while (cur != tail) {
            sb.append("(").append(cur.key).append(",").append(cur.value).append(") ");
            cur = cur.next;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(3);
        cache.put(10, 5);
        System.out.println(cache.get(10));
        cache.put(2, 3);
        cache.put(3, 4);
        System.out.println(cache.get(3));
        cache.put(4, 5);
        System.out.println(cache.get(10));
        System.out.println(cache);
    }
}
