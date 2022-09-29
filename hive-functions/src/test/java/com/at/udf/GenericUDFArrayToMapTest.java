package com.at.udf;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @create 2022-09-29
 */
public class GenericUDFArrayToMapTest {

    GenericUDFArrayToMap udf;

    List<Long> argument;

    @Before
    public void setUp() {
        udf = new GenericUDFArrayToMap();
        argument = Arrays.asList(1L, 2L, 3L, 5L, 1L, 8L, 2L);
    }

    @Test
    public void test() throws HiveException {

        ObjectInspector[] argsOIs = new ObjectInspector[] {
                ObjectInspectorFactory.getStandardListObjectInspector(
                        PrimitiveObjectInspectorFactory.javaLongObjectInspector
                )
        };


        udf.initialize(argsOIs);


        GenericUDF.DeferredObject[] deferredObjects = {
                new GenericUDF.DeferredJavaObject(argument)
        };

        Object evaluate = udf.evaluate(deferredObjects);

        System.out.println(evaluate);

    }

}
