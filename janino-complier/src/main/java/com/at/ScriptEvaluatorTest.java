package com.at;

import org.codehaus.janino.ScriptEvaluator;

public class ScriptEvaluatorTest {

    public static void main(String[] args) throws Exception{

        ScriptEvaluator se = new ScriptEvaluator();

        // 必须是静态的
        se.cook(
                ""
                        + "static void method1() {\n"
                        + "    System.out.println(1);\n"
                        + "}\n"
                        + "\n"
                        + "method1();\n"
                        + "method2();\n"
                        + "\n"
                        + "static void method2() {\n"
                        + "    System.out.println(2);\n"
                        + "}\n"
        );

        se.evaluate(new Object[]{});
    }
}
