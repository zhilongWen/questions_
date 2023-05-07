//package com.at.disruptor.test;
//
//import com.lmax.disruptor.BlockingWaitStrategy;
//import com.lmax.disruptor.EventFactory;
//import com.lmax.disruptor.EventHandler;
//import com.lmax.disruptor.RingBuffer;
//import com.lmax.disruptor.dsl.Disruptor;
//import com.lmax.disruptor.dsl.ProducerType;
//
//import java.util.concurrent.ThreadFactory;
//
///**
// *
// * 每10ms向disruptor中插入一个元素，消费者读取数据，并打印到终端。详细逻辑请细读代码
// *
// * @author zero
// * @create 2022-10-16
// */
//public class DisruptorMain {
//
//    public static void main(String[] args) throws InterruptedException {
//
//        /**
//         * 队列中的元素
//         */
//        class Element {
//            private int value;
//
//            public int getValue() {
//                return value;
//            }
//
//            public void setValue(int value) {
//                this.value = value;
//            }
//        }
//
//
//        // 生产者的线程工厂
//        ThreadFactory threadFactory = new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "simpleThread");
//            }
//        };
//
//        // RingBuffer生产工厂,初始化RingBuffer的时候使用
//        EventFactory<Element> eventFactory = new EventFactory<Element>() {
//            @Override
//            public Element newInstance() {
//                return new Element();
//            }
//        };
//
//        // 处理Event的handler
//        EventHandler<Element> eventHandler = new EventHandler<Element>() {
//            @Override
//            public void onEvent(Element element, long l, boolean b) throws Exception {
//                System.out.println("Element: " + element.getValue());
//            }
//        };
//
//
//        // 阻塞策略
//        BlockingWaitStrategy waitStrategy = new BlockingWaitStrategy();
//
//        // 指定RingBuffer的大小
//        int bufferSize = 16;
//
//        // 创建disruptor，采用单生产者模式
//        Disruptor<Element> disruptor = new Disruptor<>(eventFactory, bufferSize, threadFactory, ProducerType.SINGLE, waitStrategy);
//
//        // 设置EventHandler
//        disruptor.handleEventsWith(eventHandler);
//
//        // 启动disruptor的线程
//        disruptor.start();
//
//        RingBuffer<Element> ringBuffer = disruptor.getRingBuffer();
//
//        for (int i = 0; true ; i++) {
//
//            // 获取下一个可用位置的下标
//            long sequence  = ringBuffer.next();
//            try {
//                // 返回可用位置的元素
//                Element event  = ringBuffer.get(sequence);
//                // 设置该位置元素的值
//                event.setValue(1);
//            }finally {
//                ringBuffer.publish(sequence);
//            }
//
//            Thread.sleep(10);
//
//
//        }
//
//
//    }
//
//}
