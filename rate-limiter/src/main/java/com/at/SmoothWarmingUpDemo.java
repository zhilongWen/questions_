package com.at;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-08-17
 */
public class SmoothWarmingUpDemo {

    public static void main(String[] args) {

        // 创建一个 SmoothWarmingUp 类型的限流器
        // 每秒产生 10 个令牌
        // 预热 5s
        // 每个令牌产生的时间间隔为 100ms
        // 冷却因子 coldFactor 默认 3.0
        // 冷却时间间隔 coldIntervalMicros = 100 * 1000us * 3 = 300000us
        // 令牌预值 thresholdPermits = 0.5 * 5s / 100ms = 25
        // 最大可存储的令牌数 maxPermits = 25 + 2 * 5s / (100ms + 3.0 * 100ms) = 50
        // 基准线的斜率 slope = (3 * 100ms - 100ms) / (50 - 25) = 8
        RateLimiter rateLimiter = RateLimiter.create(10, 5, TimeUnit.SECONDS);


        double acquire = rateLimiter.acquire(1);


    }


    /**
     * 测试 SmoothWarmingUp 预热效果
     */
    @Test
    public void test1() {

        RateLimiter rateLimiter = RateLimiter.create(10, 1, TimeUnit.SECONDS);

        while (true) {

            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");

        }

        /*
        output；
            get 1 tokens: 0.0s
            get 1 tokens: 0.279377s
            get 1 tokens: 0.238447s
            get 1 tokens: 0.200174s
            get 1 tokens: 0.159555s  大概 1s 后 后续的的令牌获取在 0.1s 左右
            get 1 tokens: 0.119107s
            get 1 tokens: 0.099465s
            get 1 tokens: 0.100301s
            get 1 tokens: 0.099791s
            get 1 tokens: 0.096102s
            get 1 tokens: 0.09895s
         */


    }


    class SmoothWarmingUpTask implements Runnable {

        private CountDownLatch latch;
        private RateLimiter limiter;
        private long start;

        public SmoothWarmingUpTask(CountDownLatch latch, RateLimiter limiter, long start) {
            this.latch = latch;
            this.limiter = limiter;
            this.start = start;
        }

        @Override
        public void run() {
            try {
                //使得线程同时触发
                latch.await();
                System.out.printf("result:" + limiter.acquire(1));
                System.out.println(Thread.currentThread().getName() + ", time " + (System.currentTimeMillis() - start) + "ms");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 测试 SmoothWarmingUp acquire
     */
    @Test
    public void test2() {

        RateLimiter rateLimiter = RateLimiter.create(1, 1, TimeUnit.SECONDS);

        long start = System.currentTimeMillis();

        rateLimiter.acquire(3);

        System.out.println("time cost:" + (System.currentTimeMillis() - start) + " ms");

        ExecutorService service = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {

            service.submit(new SmoothWarmingUpTask(latch, rateLimiter, start));
            latch.countDown();
            System.out.println("countdown:" + latch.getCount());
        }

        System.out.println("countdown over");
        service.shutdown();


    }


}
