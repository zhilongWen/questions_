package com.at;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

import java.lang.reflect.InvocationTargetException;

// http://janino-compiler.github.io/janino/

public class ExpressionEvaluatorTest {
    public static void main(String[] args) throws CompileException, InvocationTargetException {

        ExpressionEvaluator ee = new ExpressionEvaluator();
//        ee.cook("3 + 4");
//        System.out.println(ee.evaluate());

        ee.setParameters(new String[]{"a","b"},new Class[]{int.class,int.class});
        ee.setExpressionType(int.class);
        ee.cook("a + b");
        Object evaluate = ee.evaluate(new Object[]{19, 21});
        System.out.println(evaluate);

    }
}
