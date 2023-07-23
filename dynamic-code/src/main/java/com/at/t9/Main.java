package com.at.t9;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @create 2023-07-23
 */
public class Main {

    // https://cloud.tencent.com/developer/article/2013675
    // https://blog.csdn.net/chy2z/article/details/106266566

    public static void main(String[] args) {


        System.out.println("=======>主线程启动<=======");
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("loader-pool-%d").build();

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, namedThreadFactory);

        executor.scheduleAtFixedRate(() -> {
            Date now = new Date();
            System.out.println();
            System.out.println(now + "=======>定时任务开始执行<=======");
            try {
                LoadExtJarFactory.getClass("com.sheep.jar.Hellow");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(now + "=======>定时任务结束<=======");
        }, 3, 10, TimeUnit.SECONDS);





        while (true) {
            // 保持主线程不断
        }

    }

}
