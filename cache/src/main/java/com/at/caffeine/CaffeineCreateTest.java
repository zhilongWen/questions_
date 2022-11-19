package com.at.caffeine;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * @author zero
 * @create 2022-11-19
 */
public class CaffeineCreateTest {


    @Test
    public void test4() {

        AsyncLoadingCache<Object, Object> asyncCache = Caffeine
                .newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .maximumSize(2)
                //选择: 去异步的封装一段同步操作来生成缓存元素
                //.buildAsync(k -> "k async");

                //选择: 构建一个异步缓存元素操作并返回一个future
                .buildAsync(new AsyncCacheLoader<Object, Object>() {
                    @Override
                    public @NonNull CompletableFuture<Object> asyncLoad(@NonNull Object key, @NonNull Executor executor) {
                        return CompletableFuture.supplyAsync(() -> "pppp");
                    }
                });

        CompletableFuture<Object> k1 = asyncCache.get("k1");

        System.out.println(k1.join());


    }

    /**
     * AsyncCache 手动异步加载
     */
    @Test
    public void test3() {

        AsyncCache<String, Object> asyncCache = Caffeine
                .newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .maximumSize(2)
                .<String, Object>buildAsync();

        asyncCache.put("k", CompletableFuture.supplyAsync(() -> "test async"));


        // 查找缓存元素，如果不存在，则异步生成
        CompletableFuture<Object> v = asyncCache.get("k1", new BiFunction<String, Executor, CompletableFuture<Object>>() {
            @Override
            public CompletableFuture<Object> apply(String s, Executor executor) {
                return CompletableFuture.supplyAsync(() -> "k1 async");
            }
        });

        System.out.println(v.join());

    }

    /**
     * LoadingCache 自动加载
     */
    @Test
    public void test2() {

        LoadingCache<String, String> cache = Caffeine
                .newBuilder()
                .maximumSize(2)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .<String, String>build(key -> "e:" + key);

        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        String v = cache.get("k");
        System.out.println("v = " + v); // v = e:k

        cache.put("k1", "v1");
        cache.put("k2", "v2");
        cache.put("k3", "v3");

        System.out.println(cache.get("k1"));

        // 批量查找缓存，如果缓存不存在则生成缓存元素
        Map<@NonNull String, @NonNull String> allPresent = cache.getAllPresent(Arrays.asList("k1", "k2", "k3", "k", "pp"));

//        k1 ---> v1
//        k2 ---> v2
//        k3 ---> v3
//        k ---> e:k
        allPresent.forEach((key, val) -> System.out.println(key + " ---> " + val));

        // e:pp
        System.out.println(cache.get("pp"));


    }

    /**
     * Cache 手动加载
     */
    @Test
    public void test1() {

        Cache<String, Object> cache = Caffeine
                .newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .maximumSize(2L)
                .<String, Object>build();

        // 查找一个缓存元素， 没有查找到的时候返回null
        Object v = cache.getIfPresent("k");
        System.out.println("v = " + v);

        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        v = cache.get("k", k -> "vk");
        System.out.println("v = " + v);

        // 添加或者更新一个缓存元素
        cache.put("k", "k1");
        System.out.println("v = " + cache.getIfPresent("k"));

        // 移除一个缓存元素
        cache.invalidate("k");
        System.out.println("v = " + cache.getIfPresent("k"));


        // 使用 Cache.asMap() 所暴露出来的 ConcurrentMap 的方法直接对缓存进行操作
        ConcurrentMap<@NonNull String, @NonNull Object> concurrentMap = cache.asMap();

    }

}
