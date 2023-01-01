package com.at.caffeine;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @create 2023-01-01
 */
public class CaffeineCacheTest {


    /**
     * 测试 refreshAfterWrite 缓存更新策略
     */
    @Test
    public void refreshAfterWriteTest() {

        Random random = new Random();

        Cache<String, Integer> cache = Caffeine
                .newBuilder()
                // 指定在建立缓存或者最近一次更新缓存后通过固定的时间间隔，刷新缓存
                .refreshAfterWrite(5,TimeUnit.SECONDS)
                .maximumSize(10)
                .removalListener((key, val, cause) -> {
                    System.out.println("{kye = " + key + " value = " + val + "} 被移除，原因：" + cause);
                })
                .<String, Integer>build(new CacheLoader<String, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull String key) throws Exception {
                        return random.nextInt();
                    }
                });



        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        while (true) {

            System.out.println("=============================");

            System.out.println("a: " + cache.getIfPresent("a"));
            System.out.println("b: " + cache.getIfPresent("b"));
            System.out.println("c: " + cache.getIfPresent("c"));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



    }

    /**
     * expireAfter: 自定义策略
     */
    @Test
    public void expireAfterTest(){

        Cache<String, Integer> cache = Caffeine
                .newBuilder()
                .maximumSize(10)
                .expireAfter(new Expiry<String, Integer>() {
                    @Override
                    public long expireAfterCreate(@NonNull String key, @NonNull Integer value, long currentTime) {
                        // Use wall clock time, rather than nanotime, if from an external resource
                        long expected;

                        if ("a".equals(key)){
                            expected = TimeUnit.SECONDS.toNanos(2);
                        }else {
                            expected = TimeUnit.SECONDS.toNanos(5);
                        }

                        return expected;
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull String key, @NonNull Integer value, long currentTime, @NonNegative long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(@NonNull String key, @NonNull Integer value, long currentTime, @NonNegative long currentDuration) {
                        return currentDuration;
                    }
                })
                .removalListener((key, val, cause) -> {
                    System.out.println("{kye = " + key + " value = " + val + "} 被移除，原因：" + cause);
                })
                .<String, Integer>build();

        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        while (true) {

            System.out.println("=============================");

            System.out.println("a: " + cache.getIfPresent("a"));
            System.out.println("b: " + cache.getIfPresent("b"));
            System.out.println("c: " + cache.getIfPresent("c"));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



    }


    /**
     *  测试 expireAfterAccess 过期策略：默认配置一直使用则不清理只有在 get 缓存的时候才会清除缓存
     */
    @Test
    public void expireAfterAccessTest() {

        Random random = new Random();

        Cache<String, Integer> cache = Caffeine
                .newBuilder()
                // 默认配置一直使用则不清理只有在 get 缓存的时候才会清除缓存
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .maximumSize(10)
                .removalListener((key, val, cause) -> {
                    System.out.println("{kye = " + key + " value = " + val + "} 被移除，原因：" + cause);
                })
                .<String, Integer>build();



        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        // 测试完下面两个再加
        // 启动一个定时任务每 2s 更新一次缓存
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());
        scheduledExecutor.scheduleAtFixedRate(() -> {
            cache.put("a", random.nextInt(100));
            cache.put("b", random.nextInt(100));
            cache.put("c", random.nextInt(100));
        },3,3,TimeUnit.SECONDS);

        // 一直可以获取到缓存
//        while (true) {
//
//            System.out.println("=============================");
//
//            System.out.println("a: " + cache.getIfPresent("a"));
//            System.out.println("b: " + cache.getIfPresent("b"));
//            System.out.println("c: " + cache.getIfPresent("c"));
//
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }



        int i = 1;
        while (true) {

            System.out.println("=============================");

            // 1 2 3 4 5 6 7 打印
            // 8 9 10 11 12 不打印 -> 5 秒已过清除缓存
            // 后续为 null
            if (i <= 7 || i > 13) {
                System.out.println("a: " + cache.getIfPresent("a"));
            }
            System.out.println("b: " + cache.getIfPresent("b"));
            System.out.println("c: " + cache.getIfPresent("c"));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;

        }



    }


    /**
     * 测试 expireAfterWrite 过期策略：写入后 n 秒自动删除
     */
    @Test
    public void expireAfterWriteTest() {

        Cache<String, Integer> cache = Caffeine
                .newBuilder()
                // 写入后 5s 自动删除
                .expireAfterWrite(5, TimeUnit.SECONDS)
                // 最大容量 10 个，超过会自动清理空间
                .maximumSize(10)
                .removalListener((key, val, cause) -> {
                    System.out.println("{kye = " + key + " value = " + val + "} 被移除，原因：" + cause);
                })
                .<String, Integer>build();

        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        while (true) {

            System.out.println("=============================");

            System.out.println("a: " + cache.getIfPresent("a"));
            System.out.println("b: " + cache.getIfPresent("b"));
            System.out.println("c: " + cache.getIfPresent("c"));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }


    @Test
    public void test(){

//        Cache<String, Integer> cache = Caffeine
//                .newBuilder()
//                // 最大容量 10 个，超过会自动清理空间
//                .maximumSize(10)
//                .removalListener((key, val, cause) -> {
//                    System.out.println("{kye = " + key + " value = " + val + "} 被移除，原因：" + cause);
//                })
//                .<String, Integer>build();
//
//        for (int i = 0; i < 20; i++) {
//            cache.put(String.valueOf(i),i);
//            System.out.println(cache.getIfPresent(String.valueOf(i)));
//        }


        Cache<String, Integer> cache = Caffeine
                .newBuilder()
                // 最大容量 10 个，超过会自动清理空间
                .maximumWeight(10_100)
                .weigher(new Weigher<Object, Object>() {
                    @Override
                    public @NonNegative int weigh(@NonNull Object key, @NonNull Object value) {
                        return Objects.hash(key);
                    }
                })
                .removalListener((key, val, cause) -> {
                    System.out.println("{kye = " + key + " value = " + val + "} 被移除，原因：" + cause);
                })
                .<String, Integer>build();

        for (int i = 0; i < 20; i++) {
            cache.put(String.valueOf(i),i);
            System.out.println(cache.getIfPresent(String.valueOf(i)) + "\t hash = " + Objects.hash(String.valueOf(i)));
        }

    }

}
