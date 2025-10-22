import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LRUCacheBenchmark {
    private static void singleThreadBenchmark(LRUCache cache) {
        int numOperations = 10_000_000;
        int capacity = 10000;

        // warm up phase (JIT optimization)
        for (int i = 0; i < 1_000_000; i++) {
            cache.put(i % capacity, i);
        }
        for (int i = 0; i < 1_000_000; i++) {
            cache.get(i % capacity);
        }

        long startPut = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            cache.put(i % capacity, i);
        }
        long endPut = System.nanoTime();

        double elapsedSeconds = (endPut - startPut) / 1_000_000_000.0;
        double opsPerSec = numOperations / elapsedSeconds;
        System.out.printf("Performed %,d operations in %.3f s%n", numOperations, elapsedSeconds);

        long startGet = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            cache.get(i % capacity);
        }
        long endGet = System.nanoTime();

        // should take average of multiple iterations
        System.out.printf("Put ops/sec: %.2f%n", numOperations / ((endPut - startPut) / 1e9));
        System.out.printf("Get ops/sec: %.2f%n", numOperations / ((endGet - startGet) / 1e9));
    }

    private static void multiThreadBenchmark(LRUCache cache) {
        int capacity = 10000;
        int threads = 2;
        int opsPerThread = 5_000_000;
        try {
            ExecutorService pool = Executors.newFixedThreadPool(threads);

            long start = System.nanoTime();

            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < opsPerThread; i++) {
                        if ((i & 1) == 0) {
                            cache.put(i % capacity, i);
                        } else {
                            cache.get(i % capacity);
                        }
                    }
                });
            }

            pool.shutdown(); // no new tasks accepted
            pool.awaitTermination(1, TimeUnit.MINUTES);

            long end = System.nanoTime();

            double elapsed = (end - start) / 1e9;
            double totalOps = (double) threads * opsPerThread;
            System.out.printf("Threads: %d, Total ops: %.0f, Time: %.3fs, Throughput: %.2f ops/sec%n",
                    threads, totalOps, elapsed, totalOps / elapsed);
        } catch (Exception ignored) {
        }
    }

    private static void singleThreadBenchmark(BlockingLRUCache cache) {
        int numOperations = 10_000_000;
        int capacity = 10000;

        // warm up phase (JIT optimization)
        for (int i = 0; i < 1_000_000; i++) {
            cache.put(i % capacity, i);
        }
        for (int i = 0; i < 1_000_000; i++) {
            cache.get(i % capacity);
        }

        long startPut = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            cache.put(i % capacity, i);
        }
        long endPut = System.nanoTime();

        double elapsedSeconds = (endPut - startPut) / 1_000_000_000.0;
        double opsPerSec = numOperations / elapsedSeconds;
        System.out.printf("Performed %,d operations in %.3f s%n", numOperations, elapsedSeconds);

        long startGet = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            cache.get(i % capacity);
        }
        long endGet = System.nanoTime();

        // should take average of multiple iterations
        System.out.printf("Put ops/sec: %.2f%n", numOperations / ((endPut - startPut) / 1e9));
        System.out.printf("Get ops/sec: %.2f%n", numOperations / ((endGet - startGet) / 1e9));
    }

    private static void multiThreadBenchmark(BlockingLRUCache cache) {
        int capacity = 10000;
        int threads = 2;
        int opsPerThread = 5_000_000;
        try {
            ExecutorService pool = Executors.newFixedThreadPool(threads);

            long start = System.nanoTime();

            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < opsPerThread; i++) {
                        if ((i & 1) == 0) {
                            cache.put(i % capacity, i);
                        } else {
                            cache.get(i % capacity);
                        }
                    }
                });
            }

            pool.shutdown(); // no new tasks accepted
            pool.awaitTermination(1, TimeUnit.MINUTES);

            long end = System.nanoTime();

            double elapsed = (end - start) / 1e9;
            double totalOps = (double) threads * opsPerThread;
            System.out.printf("Threads: %d, Total ops: %.0f, Time: %.3fs, Throughput: %.2f ops/sec%n",
                    threads, totalOps, elapsed, totalOps / elapsed);
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        int capacity = 10000;
        LRUCache cache = new LRUCache(capacity);
        singleThreadBenchmark(cache);
        multiThreadBenchmark(cache);

        BlockingLRUCache cache2 = new BlockingLRUCache(capacity);
        singleThreadBenchmark(cache2);
        multiThreadBenchmark(cache2);
    }
}
