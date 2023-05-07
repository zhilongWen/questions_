package com.at.disruptor._02_single;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zero
 * @create 2023-05-07
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(1);


        // 1.创建 Disruptor
        ExecutorService pool = Executors.newFixedThreadPool(5);
        Disruptor<Trade> disruptor = new Disruptor<>(
                new EventFactory<Trade>() {
                    @Override
                    public Trade newInstance() {
                        return new Trade();
                    }
                },
                1024,
                pool,
                ProducerType.SINGLE,
                new BusySpinWaitStrategy()
        );

        // 2.将消费者设置到 Disruptor 中



        // 串行操作
//        disruptor
//                .handleEventsWith(new TradeEventHandler_1())
//                .handleEventsWith(new TradeEventHandler_2())
//                .handleEventsWith(new TradeEventHandler_3());


        // 并行操作
//        disruptor.handleEventsWith(new TradeEventHandler_1());
//        disruptor.handleEventsWith(new TradeEventHandler_2());
//        disruptor.handleEventsWith(new TradeEventHandler_3());
//        disruptor.handleEventsWith(new TradeEventHandler_1(),new TradeEventHandler_2(),new TradeEventHandler_3());


        // 菱形操作
        // 1 2 并行最后 1 2 的操作合并一起给 3
//        disruptor.handleEventsWith(new TradeEventHandler_1(),new TradeEventHandler_2())
//                .handleEventsWith(new TradeEventHandler_3());

//        EventHandlerGroup<Trade> eventHandlerGroup = disruptor.handleEventsWith(new TradeEventHandler_1(), new TradeEventHandler_2());
//        eventHandlerGroup.then(new TradeEventHandler_3());


        // 六边形操作
        // 1 2 并行
        // 4 5 并行
        // 两个并行的结果给 3
        TradeEventHandler_1 h1 = new TradeEventHandler_1();
        TradeEventHandler_2 h2 = new TradeEventHandler_2();
        TradeEventHandler_3 h3 = new TradeEventHandler_3();
        TradeEventHandler_4 h4 = new TradeEventHandler_4();
        TradeEventHandler_5 h5 = new TradeEventHandler_5();

        /*
            ExecutorService pool = Executors.newFixedThreadPool(4);
                对于单生产者模式，handler 的处理个数必须小于等于给 disruptor 设置的线程数
                因为单生产者模式使用的是 BatchEventProcessor ，而 BatchEventProcessor 实现 runnable 接口
                这就意味着没创建一个 handler 就需要持有一个 thread
         */

        disruptor.handleEventsWith(h1,h4);
        disruptor.after(h1).handleEventsWith(h2);
        disruptor.after(h4).handleEventsWith(h5)
                .handleEventsWith(h3);


        // 3.启动 Disruptor
        RingBuffer<Trade> ringBuffer = disruptor.start();

        CountDownLatch latch = new CountDownLatch(1);

        long begin = System.currentTimeMillis();

        executorService.submit(new TradePushlisher(latch,disruptor));

        latch.await();

        disruptor.shutdown();
        pool.shutdown();
        executorService.shutdown();

        System.err.println("总耗时: " + (System.currentTimeMillis() - begin));







    }

}
