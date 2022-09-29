package org.apache.hadoop.hive.ql.udf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @create 2022-09-29
 */
public class UDFJsonTest {

    String jsonStr;
    UDFJson udf;

    @Before
    public void setUp() throws Exception {

        jsonStr = "{\"id\":123213,\"name\":{\"first_name\":\"xiaoxiao\",\"last_name\":\"明明\"},\"address\":[{\"city\":\"北京\",\"area_id\":\"0001\",\"feature\":[{\"f0\":\"北京123\",\"f1\":\"北京1234\"}]},{\"city\":\"上海\",\"area_id\":\"0002\",\"feature\":[{\"f0\":\"huangpujiang123\",\"f1\":\"huangpujiang1234\"}]},{\"city\":\"广州\",\"area_id\":\"0003\",\"feature\":[{\"f0\":\"小蛮腰123\",\"f1\":\"小蛮腰1234\"}]}]}\n";

        udf = new UDFJson();
    }


    @Test
    public void getJsonObjectTest() {

//        System.out.println(udf.evaluate(jsonStr, "$.id"));
//        System.out.println(udf.evaluate(jsonStr, "$"));

        System.out.println(udf.evaluate(jsonStr,"$.name.first_name"));

    }

    @After
    public void tearDown() throws Exception {
        jsonStr = null;
    }

}
