import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingLRUCache {
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
    private final Condition keyNotPresent;

    public BlockingLRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
        lock = new ReentrantLock();
        keyNotPresent = lock.newCondition();
    }

    public int get(int key) {
        lock.lock();
        try {
            while (!map.containsKey(key)) {
                keyNotPresent.await(); // wait until this key is inserted by someone
            }

            Node cur = map.get(key);
            removeNode(cur);
            insertAtFront(cur);
            return cur.value;
        } catch (Exception ignored)
        {
        } finally {
            lock.unlock();
        }

        return -1;
    }

    public void put(int key, int value) {
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

                // Signal that something is available
                keyNotPresent.signalAll();
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
        BlockingLRUCache cache = new BlockingLRUCache(3);
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
