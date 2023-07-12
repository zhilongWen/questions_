package com.at.t5;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class DirectoryClassLoaderExample {
    public static void main(String[] args) throws Exception {
        // 指定目录路径
        String directoryPath = "./files/";

        // 创建一个URLClassLoader，加载指定目录下的类
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(directoryPath).toURI().toURL()});

        // 加载类
        Class<?> loadedClass = classLoader.loadClass("com.at.t5.MyClass");

        // 创建类的实例
        Object instance = loadedClass.newInstance();

        // 获取方法
        Method method = loadedClass.getMethod("methodName");

        // 调用方法
        method.invoke(instance);
    }
}
