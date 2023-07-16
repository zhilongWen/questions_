package com.at.controller;

import com.at.aspectj.TimeConsumeLogAnnotation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @create 2023-07-17
 */
public class BaseController {

    @TimeConsumeLogAnnotation
    public static void test1() {
        System.out.println("BaseController test1...");
    }

    @TimeConsumeLogAnnotation
    public static void test2(String arg) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("BaseController test2，arg = " + arg);
    }

    @TimeConsumeLogAnnotation
    public static String test3(String arg) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "BaseController test3, arg = " + arg;
    }

    @TimeConsumeLogAnnotation
    public static Map<String, List<Integer>> test4(String arg) {
        System.out.println("BaseController test4，arg: = " + arg);
        return new HashMap<String, List<Integer>>() {{
            put(arg, Arrays.asList(1, 2, 3));
        }};
    }

    @TimeConsumeLogAnnotation
    public static void test5(){
           System.out.println("BaseController test5");
//           int i = 10 / 0;
           throw new RuntimeException();
    }


}
