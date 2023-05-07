package com.at.disruptor._03_multi;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;


/**
 * @create 2023-05-07
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // 1.创建 RingBuffer
        RingBuffer<Trade> ringBuffer = RingBuffer
                .<Trade>create(
                        ProducerType.MULTI,
                        new EventFactory<Trade>() {
                            @Override
                            public Trade newInstance() {
                                return new Trade();
                            }
                        },
                        1024,
                        new YieldingWaitStrategy()
                );

        // 2.通过 RingBuffer 创建一个 屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();


        // 3.创建消费者组
        EventConsumer[] consumers = new EventConsumer[10];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new EventConsumer("consumer_" + i);
        }

        // 4.创建消费者工作池
        WorkerPool<Trade> workerPool = new WorkerPool<>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumers
        );

        // 5.设置多个消费者的 sequence
        // 为什么消费者的 sequence 需要让 RingBuffer 感知？？？
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());


        // 6.启动消费者工作池
        workerPool.start(Executors.newFixedThreadPool(10));


        // 7.生产者生产消息
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        for (int i = 0; i < 100; i++) {
//
//            final EventProducer producer = new EventProducer(ringBuffer);
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        latch.await();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("Thread name = " + Thread.currentThread().getName() + " 开始生产消息");
//                    for (int j = 0; j < 100; j++) {
//                        producer.put(UUID.randomUUID().toString());
//                    }
//                }
//            }).start();
//
//        }
//
//        Thread.sleep(2000);
//        System.out.println("----------线程创建完毕，开始生产数据----------");
//        latch.countDown();
//
//        Thread.sleep(10000);


        CyclicBarrier cyclicBarrier = new CyclicBarrier(100, () -> System.out.println("----------线程创建完毕，开始生产数据----------"));

        for (int i = 0; i < 100; i++) {

            final EventProducer producer = new EventProducer(ringBuffer);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Thread name = " + Thread.currentThread().getName() + " 开始生产消息");
                    for (int j = 0; j < 100; j++) {
                        producer.put(UUID.randomUUID().toString());
                    }
                }
            }).start();

        }

        Thread.sleep(6000);

        System.out.println("任务总数:" + consumers[2].getCount());


        // 8.关闭资源


    }

    static class EventExceptionHandler implements ExceptionHandler<Trade> {
        @Override
        public void handleEventException(Throwable ex, long sequence, Trade event) {
        }

        @Override
        public void handleOnStartException(Throwable ex) {
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
        }
    }

}
