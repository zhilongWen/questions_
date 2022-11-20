package com.at.caffeine;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @create 2022-11-19
 */
public class CaffeineParametersTest {


    @Test
    public void test3() {

        /*
            removal（移除）包括 eviction（驱逐：由于策略自动移除）和 invalidation（失效：手动移除）

            removalListener 的操作将会异步执行在一个 Executor 上。
            默认的线程池实现是 ForkJoinPool.commonPool()。当然也可以通过覆盖 Caffeine.executor(Executor) 方法自定义线程池的实现。
            这个 Executor 同时负责 refresh 等操作

         */

        Caffeine
                .newBuilder()
                .removalListener(new RemovalListener<Object, Object>() {
                    @Override
                    public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {

                    }
                })
                .build();

        Caffeine
                .newBuilder()
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                .build();

        /*
            hitRate(): 查询缓存的命中率
            evictionCount(): 被驱逐的缓存数量
            averageLoadPenalty(): 新值被载入的平均耗时
         */
        Caffeine.newBuilder()
                .maximumSize(10_000)
                .recordStats()
                .build();

    }

    @Test
    public void test2() {

        /*

        软引用：内存不够 GC 清除
        弱引用：GC 清除

        key 支持弱引用，而 value 则支持弱引用和软引用。需要注意的是，AsyncCache 不支持软引用和弱引用

        使用了 weakKeys 后，缓存 key 之间的比较将会通过引用相等(==) 而不是对象相等 equals() 去进行原因是 GC 只依赖于引用相等性

        使用了 weakValues 或 softValues 后，value 之间的比较也会通过引用相等(==) 而不是对象相等 equals()

         */

        Caffeine
                .newBuilder()
                .weakKeys()
                .weakValues()
                .build();

        Caffeine.newBuilder()
                .softValues()
                .build();


    }


    @Test
    public void test1() {

        /*

        expireAfterAccess 表示上次读写超过一定时间后过期
        expireAfterWrite 表示上次创建或更新超过一定时间后过期
        expireAfter 允许复杂的表达式，过期时间可以通过 entry 等外部参数确定

        至于过期淘汰的发生，是在写操作以及偶尔发生在读操作中的。过期事件的调度和触发将会在 O(1)的时间复杂度内完成。
        如果希望过期发生的更及时，可以通过在你的 Cache 构造器中通过 Scheduler 接口和 Caffeine.scheduler(Scheduler) 方法去指定一个调度线程代替在缓存活动中去对过期事件进行调度。
        具体地说，在默认情况下，当一个缓存元素过期的时候，Caffeine 不会自动立即将其清理和驱逐。而它将会在写操作之后进行少量的维护工作，在写操作较少的情况下，
        也偶尔会在读操作之后进行。如果你的缓存吞吐量较高，那么你不用去担心你的缓存的过期维护问题。
        但是如果你的缓存读写操作都很少，可以额外通过一个线程使用 Cache.cleanUp() 方法在合适的时候触发清理操作


         */

        Caffeine
                .newBuilder()
//                .expireAfterWrite()
//                .expireAfterWrite()
                .expireAfter(new Expiry<Object, Object>() {
                    @Override
                    public long expireAfterCreate(@NonNull Object key, @NonNull Object value, long currentTime) {
                        return 0;
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull Object key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                        return 0;
                    }

                    @Override
                    public long expireAfterRead(@NonNull Object key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                        return 0;
                    }
                })
                .build();

    }

    @Test
    public void test() {

        Cache<String, Object> cache = Caffeine.newBuilder()
                // 缓存会通过 Window TinyLfu 算法控制整个缓存大小
                .maximumSize(2)
                // 这种方式控制的是总权重。需要 weigher 提供为每个 entry 计算权重的方式。当我们的缓存大小不均匀时，我们可以通过这种方式控制总大小。权重计算是在其创建或更新时发生的，此后其权重值都是静态存在的
                .maximumWeight(2)
                .weigher(new Weigher<Object, Object>() {
                    @Override
                    public @NonNegative int weigh(@NonNull Object key, @NonNull Object value) {
                        return 2;
                    }
                })
                .build();

    }

}
