package com.at.disruptor._01_quick_start;


import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zero
 * @create 2023-05-06
 */
public class Main {

    public static void main(String[] args) {

        // 创建事件工厂，用于创建 event
        OrderEventFactory factory = new OrderEventFactory();

        // 核心参数
        int ringBufferSize = 1024;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        /**
         * 1 eventFactory: 消息(event)工厂对象
         * 2 ringBufferSize: 容器的长度
         * 3 executor: 线程池(建议使用自定义线程池) RejectedExecutionHandler
         * 4 ProducerType: 单生产者 还是 多生产者
         * 5 waitStrategy: 等待策略
         */
        // 实例化disruptor对象
        Disruptor<OrderEvent> disruptor = new Disruptor<>(
                factory,
                ringBufferSize,
                pool,
                ProducerType.SINGLE,
                new BlockingWaitStrategy() // 堵塞策略
        );

        // 添加消费者的监听 (构建disruptor 与 消费者的一个关联关系)
        disruptor.handleEventsWith(new OrderEventHandler());

        // 启动disruptor
        disruptor.start();

        // 获取实际存储数据的容器: RingBuffer
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();

        OrderEventProducer producer = new OrderEventProducer(ringBuffer);

        ByteBuffer buffer = ByteBuffer.allocate(8);

        for (int i = 0; i < 10; i++) {
            buffer.putLong(0, i);
            producer.put(buffer);
        }

        disruptor.shutdown();
        pool.shutdown();


    }

}
