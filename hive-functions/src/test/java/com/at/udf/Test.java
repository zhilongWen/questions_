package com.at.udf;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;

/**
 * @create 2022-09-29
 */
public class Test {

    String jsonStr;

    @Before
    public void setUp() {

        jsonStr = "{\"id\":123213,\"name\":\"lisi\",\"address\":[{\"city\":\"北京\",\"area_id\":\"0001\",\"feature\":[{\"f0\":\"北京123\",\"f1\":\"北京1234\"}]},{\"city\":\"上海\",\"area_id\":\"0002\",\"feature\":[{\"f0\":\"huangpujiang123\",\"f1\":\"huangpujiang1234\"}]},{\"city\":\"广州\",\"area_id\":\"0003\",\"feature\":[{\"f0\":\"小蛮腰123\",\"f1\":\"小蛮腰1234\"}]}]}";




    }

    @org.junit.Test
    public void test1(){

//
//        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//
//        System.out.println(jsonObject);
//
//        System.out.println(jsonObject.get("id"));
//
//        JSONArray address = jsonObject.getJSONArray("address");
//
//        for (Object o : address) {
//            if (o instanceof JSONObject) {
//                JSONObject object = (JSONObject) o;
//                System.out.println(object);
//                System.out.println(object.get("city"));
//            }
//        }

        System.out.println(StringUtils.isEmpty(""));     // true
        System.out.println(StringUtils.isAnyEmpty("")); // true
        System.out.println(StringUtils.isNoneEmpty("")); // false
        System.out.println(StringUtils.isNotEmpty(""));    // false

        System.out.println("===================================================");

        System.out.println(StringUtils.isEmpty(null));
        System.out.println(StringUtils.isAnyEmpty(null));
        System.out.println(StringUtils.isNoneEmpty(null));
        System.out.println(StringUtils.isNotEmpty(null));

        System.out.println("===================================================");

        String s = new String();

        System.out.println(StringUtils.isEmpty(s));
        System.out.println(StringUtils.isAnyEmpty(s));
        System.out.println(StringUtils.isNoneEmpty(s));
        System.out.println(StringUtils.isNotEmpty(s));

        System.out.println(s.isEmpty());
        System.out.println("".isEmpty());
        System.out.println(" ".isEmpty()); // false


//        JSONObject sfdsfdsf = JSONObject.parseObject("sfdsfdsf");
//
//        System.out.println(sfdsfdsf);

        boolean f = false;

        assert f; // false java.lang.AssertionError


    }

    @After
    public void tearDown() {
        jsonStr = null;
    }

}
