/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.at.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

// 预热配置：预热 1 次，每次 1 秒
@Warmup(iterations = 1,time = 1,timeUnit = TimeUnit.SECONDS)
// 测试配置：测试 1 次，每次 1 秒
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
public class JMHSample_02_BenchmarkModes {


    /**
     * 吞吐量测试最终报告
     * Benchmark                                       Mode  Cnt  Score   Error  Units
     * JMHSample_02_BenchmarkModes.measureThroughput  thrpt    5  9.201 ± 0.041  ops/s
     *
     * @throws InterruptedException
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput) // 吞吐量测试，输出报告：每单位时间该方法执行多少次
    @OutputTimeUnit(TimeUnit.SECONDS) // 输出报告时间单位
    public void measureThroughput() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }


    /**
     * 平均耗时测试最终报告
     * Benchmark                                   Mode  Cnt       Score     Error  Units
     * JMHSample_02_BenchmarkModes.measureAvgTime  avgt    5  108782.568 ± 661.738  us/op
     *
     * @throws InterruptedException
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime) // 平均耗时测试，输出报告：每次操作耗时
    @OutputTimeUnit(TimeUnit.MICROSECONDS) // 耗时时间单位
    public void measureAvgTime() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    /**
     * 采样测试最终报告 0 分位，50 分位，90 分位，95/99/999/9999/1 分位 抽样结果
     * Benchmark                                                            Mode  Cnt       Score      Error  Units
     * JMHSample_02_BenchmarkModes.measureSamples                         sample   50  108603.638 ± 1433.014  us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p0.00    sample       100532.224             us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p0.50    sample       108920.832             us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p0.90    sample       110742.733             us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p0.95    sample       111719.219             us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p0.99    sample       116260.864             us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p0.999   sample       116260.864             us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p0.9999  sample       116260.864             us/op
     * JMHSample_02_BenchmarkModes.measureSamples:measureSamples·p1.00    sample       116260.864             us/op
     *
     * @throws InterruptedException
     */
    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureSamples() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }


    /**
     * 冷启动的测试
     * 此方法在一轮中只会运行一次，这个模式主要是为了测试冷启动的性能
     * 因为 java 有一个预热的过程，java 代码是越执行越快，通过J1方式优化让自己执行效率越来越快
     *Benchmark                                      Mode  Cnt       Score      Error  Units
     *JMHSample_02_BenchmarkModes.measureSingleShot    ss    5  104223.960 ± 6180.141  us/op
     *
     * @throws InterruptedException
     */
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureSingleShot() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }


    /**
     * 上面四种模式都测试
     * Benchmark                                                              Mode  Cnt       Score      Error   Units
     * JMHSample_02_BenchmarkModes.measureMultiple                           thrpt    5      ≈ 10⁻⁵             ops/us
     * JMHSample_02_BenchmarkModes.measureMultiple                            avgt    5  108809.850 ± 2243.098   us/op
     * JMHSample_02_BenchmarkModes.measureMultiple                          sample   50  108708.495 ± 1136.689   us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p0.00    sample       101187.584              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p0.50    sample       109051.904              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p0.90    sample       110467.482              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p0.95    sample       111017.984              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p0.99    sample       115081.216              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p0.999   sample       115081.216              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p0.9999  sample       115081.216              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple:measureMultiple·p1.00    sample       115081.216              us/op
     * JMHSample_02_BenchmarkModes.measureMultiple                              ss    5  104209.840 ± 1756.008   us/op
     *
     * @throws InterruptedException
     */
    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureMultiple() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }


    /**
     * 同上
     * @throws InterruptedException
     */
    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureAll() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_02_BenchmarkModes.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
