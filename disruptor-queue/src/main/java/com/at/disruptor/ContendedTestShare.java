package com.at.disruptor;


/**
 * @author zero
 * @create 2022-10-16
 */
public class ContendedTestShare {

    volatile long a;

    volatile long b;

    public static void main(String[] args) throws InterruptedException {

        ContendedTestShare c = new ContendedTestShare();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000_0000L; i++) {
                c.a = i;
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000_0000L; i++) {
                c.b = i;
            }
        });
        final long start = System.nanoTime();
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        // 1823
        System.out.println((System.nanoTime() - start) / 100_0000);
    }



}
