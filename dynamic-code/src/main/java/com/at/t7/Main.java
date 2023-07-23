package com.at.t7;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @create 2023-07-23
 */
public class Main {

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
                List<Object> objects = CustomLoader.getInstance().loadExtJar("C:\\Users\\zero\\Videos\\jar");
                Object o = objects.get(0);
                Method say = o.getClass().getMethod("say", String.class);
                say.invoke(o, " 00000");
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
