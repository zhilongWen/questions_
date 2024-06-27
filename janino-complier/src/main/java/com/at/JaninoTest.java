package com.at;

import org.codehaus.janino.SimpleCompiler;

import java.lang.reflect.Method;

public class JaninoTest {
    public static void main(String[] args) throws Exception {

        // 类定义
//        String classDef = "public class HelloWorld { public static void helloWorld() { System.out.println(\"Hello, world!\"); } }";
//
//        // 使用janino动态编译字符串并加载进类加载器
//        SimpleCompiler sc = new SimpleCompiler();
//        sc.cook(classDef);
//        Class<?> clazz = sc.getClassLoader().loadClass("HelloWorld");
//
//        // 使用janino执行
//        clazz.getMethod("helloWorld").invoke(null);

        String coder =
                "public class Test {\n"
                        + "    public  void f(int[] arr){\n"
                        + "        if (arr == null){\n"
                        + "            System.out.println(\"arr is null\");\n"
                        + "            return;\n"
                        + "        }\n"
                        + "\n"
                        + "        for (int i = 0; i < arr.length; i++) {\n"
                        + "            System.out.println(\"idx = \" + i + \", value = \" + arr[i]);\n"
                        + "        }\n"
                        + "    }\n"
                        + "}";


        SimpleCompiler simpleCompiler = new SimpleCompiler();
        simpleCompiler.setParentClassLoader(Thread.currentThread().getContextClassLoader());
        simpleCompiler.cook(coder);

        Class<?> testClass = simpleCompiler.getClassLoader().loadClass("Test");

        Method method = testClass.getMethod("f", int[].class);
//        method.invoke(null,new int[]{1,2,3});      // static
        method.invoke(testClass.newInstance(), new int[]{1, 2, 3});

    }
}
