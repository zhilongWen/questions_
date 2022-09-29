package com.at.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @create 2022-09-29
 */
public class GenericUDFJSONArrayTest {

    GenericUDFJSONArray udf;

    String argument01;
    String argument02;

    @Before
    public void setUp() throws Exception {

        udf = new GenericUDFJSONArray();

        argument01 = "{\"id\":123213,\"name\":\"lisi\",\"address\":[{\"city\":\"北京\",\"area_id\":\"0001\",\"feature\":\"胡同\"},{\"city\":\"上海\",\"area_id\":\"0002\",\"feature\":\"huangpujiang\"},{\"city\":\"广州\",\"area_id\":\"0003\",\"feature\":\"小蛮腰\"}]}";

        argument02 = "{\"id\":123213,\"name\":{\"first_name\":\"xiaoxiao\",\"last_name\":\"明明\"},\"address\":[{\"city\":\"北京\",\"area_id\":\"0001\",\"feature\":[{\"f0\":\"北京123\",\"f1\":\"北京1234\"}]},{\"city\":\"上海\",\"area_id\":\"0002\",\"feature\":[{\"f0\":\"huangpujiang123\",\"f1\":\"huangpujiang1234\"}]},{\"city\":\"广州\",\"area_id\":\"0003\",\"feature\":[{\"f0\":\"小蛮腰123\",\"f1\":\"小蛮腰1234\"}]}]}\n";

    }

    @After
    public void tearDown() throws Exception {
        udf = null;
        argument01 = null;
        argument02 = null;
    }

    @Test
    public void test() throws HiveException {

        ObjectInspector[] argsOIs = new ObjectInspector[] {
                PrimitiveObjectInspectorFactory.javaStringObjectInspector,
                PrimitiveObjectInspectorFactory.javaStringObjectInspector
        };

        udf.initialize(argsOIs);

        String pattern = "$.address.feature";

        System.out.println(argument02);

        GenericUDF.DeferredObject[] deferredObjects = {
                new GenericUDF.DeferredJavaObject(argument02),
                new GenericUDF.DeferredJavaObject(pattern)
        };

        Object evaluate = udf.evaluate(deferredObjects);

        System.out.println(evaluate);


    }



}
