package com.at.disruptor._03_multi;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @create 2023-05-07
 */
public class UU {
    public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> System.out.println("召唤神龙"));

        for (int i = 0; i < 7; i++) {

            int finalI = i;

            new Thread(() -> {

                System.out.println("收集到第：" + (finalI + 1) + " 颗龙珠");

                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }


            }, String.valueOf(i)).start();

        }


    }
}
