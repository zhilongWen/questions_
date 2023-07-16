package com.at;

import com.at.controller.BaseController;

import java.util.List;
import java.util.Map;

/**
 * @create 2023-07-17
 */
public class Main {

    public static void main(String[] args) {

        BaseController.test1();

        System.out.println("==============================");

        BaseController.test2("test12");

        System.out.println("==============================");

        String test3 = BaseController.test3("test3");
        System.out.println(test3);

        System.out.println("==============================");

        Map<String, List<Integer>> test4 = BaseController.test4("test4");
        System.out.println(test4);

        System.out.println("==============================");

        try {
            BaseController.test5();
        }catch (Exception e){

        }

        System.out.println("==============================");


    }

}
