package com.at;

import org.codehaus.janino.ExpressionEvaluator;
import org.junit.Before;
import org.junit.Test;

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
    public void testExpressionCacheReuse() {
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
