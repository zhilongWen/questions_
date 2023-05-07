package com.at.disruptor._02_single;

import com.lmax.disruptor.EventHandler;

import java.util.UUID;

/**
 * @author zero
 * @create 2023-05-07
 */
public class TradeEventHandler_2 implements EventHandler<Trade>{

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("handler 2 : set id");
        Thread.sleep(500);
        event.setId(UUID.randomUUID().toString());
    }

}