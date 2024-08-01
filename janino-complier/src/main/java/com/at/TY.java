package com.at;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TY {

    static JedisCluster jedisCluster;
    static ScheduledExecutorService executorService;

    static {

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("10.211.55.102", 6379));
        jedisClusterNodes.add(new HostAndPort("10.211.55.102", 6380));
        jedisClusterNodes.add(new HostAndPort("10.211.55.102", 6381));

        // 创建 JedisCluster 实例
        jedisCluster = new JedisCluster(jedisClusterNodes);

        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    static volatile String code = "json.get(\"name\")";

    public static void main(String[] args) throws CompileException, InvocationTargetException, InterruptedException, JaninoCompareException {

        executorService.scheduleAtFixedRate(
                () -> {
                    code = jedisCluster.get("111");
                },
                5,
                5,
                TimeUnit.SECONDS
        );

        FOO foo = (FOO) ExpressionEvaluator.createFastExpressionEvaluator(
                "a + b",
                FOO.class,
                new String[]{"a", "b"},
                TY.class.getClassLoader()
        );

        System.out.println(foo.bar(1, 2));

        System.out.println();
        System.out.println();
        System.out.println("==================================================");

        String str = "{\n"
                + "\"name\":\"zhangsan\",\n"
                + "\"age\":1\n"
                + "}";

        JSONObject jsonObject = JSON.parseObject(str);

        System.out.println(jsonObject.get("name"));



        while (true){

            System.out.println(code);

            ExpressionEvaluator ee = CompileUtils.compileExpression(
                    code,
                    Arrays.asList("json"),
                    Arrays.asList(JSONObject.class),
                    Object.class
            );

            Object evaluate = ee.evaluate(new Object[]{jsonObject});
            System.out.println(evaluate);

            TimeUnit.SECONDS.sleep(1);
        }


    }
}


