package com.at;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

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


    @Test
    public void test1(){

        // 每秒产生 5 个 token   -> 平均 0.2s 生成一个 token
        RateLimiter rateLimiter = RateLimiter.create(5);

        while (true){

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


    @Test
    public void test2(){

        RateLimiter rateLimiter = RateLimiter.create(2);

        while (true){


            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");

            try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }

            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("get 1 tokens: " + rateLimiter.acquire(1) + "s");
            System.out.println("end");


        }

    }



    @Test
    public void test3() throws Exception{

        RateLimiter rateLimiter = RateLimiter.create(1);

        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " get 1 tokens: " + rateLimiter.acquire(1) + "s");
            },String.valueOf(i)).start();
        }

        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }



//        RateLimiter.create()

    }



}
