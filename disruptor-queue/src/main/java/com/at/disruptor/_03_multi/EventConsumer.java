package com.at.disruptor._03_multi;

import com.lmax.disruptor.WorkHandler;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @create 2023-05-07
 */
public class EventConsumer implements WorkHandler<Trade> {

    private String consumerId;

    private Random random = new Random();

    public static final AtomicInteger count = new AtomicInteger(0);

    public EventConsumer(String id) {
        this.consumerId = id;
    }


    @Override
    public void onEvent(Trade event) throws Exception {

        Thread.sleep(random.nextInt(5) * 1);

        System.out.println("当前消费者: " + this.consumerId + ", 消费信息ID: " + event.getId());

        count.incrementAndGet();

    }

    public int getCount(){
        return count.get();
    }

}
