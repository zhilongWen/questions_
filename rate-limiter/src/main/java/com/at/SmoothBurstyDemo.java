package com.at;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-08-16
 */
public class SmoothBurstyDemo {


    public static void main(String[] args) {


        // 每秒产生 5 个 token   -> 平均 0.2s 生成一个 token
        RateLimiter rateLimiter = RateLimiter.create(5);

        // 请求一个令牌 返回获取当前令牌的延时
        double acquire = rateLimiter.acquire(1);

        // 尝试请求一个令牌
        boolean tryAcquire = rateLimiter.tryAcquire(1);

    }


    /**
     * 测试 SmoothBursty 令牌产生速率
     */
    @Test
    public void test1() {

        // 每秒产生 5 个 token   -> 平均 0.2s 生成一个 token
        RateLimiter rateLimiter = RateLimiter.create(5);

        while (true) {

            System.out.println("get 1 tokens: " + rateLimiter.acquire() + "s");  //基本上都是0.2s执行一次

        }

        /*
            output:  基本上都是0.2s执行一次
                get 1 tokens: 0.0s
                get 1 tokens: 0.199301s
                get 1 tokens: 0.199139s
                get 1 tokens: 0.20023s
                get 1 tokens: 0.199287s
                get 1 tokens: 0.199929s
                get 1 tokens: 0.199471s
         */

    }


    /**
     * 测试 SmoothBursty 令牌最大可存储的令牌数（瞬时流量突增，消费storedPermits)
     */
    @Test
    public void test2() {

        // 每秒生成 2 个令牌，最大可存储的令牌 = 2 = 1 * 2
        RateLimiter rateLimiter = RateLimiter.create(2);

        while (true) {


            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");

            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("end");


        }

        /*
            output:
                get 1 tokens: 0.0s                  首次运行 获取到 1个 令牌，期待的下一次令牌获取时间为 0.5s 后
                get 1 tokens: 0.0s                  如果没有 sleep 2s 该令牌的获取应消耗 0.5s 左右，由于睡眠了 2s，又因为 SmoothBursty 最大存储 2个 令牌，所以
                                                        随眠结束的那一刻，storedPermits 中有两个令牌，storedPermits 中的令牌将由 当前 acquire 和下一个 acquire 消耗，并同时开始生产新的令牌
                get 1 tokens: 0.0s
                get 1 tokens: 0.0s                  新产生的令牌，期待下一个令牌获取在 0.5s 左右
                get 1 tokens: 0.49992s
                get 1 tokens: 0.497888s
                end
                get 1 tokens: 0.500394s
                get 1 tokens: 0.0s
                get 1 tokens: 0.0s
                get 1 tokens: 0.0s
                get 1 tokens: 0.499865s
                get 1 tokens: 0.496256s
                end
                get 1 tokens: 0.499565s
                get 1 tokens: 0.0s
                get 1 tokens: 0.0s
                get 1 tokens: 0.0s
                get 1 tokens: 0.499871s
                get 1 tokens: 0.498076s
                end
         */


    }


    /**
     * 测试 SmoothBursty 消费预期（瞬时流量突增，提前消费，后面的来偿还)
     */
    @Test
    public void test3() {

        // 每秒产生 5 个令牌，最大存储令牌数为 5 ，令牌的产生时间间隔为 0.2s
        RateLimiter rateLimiter = RateLimiter.create(5);

        while (true) {

            System.out.println("get 1 tokens: " + rateLimiter.acquire(4) + "s");

            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");

            System.out.println("end");

        }

        /*
            output；
                get 1 tokens: 0.0s                 如果该 acquire 需要的是一个令牌，则下一个令牌的产生大概在 0.2s，但是此处 一次获取了 4 个令牌，
                                                    则下一个令牌的期望获取时间为=nextFreeTicketMicros+stableIntervalMicros*令牌数=0 + 0.2 * 4=0.8s 所以大概是 0.8s 才能获取到下一个令牌
                get 1 tokens: 0.799356s
                end
                get 1 tokens: 0.199772s
                get 1 tokens: 0.796951s
                end
                get 1 tokens: 0.19975s
                get 1 tokens: 0.797613s
                end
                get 1 tokens: 0.199211s
                get 1 tokens: 0.79907s
                end
                get 1 tokens: 0.199802s
                get 1 tokens: 0.796738s
         */


    }


    class SmoothBurstyTask implements Runnable {
        private CountDownLatch latch;
        private RateLimiter limiter;

        public SmoothBurstyTask(CountDownLatch latch, RateLimiter limiter) {
            this.latch = latch;
            this.limiter = limiter;
        }

        @Override
        public void run() {
            try {
                // 阻塞 使得线程同时触发
                latch.await();
                System.out.println("time " + System.currentTimeMillis() + "ms :" + limiter.tryAcquire());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * tryAcquire 多线程测试
     */
    @Test
    public void test4() {

        // 每秒产生 5 个令牌，每个令牌产生的时间间隔 200ms，最大可存储5个令牌
        RateLimiter r = RateLimiter.create(5);
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {

            service.submit(new SmoothBurstyTask(latch, r));
            latch.countDown();
            System.out.println("countdown:" + latch.getCount());
        }
        System.out.println("countdown over");
        service.shutdown();
    }


}
