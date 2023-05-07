package com.at.disruptor._02_single;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * @author zero
 * @create 2023-05-07
 */
public class TradeEventHandler_1 implements EventHandler<Trade>, WorkHandler<Trade> {

    @Override
    public void onEvent(Trade event) throws Exception {
        System.out.println("handler 1 : set name");
        Thread.sleep(1000);
        event.setName("H1");
    }

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }
}
