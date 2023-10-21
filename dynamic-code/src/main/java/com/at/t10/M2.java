package com.at.t10;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @create 2023-10-21
 */
public class M2 {

    public static void main(String[] args) throws Exception {

        String classFile = "D:\\workspace\\questions_\\dynamic-code\\src\\main\\java\\com\\at\\t10\\Main.class";

        final URL[] urls = {new URL("file:" + classFile)};

        URLClassLoader classLoader = new URLClassLoader(urls);
        final Class<?> main = classLoader.loadClass("com.at.t10.Main");

        final Object instance = main.newInstance();

        System.out.println(instance);

        final Method mainMethod = main.getMethod("main", String[].class);
        mainMethod.invoke(null,(Object) null);


    }

}
