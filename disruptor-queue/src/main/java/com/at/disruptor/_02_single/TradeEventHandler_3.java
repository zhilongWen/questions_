package com.at.disruptor._02_single;

import com.lmax.disruptor.EventHandler;

/**
 * @author zero
 * @create 2023-05-07
 */
public class TradeEventHandler_3 implements EventHandler<Trade>{

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("handler 3 : toString --> + " + event.toString());
    }

}