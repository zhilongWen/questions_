package com.at.disruptor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zero
 * @create 2022-10-16
 */
public class Accumulator {

    private AtomicBoolean flag = new AtomicBoolean(false);

    private static final int count = 10000 * 50000;

    private long accumulator;

    public void add(){
        accumulator+=1;
    }

    public static void main(String[] args) {

        final Accumulator accumulator = new Accumulator();

        accumulator.singleThread();
        accumulator.singleThreadCAS();
        accumulator.singleThreadAddLock();
    }


    /**
     * 单线程
     */
    public void singleThread() {

        long sT = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            add();
        }
        System.out.println("单线程：" + (System.currentTimeMillis() - sT));

    }

    private AtomicLong singleThreadCAS = new AtomicLong(0L);

    /**
     * 单线程 CAS
     */
    public void singleThreadCAS(){

        long sT = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            singleThreadCAS.getAndIncrement();
        }

        System.out.println("单线程 CAS：" + (System.currentTimeMillis() - sT));

    }

    /**
     * 单线程加锁
     */
    public synchronized void singleThreadAddLock(){
        long sT = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            add();
        }
        System.out.println("单线程加锁：" + (System.currentTimeMillis() - sT));
    }




}
