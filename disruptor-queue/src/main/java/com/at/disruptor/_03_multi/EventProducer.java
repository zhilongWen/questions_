package com.at.disruptor._03_multi;

import com.lmax.disruptor.RingBuffer;

/**
 * @create 2023-05-07
 */
public class EventProducer {

    private RingBuffer<Trade> ringBuffer;

    public EventProducer(RingBuffer<Trade> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void put(String id){

        long sequence = ringBuffer.next();

        try {

            Trade trade = ringBuffer.get(sequence);

            trade.setId(id);

        }finally {
            ringBuffer.publish(sequence);
        }

    }

}
