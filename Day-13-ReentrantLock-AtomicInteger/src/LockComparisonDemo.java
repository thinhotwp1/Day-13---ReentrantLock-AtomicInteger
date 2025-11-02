import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

// -------- 1. Dùng synchronized --------
class SynchronizedCounter {
    private int count = 0;

    // Khóa được nhả tự động khi ra khỏi phương thức
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}

// -------- 2. Dùng ReentrantLock --------
class ReentrantLockCounter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock(); // Tạo khóa

    public void increment() {
        lock.lock(); // Phải khóa thủ công
        try {
            count++;
        } finally {
            lock.unlock(); // BẮT BUỘC phải nhả trong finally
        }
    }

    public int getCount() {
        return count; // (Việc đọc cũng nên được khóa, nhưng để demo đơn giản)
    }
}

// -------- 3. Chương trình chính --------
public class LockComparisonDemo {
    public static void main(String[] args) throws InterruptedException {
        int tasks = 100000;
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // --- Thử nghiệm Synchronized ---
        SynchronizedCounter syncCounter = new SynchronizedCounter();
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < tasks; j++) {
                    syncCounter.increment();
                }
            });
        }
        
        // Chờ 2 luồng này chạy xong (code này không an toàn, chỉ để demo)
        // Trong thực tế, bạn cần tắt executor và awaitTermination
        Thread.sleep(2000); 
        System.out.println("Kết quả Synchronized: " + syncCounter.getCount()); // Ra 200000

        
        // --- Thử nghiệm ReentrantLock ---
        ReentrantLockCounter lockCounter = new ReentrantLockCounter();
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < tasks; j++) {
                    lockCounter.increment();
                }
            });
        }

        Thread.sleep(2000);
        System.out.println("Kết quả ReentrantLock: " + lockCounter.getCount()); // Ra 200000

        executor.shutdown();
    }
}