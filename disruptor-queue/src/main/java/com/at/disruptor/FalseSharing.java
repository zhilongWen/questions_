package com.at.disruptor;

import sun.misc.Contended;

/**
 * 对于伪共享，一般的解决方案是，增大数组元素的间隔使得由不同线程存取的元素位于不同的缓存行上，以空间换时间
 *
 * @author zero
 * @create 2022-10-16
 */
public class FalseSharing implements Runnable {

    public final static long ITERATIONS = 500L * 1000L * 100L;
    private int arrayIndex = 0;

    //    private static ValuePadding[] longs;
    private static ValueNoPadding[] longs;

    public FalseSharing(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public static void main(final String[] args) throws Exception {
        for (int i = 1; i < 10; i++) {
            System.gc();
            final long start = System.currentTimeMillis();
            runTest(i);
            System.out.println("Thread num " + i + " duration = " + (System.currentTimeMillis() - start));
        }

    }

    private static void runTest(int NUM_THREADS) throws InterruptedException {
        Thread[] threads = new Thread[NUM_THREADS];
//        longs = new ValuePadding[NUM_THREADS];
        longs = new ValueNoPadding[NUM_THREADS];
        for (int i = 0; i < longs.length; i++) {
//            longs[i] = new ValuePadding();
            longs[i] = new ValueNoPadding();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseSharing(i));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    @Override
    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = 0L;
        }
    }

    /**
     * Thread num 1 duration = 292
     * Thread num 2 duration = 289
     * Thread num 3 duration = 291
     * Thread num 4 duration = 325
     * Thread num 5 duration = 300
     * Thread num 6 duration = 339
     * Thread num 7 duration = 400
     * Thread num 8 duration = 411
     * Thread num 9 duration = 450
     */
    public final static class ValuePadding {
        protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        protected long p9, p10, p11, p12, p13, p14;
        protected long p15;
    }

    /**
     * Thread num 1 duration = 303
     * Thread num 2 duration = 1231
     * Thread num 3 duration = 1789
     * Thread num 4 duration = 2024
     * Thread num 5 duration = 3243
     * Thread num 6 duration = 3589
     * Thread num 7 duration = 3202
     * Thread num 8 duration = 3255
     * Thread num 9 duration = 2667
     */
    public final static class ValueNoPadding {
        // protected long p1, p2, p3, p4, p5, p6, p7;
        @Contended
        protected volatile long value = 0L;
        // protected long p9, p10, p11, p12, p13, p14, p15;

        /**
         *   备注：在jdk1.8中，有专门的注解@Contended来避免伪共享，更优雅地解决问题。
         *   -XX:-RestrictContended
         * Thread num 1 duration = 292
         * Thread num 2 duration = 308
         * Thread num 3 duration = 336
         * Thread num 4 duration = 331
         * Thread num 5 duration = 336
         * Thread num 6 duration = 346
         * Thread num 7 duration = 415
         * Thread num 8 duration = 433
         * Thread num 9 duration = 466
         */
    }
}