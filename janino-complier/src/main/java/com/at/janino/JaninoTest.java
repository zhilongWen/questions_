package com.at.janino;

import com.at.CompileUtils;
import com.at.EmptyClass;
import org.codehaus.commons.compiler.*;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class JaninoTest {

    ICompilerFactory compilerFactory;

    @Before
    public void setUp() {
        System.out.println("start:" + System.currentTimeMillis());
        try {
            compilerFactory = CompilerFactoryFactory.getDefaultCompilerFactory(this.getClass().getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void tearDown() {
        compilerFactory = null;
    }

    @Test
    public void t5() throws Exception {
        // 创建一个 ScriptEvaluator 实例
        ScriptEvaluator se = new ScriptEvaluator();

        // 设置脚本的返回类型
        se.setReturnType(String.class);

        // 设置脚本的参数名称和类型
        se.setParameters(new String[]{"name", "age"}, new Class[]{String.class, int.class});

        // 定义脚本
        String script = "if (age >= 18) {\n"
                + "            return \"Hello, \" + name + \". You are an adult.\";\n"
                + "        } else {\n"
                + "            return \"Hello, \" + name + \". You are a minor.\";\n"
                + "        }";
        System.out.println(script);

        // 编译脚本
        se.cook(script);

        // 执行脚本并传递参数
        String result = (String) se.evaluate(new Object[]{"Alice", 20});
        System.out.println(result);  // 输出: Hello, Alice. You are an adult.

        result = (String) se.evaluate(new Object[]{"Bob", 16});
        System.out.println(result);  // 输出: Hello, Bob. You are a minor.


        ScriptEvaluator se1 = CompileUtils.compileScriptEvaluator(
                script,
                Arrays.asList("name", "age"),
                Arrays.asList(String.class, int.class),
                String.class
        );

        System.out.println(se1.evaluate(new Object[]{"11aa", 11}));

    }

    @Test
    public void t4() throws Exception {
        ScriptEvaluator se = new ScriptEvaluator();
        se.setParameters(new String[]{"arg1", "arg2"}, new Class[]{String.class, int.class});
        String code = ""
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
        se.cook(
                code

        );
        se.evaluate(new Object[]{"aaa", 22});


        ScriptEvaluator scriptEvaluator = CompileUtils.compileScriptEvaluator(
                code,
                Arrays.asList("arg1", "arg2"),
                Arrays.asList(String.class, int.class),
                EmptyClass.class
        );
        scriptEvaluator.evaluate(new Object[]{"aaa", 22});

    }

    @Test
    public void t3() throws Exception {
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
    }

    @Test
    public void t2() throws Exception {
        // 首先定义一个表达式模拟器ExpressionEvaluator对象
        ExpressionEvaluator ee = new ExpressionEvaluator();

        // 定义一个算术表达式，表达式中需要有2个int类型的参数a和b
        String expression = "2 * (a + b)";
        ee.setParameters(new String[]{"a", "b"}, new Class[]{int.class, int.class});

        // 设置表达式的返回结果也为int类型
        ee.setExpressionType(int.class);

        // 这里处理（扫描，解析，编译和加载）上面定义的算数表达式.
        ee.cook(expression);

        // 根据输入的a和b参数执行实际的表达式计算过程
        int result = (Integer) ee.evaluate(new Object[]{19, 23});
        System.out.println(expression + " = " + result);


        ScriptEvaluator se = new ScriptEvaluator();
        se.setParameters(new String[]{"a", "b"}, new Class[]{int.class, int.class});
        se.setReturnType(int.class);
        se.cook(expression);

        Object evaluate = se.evaluate(new Object[]{19, 23});
        System.out.println(evaluate);

    }

    @Test
    public void t1() throws Exception {
        String content = "System.out.println(\"Hello world\" + System.currentTimeMillis());";
        IScriptEvaluator evaluator = new ScriptEvaluator();

        evaluator.cook(content);

        for (int i = 0; i < 10; i++) {
            evaluator.evaluate(null);
        }
    }

    @Test
    public void evaluatorTest() throws CompileException, InvocationTargetException, IllegalAccessException {

        IScriptEvaluator se = compilerFactory.newScriptEvaluator();
        se.setOverrideMethod(new boolean[]{false, false});
        se.setReturnTypes(new Class[]{double.class, double.class});
        se.setMethodNames(new String[]{"funct2", "funct3"});

        se.setParameters(new String[][]{{"a", "b"}, {}}, new Class<?>[][]{{double.class, double.class}, {}});
        se.setStaticMethod(new boolean[]{true, true});
        se.cook(new String[]{"return a + b;", "return 0;"});


        System.out.println(se.getMethod(0).invoke(null, new Object[]{new Double(3.0), new Double(4.0)}));

        System.out.println(se.getMethod(1).invoke(null, new Object[0]));
    }


    @Test
    public void baseClassTest() throws Exception {

        IExpressionEvaluator ee = compilerFactory.newExpressionEvaluator();
        ee.setExtendedClass(BaseClass.class);
        ee.setStaticMethod(false);

        ee.cook("a + b");

        Object instance = ee.getMethod().getDeclaringClass().newInstance();
        BaseClass baseClass = (BaseClass) instance;

        baseClass.a = 7;
        baseClass.b = 8;

        Object result = ee.getMethod().invoke(baseClass);

        System.out.println(result);

    }

    public static class BaseClass {
        public int a, b;
    }

}
