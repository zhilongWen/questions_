package com.at;

public class Test {
    public static void f(int[] arr){
        if (arr == null){
            System.out.println("arr is null");
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.println("idx = " + i + " => " + arr[i]);
        }
    }
    public static void f1(){
        System.out.println("hello");
    }
}
