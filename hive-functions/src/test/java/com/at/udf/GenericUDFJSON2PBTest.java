package com.at.udf;

import com.at.pb.UserMessageInfoProto;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Base64;
import java.util.HashMap;

/**
 * @create 2023-05-05
 */
public class GenericUDFJSON2PBTest {


    GenericUDFJSON2PB udf;

    String argument01;
    String argument02;

    @Before
    public void setUp(){

        udf = new GenericUDFJSON2PB();

        argument01 = init(); //"{\"update_time\": 1683219918380,\"id_card\": 12312312364732490,\"name\": \"wuiwkdj\",\"sex\": 1,\"user_addr\": [{\"key\": \"ads\",\"value\": {\"city\": \"232wq\",\"addr\": \"asdsa\"}},{\"key\": \"DFD\",\"value\": {\"city\": \"232wq\",\"addr\": \"asdsa\"}}]}\n";

        argument02 = "com.at.pb.UserMessageInfoProto$UserMessageInfo";

    }


    @Test
    public void test() throws HiveException, InvalidProtocolBufferException {

        System.out.println(argument01);
        System.out.println(argument02);

        ObjectInspector[] argsIOs = {
                PrimitiveObjectInspectorFactory.javaStringObjectInspector,
                PrimitiveObjectInspectorFactory.javaStringObjectInspector,
        };

        udf.initialize(argsIOs);

        GenericUDF.DeferredObject[] deferredObjects = {
                new GenericUDF.DeferredJavaObject(argument01),
                new GenericUDF.DeferredJavaObject(argument02),
        };

        Object evaluate = udf.evaluate(deferredObjects);

        System.out.println(evaluate);

        UserMessageInfoProto.UserMessageInfo userMessageInfo = UserMessageInfoProto.UserMessageInfo.parseFrom(Base64.getDecoder().decode(evaluate.toString()));

        System.out.println(userMessageInfo);


    }

    @After
    public void clear(){
        udf = null;
        argument01 = null;
        argument02 = null;
    }



    public String init(){

        UserMessageInfoProto.Address address = UserMessageInfoProto.Address.newBuilder()
                .setAddr("asdsa")
                .setCity("232wq")
                .build();

        UserMessageInfoProto.UserMessageInfo userMessageInfo = UserMessageInfoProto.UserMessageInfo.newBuilder()
                .setUpdateTime(System.currentTimeMillis())
                .setIdCard(12312312364732490L)
                .setName("wuiwkdj")
                .setSex(1)
                .putAllUserAddr(
                        new HashMap<String, UserMessageInfoProto.Address>(){{
                            put("ads",address);
                            put("DFD",address);
                        }}
                )
                .build();

        System.out.println(userMessageInfo);

        String printToString = new JsonFormat().printToString(userMessageInfo);

        System.out.println(printToString);

        return printToString;

    }


}
