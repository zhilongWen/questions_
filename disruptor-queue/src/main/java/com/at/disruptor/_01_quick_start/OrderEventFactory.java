package com.at.disruptor._01_quick_start;

import com.lmax.disruptor.EventFactory;

/**
 * @author zero
 * @create 2023-05-06
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {

    /**
     * 这个方法就是为了返回空的数据对象（Event）
     * @return
     */
    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}
