package com.at.lru;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zero
 * @create 2022-11-20
 */
public class LRUCache {

    // Https://leetcode-cn.com/problems/lru-cache/

    public static void main(String[] args) {

//        LRUUseLinkedHashMap lruCacheDemo = new LRUUseLinkedHashMap(3);
//
//        lruCacheDemo.put(1,"a");
//        lruCacheDemo.put(2,"b");
//        lruCacheDemo.put(3,"c");
//        System.out.println(lruCacheDemo.keySet());
//
//        lruCacheDemo.put(4,"d");
//        System.out.println(lruCacheDemo.keySet());
//
//        lruCacheDemo.put(3,"c");
//        System.out.println(lruCacheDemo.keySet());
//        lruCacheDemo.put(3,"c");
//        System.out.println(lruCacheDemo.keySet());
//        lruCacheDemo.put(3,"c");
//        System.out.println(lruCacheDemo.keySet());
//        lruCacheDemo.put(5,"x");
//        System.out.println(lruCacheDemo.keySet());

        LRU lruCacheDemo = new LRU(3);

        lruCacheDemo.put(1, 1);
        lruCacheDemo.put(2, 2);
        lruCacheDemo.put(3, 3);
        System.out.println(lruCacheDemo.map.keySet());

        lruCacheDemo.put(4, 1);
        System.out.println(lruCacheDemo.map.keySet());

        lruCacheDemo.put(3, 1);
        System.out.println(lruCacheDemo.map.keySet());
        lruCacheDemo.put(3, 1);
        System.out.println(lruCacheDemo.map.keySet());
        lruCacheDemo.put(3, 1);
        System.out.println(lruCacheDemo.map.keySet());
        lruCacheDemo.put(5, 1);
        System.out.println(lruCacheDemo.map.keySet());

        /**
         * true
         * [1, 2, 3]
         * [2, 3, 4]
         * [2, 4, 3]
         * [2, 4, 3]
         * [2, 4, 3]
         * [4, 3, 5]
         * */

        /**
         [1, 2, 3]
         [2, 3, 4]
         [2, 3, 4]
         [2, 3, 4]
         [2, 3, 4]
         [3, 4, 5]
         */


    }


    /**
     *
     */
    static class LRU {

        // map 负责查找，构建一个虚拟的双向链表，它里面安装的就是一个个 Node 节点，作为数据载体。

        //1.构造一个node节点作为数据载体
        class Node<K, V> {
            K key;
            V value;
            Node<K, V> prev;
            Node<K, V> next;

            public Node() {
                this.prev = this.next = null;
            }

            public Node(K key, V value) {
                this.key = key;
                this.value = value;
                this.prev = this.next = null;
            }

        }

        //2 构建一个虚拟的双向链表,,里面安放的就是我们的 Node
        class LinkedListDeque<K, V> {
            Node<K, V> head;
            Node<K, V> tail;

            public LinkedListDeque() {
                head = new Node<>();
                tail = new Node<>();
                head.next = tail;
                tail.prev = head;
            }

            //3. 添加到头
            public void addHead(Node<K, V> node) {
                node.next = head.next;
                node.prev = head;
                head.next.prev = node;
                head.next = node;
            }

            //4.删除节点
            public void removeNode(Node<K, V> node) {
                node.next.prev = node.prev;
                node.prev.next = node.next;
                node.prev = null;
                node.next = null;
            }

            //5.获得最后一个节点
            public Node getLast() {
                return tail.prev;
            }


        }

        private int cacheSize;
        Map<Integer, Node<Integer, Integer>> map;
        LinkedListDeque<Integer, Integer> linkedListDeque;

        public LRU(int cacheSize) {
            this.cacheSize = cacheSize;//坑位
            map = new HashMap<>();//查找
            linkedListDeque = new LinkedListDeque<>();
        }

        public int get(int key) {
            if (!map.containsKey(key)) {
                return -1;
            }

            Node<Integer, Integer> node = map.get(key);
            linkedListDeque.removeNode(node);
            linkedListDeque.addHead(node);

            return node.value;
        }

        public void put(int key, int value) {
            if (map.containsKey(key)) {  //update
                Node<Integer, Integer> node = map.get(key);
                node.value = value;
                map.put(key, node);

                linkedListDeque.removeNode(node);
                linkedListDeque.addHead(node);
            } else {
                if (map.size() == cacheSize)  //坑位满了
                {
                    Node<Integer, Integer> lastNode = linkedListDeque.getLast();
                    map.remove(lastNode.key);
                    linkedListDeque.removeNode(lastNode);
                }

                //新增一个
                Node<Integer, Integer> newNode = new Node<>(key, value);
                map.put(key, newNode);
                linkedListDeque.addHead(newNode);

            }
        }


    }


    /**
     * @param <K>
     * @param <V>
     */
    static class LRUUseLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

        private int capacity;//缓存坑位

        private Object lock = new Object();

        public LRUUseLinkedHashMap(int capacity) {
            super(capacity, 0.75F, false);
            this.capacity = capacity;
        }

        /**
         * 重写 LinkedHashMap 的 removeEldestEntry 方法
         *
         * @param eldest
         * @return
         */
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return super.size() > capacity;
        }

        public V getVal(Object key) {
            return get(key);
        }

        public void putVal(K key, V value) {
            synchronized (lock) {
                put(key, value);
            }
        }

        public boolean removeValue(Object key) {
            synchronized (lock) {
                return remove(key) != null;
            }
        }

        public boolean removeAll() {
            clear();
            return true;
        }


    }

}
