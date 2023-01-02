package com.at.sortingalgorithmtest;

import cn.hutool.core.util.RandomUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author zero
 * @create 2023-01-02
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class SortingAlgorithmTest {

    int[] arr;

    @Setup
    public void prepare() {

        arr = new int[100_000];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = RandomUtil.randomInt(0, 100);
        }
    }

    @Benchmark
    public int[] bubbleSort() throws Exception {
        return BubbleSort.sort(arr);
    }

    @Benchmark
    public int[] bucketSort() throws Exception {
        return BucketSort.sort(arr);
    }

    @Benchmark
    public int[] countingSort() throws Exception {
        return CountingSort.sort(arr);
    }

    @Benchmark
    public int[] heapSort() throws Exception {
        return HeapSort.sort(arr);
    }

    @Benchmark
    public int[] insertSort() throws Exception {
        return InsertSort.sort(arr);
    }

    @Benchmark
    public int[] mergeSort() throws Exception {
        return MergeSort.sort(arr);
    }

    @Benchmark
    public int[] quickSort() throws Exception {
        return QuickSort.sort(arr);
    }

    @Benchmark
    public int[] radixSort() throws Exception {
        return RadixSort.sort(arr);
    }

    @Benchmark
    public int[] selectionSort() throws Exception {
        return SelectionSort.sort(arr);
    }

    @Benchmark
    public int[] shellSort() throws Exception {
        return ShellSort.sort(arr);
    }

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(SortingAlgorithmTest.class.getSimpleName())
                // 可视化 https://jmh.morethan.io/
                .resultFormat(ResultFormatType.JSON)
                .result("D:\\workspace\\questions_\\jmh\\src\\main\\java\\com\\at\\sortingalgorithmtest\\res.json")
                .build();

        new Runner(opt).run();

    }

}
