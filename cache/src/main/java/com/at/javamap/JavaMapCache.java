package com.at.javamap;

import java.util.HashMap;

/**
 * @author zero
 * @create 2022-11-20
 */
public class JavaMapCache {

    public static final CacheMapper<String, Object> cache = new CacheMapper<String, Object>(16);

    public static void main(String[] args) {

        Object a = cache.getIfAbsent("a");

        cache.put("a", "123");

    }


    /**
     * HashMap 无法进行数据淘汰，内存会无限制的增长
     * @param <K>
     * @param <V>
     */
    static class CacheMapper<K, V> {

        private int size;
        private HashMap<K, V> map;


        public CacheMapper(int size) {
            this.size = size;
            map = new HashMap<K, V>(size);
        }


        public V getIfAbsent(K key) {
            return map.getOrDefault(key, null);
        }

        public void put(K key, V val) {
            this.map.put(key, val);
        }

    }


}
