package com.at.lfu;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zero
 * @create 2022-12-31
 */


public class LfuCache {

    public static void main(String[] args) {

        LFU<Integer, Integer> lfu = new LFU<>(2);

        lfu.put(1, 1);
        lfu.put(2, 2);

        System.out.println(lfu.get(1));

        lfu.put(3, 8);

        System.out.println(lfu.get(1));
        System.out.println(lfu.get(2));
        System.out.println(lfu.get(3));


    }


}

/**
 * LFU：Least Frequently Used 的缩写，即最不经常最少使用算法
 * https://blog.csdn.net/andrewgithub/article/details/123469157
 * <p>
 * 最少使用频率那么节点肯定要有几个记录频率的变量，因此我们定义一个 FreqNode
 * FreqNode{
 * key;
 * val;
 * freq;
 * }
 * <p>
 * 为了更好的 put、get 我们定义一个双端链表，这样 get、put 时间复杂度都为 O(1)
 * FreqNode{
 * head;
 * tail;
 * size;
 * }
 * <p>
 * get 方法
 * 无论是单链表还是双端链表查找都需要 O(n) 的时间复杂度，因此使用几个 map 来记录当前 key 的 节点 Map<key,FreqNode> keyMap
 * 在该 map 中如果不存在直接返回 null，如果存在则进行后续操作
 * get 操作也要对节点的频率 +1，并且需要将该节点移动到相同频率节点的最前面，查找相同频率的节点同样需要 O(n) 的时间复杂度 freqMap
 * 为此也定义的 Map 用来记录相同频率的节点链表 Map<freq,LinkedListDeque>
 * 操作完后直接返回节点
 * <p>
 * put 方法
 * 通过 keyMap 查看该节点是否已经存在
 * 如果不存在：
 * 不存在的话要先判断容量是否足够，如果不够需要通过 freqMap 删除最小频率的最后一个节点
 * 然后新增该节点，新增操作主要包含两部分 1.将创建的节点添加到 keyMap 中 2.将节点添加到 freqMap 频率相同的链表中
 * 最后结束操作
 * 如果存在：
 * 存在的话就比较简单，但也需要两步操作 1.将节点从 freqMap 频率的链表删除 2.将节点新增到 freqMap 中之前频率 +1 的链表中
 */

class LFU<K, V> {

    int capacity;
    int minFreq;

    Map<K, FreqNode<K, V>> keyMap;
    Map<Integer, LinkedListDeque> freqMap;

    public LFU(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;

        keyMap = new HashMap<K, FreqNode<K, V>>();
        freqMap = new HashMap<Integer, LinkedListDeque>();
    }

    public FreqNode<K, V> get(K key) {

        if (!keyMap.containsKey(key)) return null;

        FreqNode<K, V> oldNode = keyMap.get(key);

        int oldFreq = oldNode.freq;

        // 删除
        freqMap.get(oldFreq).remove(oldNode);
        if (freqMap.get(oldFreq).size == 0) {
            freqMap.remove(oldFreq);
            if (oldFreq == minFreq) minFreq += 1;
        }

        // 添加
        FreqNode<K, V> newNode = new FreqNode<>(oldNode.key, oldNode.val, oldNode.freq + 1);

        keyMap.put(key, newNode);
        LinkedListDeque listDeque = freqMap.getOrDefault(newNode.freq, new LinkedListDeque());
        listDeque.addFirst(newNode);
        freqMap.put(newNode.freq, listDeque);

        return newNode;
    }

    public void put(K key, V val) {

        if (capacity == 0) return;

        if (!keyMap.containsKey(key)) {

            // 新增

            // 容量满了
            if (keyMap.size() == capacity) {
                FreqNode minTail = freqMap.get(minFreq).tail.prev;
                keyMap.remove(minTail.key);
                freqMap.get(minFreq).remove(minTail);
                if (freqMap.get(minFreq).size == 0) {
                    freqMap.remove(minFreq);
                }
            }

            FreqNode<K, V> newNode = new FreqNode<>(key, val, 1);
            keyMap.put(key, newNode);
            LinkedListDeque listDeque = freqMap.getOrDefault(1, new LinkedListDeque());
            listDeque.addFirst(newNode);
            freqMap.put(1, listDeque);

            minFreq = 1;
        } else {

            // 已存在
            FreqNode<K, V> oldNode = keyMap.get(key);

            freqMap.get(oldNode.freq).remove(oldNode);
            if (freqMap.get(oldNode.freq).size == 0) {
                freqMap.remove(oldNode.freq);
                if (minFreq == oldNode.freq) {
                    minFreq += 1;
                }
            }

            FreqNode<K, V> newNode = new FreqNode<>(key, val, oldNode.freq + 1);

            keyMap.put(key, newNode);
            LinkedListDeque listDeque = freqMap.getOrDefault(newNode.freq, new LinkedListDeque());
            listDeque.addFirst(newNode);
            freqMap.put(newNode.freq, listDeque);

        }


    }


}

class LinkedListDeque<K, V> {

    FreqNode<K, V> head, tail;
    int size;

    public LinkedListDeque() {
        head = new FreqNode<K, V>();
        tail = new FreqNode<K, V>();

        head.next = tail;
        tail.prev = head;

        size = 0;
    }

    public void addFirst(FreqNode<K, V> node) {

        FreqNode<K, V> nextNode = head.next;

        node.next = nextNode;
        nextNode.prev = node;

        head.next = node;
        node.prev = head;

        size++;

    }

    public void remove(FreqNode<K, V> node) {

        FreqNode<K, V> prevNode = node.prev;
        FreqNode<K, V> nextNode = node.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        size--;

    }

}

class FreqNode<K, V> {
    K key;
    V val;
    int freq;
    FreqNode<K, V> next, prev;

    public FreqNode(K key, V val, int freq) {
        this.key = key;
        this.val = val;
        this.freq = freq;
    }

    public FreqNode(K key, V val) {
        this(key, val, 1);
    }

    public FreqNode() {
        this(null, null, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreqNode<?, ?> freqNode = (FreqNode<?, ?>) o;
        return freq == freqNode.freq && Objects.equals(key, freqNode.key) && Objects.equals(val, freqNode.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, val, freq, next, prev);
    }

    @Override
    public String toString() {
        return "FreqNode{" +
                "key=" + key +
                ", val=" + val +
                ", freq=" + freq +
                '}';
    }
}