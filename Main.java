
public class Main {
    public static void main(String[] args) {
        class Bakery {
            private int breadCount;
            private final Lock lock = new ReentrantLock();
            private final Condition breadAvailable = lock.newCondition();

            public Bakery() {
                this.breadCount = 0;
            }

            public void produceBread() throws InterruptedException {
                lock.lock();
                try {
                    breadCount++;
                    System.out.println("Bakery produced 1 bread. Total bread count: " + breadCount);
                    breadAvailable.signalAll();
                } finally {
                    lock.unlock();
                }
                Thread.sleep(3000); // Producing bread takes 3 seconds
            }

            public boolean sellBread() throws InterruptedException {
                lock.lock();
                try {
                    if (breadCount > 0) {
                        breadCount--;
                        System.out.println("Shop bought 1 bread. Remaining bread count: " + breadCount);
                        return true;
                    } else {
                        System.out.println("No bread available. Waiting for bakery to produce bread...");
                        breadAvailable.await();
                        return false;
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        class Shop extends Thread {
            private final Bakery bakery;

            public Shop(Bakery bakery) {
                this.bakery = bakery;
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        if (bakery.sellBread()) {
                            // Customer bought bread
                            Thread.sleep(1000); // Simulating customer enjoying the bread
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public class Main {
            public static void main(String[] args) {
                Bakery bakery = new Bakery();
                int numOfShops = 3; // Number of shop threads

                // Create and start shop threads
                for (int i = 0; i < numOfShops; i++) {
                    new Shop(bakery).start();
                }

                // Start bakery thread
                new Thread(() -> {
                    try {
                        while (true) {
                            bakery.produceBread();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}