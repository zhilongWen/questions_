package com.at;

import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class CompileUtilsTest {

    @Before
    public void before() {
        // cleanup cached class before tests
        CompileUtils.COMPILED_CLASS_CACHE.invalidateAll();
        CompileUtils.COMPILED_EXPRESSION_CACHE.invalidateAll();
    }

    @Test
    public void t5() throws Exception {

//        String code = ""
//                + "static void method1() {\n"
//                + "    System.out.println(\"run in method1()\");\n"
//                + "}\n"
//                + "\n"
//                + "static void method2() {\n"
//                + "    System.out.println(\"run in method2()\");\n"
//                + "}\n"
//                + "\n"
//                + "method1();\n"
//                + "method2();\n"
//                + "\n";
//
//
//        ExpressionEvaluator ee = CompileUtils.compileExpression(
//                code,
//                new ArrayList<>(),
//                new ArrayList<>(),
//                Object.class
//        );
//
//        ee.evaluate(new Object[]{});

        String code =
                ""
                        + "System.out.println(arg1);\n"
                        + "System.out.println(arg2);\n"
                        + "\n"
                        + "static void method1() {\n"
                        + "    System.out.println(\"run in method1()\");\n"
                        + "}\n"
                        + "\n"
                        + "public static void method2() {\n"
                        + "    System.out.println(\"run in method2()\");\n"
                        + "}\n"
                        + "\n"
                        + "method1();\n"
                        + "method2();\n"
                        + "\n";

        ScriptEvaluator se = CompileUtils.compileScriptEvaluator(
                code,
                Arrays.asList("arg1", "arg2"),
                Arrays.asList(String.class, int.class),
                EmptyClass.class
        );

        Object evaluate = se.evaluate(new Object[]{"aaa", 22});
        System.out.println(evaluate);

    }

    @Test
    public void t4() throws Exception {

        ScriptEvaluator se = new ScriptEvaluator();
        se.cook(
                ""
                        + "static void method1() {\n"
                        + "    System.out.println(\"run in method1()\");\n"
                        + "}\n"
                        + "\n"
                        + "static void method2() {\n"
                        + "    System.out.println(\"run in method2()\");\n"
                        + "}\n"
                        + "\n"
                        + "method1();\n"
                        + "method2();\n"
                        + "\n"

        );

        se.evaluate(null);

        se = new ScriptEvaluator();
        se.setParameters(new String[]{"arg1", "arg2"}, new Class[]{String.class, int.class});
        se.cook(
                ""
                        + "System.out.println(arg1);\n"
                        + "System.out.println(arg2);\n"
                        + "\n"
                        + "static void method1() {\n"
                        + "    System.out.println(\"run in method1()\");\n"
                        + "}\n"
                        + "\n"
                        + "public static void method2() {\n"
                        + "    System.out.println(\"run in method2()\");\n"
                        + "}\n"
                        + "\n"
                        + "method1();\n"
                        + "method2();\n"
                        + "\n"

        );
        se.evaluate(new Object[]{"aaa", 22});
    }

    @Test
    public void t3() throws Exception {
//        ScriptEvaluator se = new ScriptEvaluator();
//        se.cook(
//                ""
//                        + "static void method1() {\n"
//                        + "    System.out.println(\"run in method1()\");\n"
//                        + "}\n"
//                        + "\n"
//                        + "static void method2() {\n"
//                        + "    System.out.println(\"run in method2()\");\n"
//                        + "}\n"
//                        + "\n"
//                        + "method1();\n"
//                        + "method2();\n"
//                        + "\n"
//
//        );
//
//        se.evaluate(null);


        String code = "public class GeneratedClass {" +
                "    public static void method1() {" +
                "        System.out.println(\"run in method1()\");" +
                "    }" +
                "    public static void method2() {" +
                "        System.out.println(\"run in method2()\");" +
                "    }" +
                "    public static void main(String[] args) {" +
                "        method1();" +
                "        method2();" +
                "    }" +
                "}";

        Class<Object> instance = CompileUtils.compile(
                this.getClass().getClassLoader(),
                "GeneratedClass",
                code
        );
        System.out.println(instance);

        Method mainMethod = instance.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) new String[]{});

    }

    @Test
    public void t2() throws Exception {
//        // 首先定义一个表达式模拟器ExpressionEvaluator对象
//        ExpressionEvaluator ee = new ExpressionEvaluator();
//
//        // 定义一个算术表达式，表达式中需要有2个int类型的参数a和b
//        String expression = "2 * (a + b)";
//        ee.setParameters(new String[] { "a", "b" }, new Class[] { int.class, int.class });
//
//        // 设置表达式的返回结果也为int类型
//        ee.setExpressionType(int.class);
//
//        // 这里处理（扫描，解析，编译和加载）上面定义的算数表达式.
//        ee.cook(expression);
//
//        // 根据输入的a和b参数执行实际的表达式计算过程
//        int result = (Integer) ee.evaluate(new Object[] { 19, 23 });
//        System.out.println(expression + " = " + result);


        ExpressionEvaluator ee = CompileUtils.compileExpression(
                "2 * (a + b)",
                Arrays.asList("a", "b"),
                Arrays.asList(int.class, int.class),
                int.class
        );

        Object evaluate = ee.evaluate(new Object[]{19, 23});
        System.out.println(evaluate);
    }


    @Test
    public void testCacheReuse() {
        String code = "public class Main {\n" + "  int i;\n" + "  int j;\n" + "}";

        Class<?> class1 = CompileUtils.compile(this.getClass().getClassLoader(), "Main", code);
        Class<?> class2 = CompileUtils.compile(this.getClass().getClassLoader(), "Main", code);
        Class<?> class3 = CompileUtils.compile(new TestClassLoader(), "Main", code);

        System.out.println(class1);
        System.out.println(class2);
        System.out.println(class3);

        // true
        System.out.println(class1 == class2);

        // false
        System.out.println(class2 == class3);
    }

    @Test
    public void testExpressionCacheReuse() throws JaninoCompareException {
        String code = "a + b";

        ExpressionEvaluator evaluator1 =
                CompileUtils.compileExpression(
                        code,
                        Arrays.asList("a", "b"),
                        Arrays.asList(Integer.class, Integer.class),
                        Integer.class);
        ExpressionEvaluator evaluator2 =
                CompileUtils.compileExpression(
                        code,
                        Arrays.asList("a", "b"),
                        Arrays.asList(Integer.class, Integer.class),
                        Integer.class);
        ExpressionEvaluator evaluator3 =
                CompileUtils.compileExpression(
                        code,
                        Arrays.asList("a", "b"),
                        Arrays.asList(String.class, String.class),
                        String.class);

        System.out.println(evaluator1);
        System.out.println(evaluator2);
        System.out.println(evaluator3);
    }

    private static class TestClassLoader extends URLClassLoader {

        TestClassLoader() {
            super(new URL[0], Thread.currentThread().getContextClassLoader());
        }
    }
}
