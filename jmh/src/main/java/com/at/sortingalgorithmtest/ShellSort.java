package com.at.sortingalgorithmtest;

/**
 * @create 2023-01-02
 */
public class ShellSort {

    public static int[] sort(int[] arr) throws Exception {
        int length = arr.length;
        int temp;
        for (int step = length / 2; step >= 1; step /= 2) {
            for (int i = step; i < length; i++) {
                temp = arr[i];
                int j = i - step;
                while (j >= 0 && arr[j] > temp) {
                    arr[j + step] = arr[j];
                    j -= step;
                }
                arr[j + step] = temp;
            }
        }
        return arr;
    }

}
