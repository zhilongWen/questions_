package com.at.disruptor._01_quick_start;

import com.lmax.disruptor.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @create 2023-05-06
 */
public class OrderEventHandler implements EventHandler<OrderEvent> {

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {

//        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("消费者：" + event.getValue());
    }
}
