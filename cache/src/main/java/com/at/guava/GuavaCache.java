package com.at.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @create 2022-11-20
 */
public class GuavaCache {

    public static void main(String[] args) throws ExecutionException {

        LoadingCache<String, String> cache = CacheBuilder
                .newBuilder()
                .maximumSize(10)
                //写之后30ms过期
                .expireAfterWrite(30L, TimeUnit.MILLISECONDS)
                //访问之后30ms过期
                .expireAfterAccess(30L, TimeUnit.MILLISECONDS)
                //20ms之后刷新
                .refreshAfterWrite(20L, TimeUnit.MILLISECONDS)
                //开启weakKey key 当启动垃圾回收时，该缓存也被回收
                .weakKeys()
                .<String, String>build(createCacheLoader());


        System.out.println(cache.get("hello"));
        cache.put("hello1", "我是hello1");
        System.out.println(cache.get("hello1"));
        cache.put("hello1", "我是hello2");
        System.out.println(cache.get("hello1"));


    }

    public static com.google.common.cache.CacheLoader<String, String> createCacheLoader() {
        return new com.google.common.cache.CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                return key;
            }
        };
    }


}
