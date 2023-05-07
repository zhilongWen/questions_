//package com.at.disruptor.test;
//
//
//import sun.misc.Contended;
//
///**
// *
// * https://developer.aliyun.com/article/1000205
// *
// * @author zero
// * @create 2022-10-16
// */
//public class ContendedTestNoShare {
//
//    @Contended
//    volatile int a;
//
//    @Contended
//    volatile int b;
//
//    public static void main(String[] args) throws InterruptedException {
//
//        ContendedTestNoShare c = new ContendedTestNoShare();
//        Thread thread1 = new Thread(() -> {
//            for (int i = 0; i < 10000_0000L; i++) {
//                c.a = i;
//            }
//        });
//        Thread thread2 = new Thread(() -> {
//            for (int i = 0; i < 10000_0000L; i++) {
//                c.b = i;
//            }
//        });
//        final long start = System.nanoTime();
//        thread1.start();
//        thread2.start();
//        thread1.join();
//        thread2.join();
//        // 455
//        System.out.println((System.nanoTime() - start) / 100_0000);
//    }
//
//
//
//}
