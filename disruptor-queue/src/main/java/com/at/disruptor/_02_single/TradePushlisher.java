package com.at.disruptor._02_single;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @create 2023-05-07
 */
public class TradePushlisher implements Runnable {

    private CountDownLatch latch;
    private Disruptor<Trade> disruptor;

    private static int PUBLISH_COUNT = 1;

    public TradePushlisher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.latch = latch;
        this.disruptor = disruptor;
    }

    @Override
    public void run() {

        TradeEventTranslator eventTranslator = new TradeEventTranslator();

        for (int i = 0; i < PUBLISH_COUNT; i++) {
            disruptor.publishEvent(eventTranslator);
        }

        latch.countDown();

    }
}

class TradeEventTranslator implements EventTranslator<Trade> {

    private Random random = new Random();

    @Override
    public void translateTo(Trade event, long sequence) {
        this.generateTrade(event);
    }

    private void generateTrade(Trade event) {
        event.setPrice(random.nextDouble() * 9999);
    }

}



