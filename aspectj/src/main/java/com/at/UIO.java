package com.at;

import com.at.aspectj.TimeConsumeLogAnnotation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @create 2023-07-16
 */
public class UIO {

    public static void sayHelloWorld(String name) {
        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("Hello " + name);
    }

    public static void abc(String name) {
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("abc " + name);
    }


    public static String sadsai(String name) {
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        return "aaa " + name;
    }

    public static Map<String, List<Integer>> wqeqw(String name) {
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        return new HashMap<String, List<Integer>>(){{put(name, Arrays.asList(1,2,3));}};
    }

}
