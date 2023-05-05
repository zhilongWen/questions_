package com.at.udf;

import com.alibaba.fastjson2.JSONValidator;
import com.googlecode.protobuf.format.JsonFormat;
import com.google.protobuf.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zero
 * @create 2023-05-05
 */
@Description(
        name = "json_to_pb",
        value = "",
        extended = "Example "
                + "select json_to_pb(value,'com.at.pb.UserMessageInfoProto$UserMessageInfo') as v\n"
                + "from (\n"
                + "         select '{\"update_time\": 1683219918380,\"id_card\": 12312312364732490,\"name\": \"wuiwkdj\",\"sex\": 1,\"user_addr\": [{\"key\": \"ads\",\"value\": {\"city\": \"232wq\",\"addr\": \"asdsa\"}},{\"key\": \"DFD\",\"value\": {\"city\": \"232wq\",\"addr\": \"asdsa\"}}]}' as value\n"
                + "     ) a"
)
public class GenericUDFJSON2PB extends GenericUDF {


    public static final String functionName = "json_to_pb";

    private StringObjectInspector var1;
    private StringObjectInspector var2;

    private StringObjectInspector restIO;

    private Class<?> pbClass = null;

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {

        if (objectInspectors == null || objectInspectors.length != 2) {
            throw new UDFArgumentException("The function " + functionName + " must accepts two argument.");
        }

        if (!(objectInspectors[0] instanceof StringObjectInspector && objectInspectors[1] instanceof StringObjectInspector)) {
            throw new UDFArgumentException("The function argument must String type, but first argument " + objectInspectors[0].getTypeName() + " is found, second argument " + objectInspectors[1].getTypeName() + " is found.");
        }

        var1 = (StringObjectInspector) objectInspectors[0];
        var2 = (StringObjectInspector) objectInspectors[1];

        restIO = (StringObjectInspector) PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(PrimitiveObjectInspector.PrimitiveCategory.STRING);

        return restIO;
    }

    @Override
    public String evaluate(DeferredObject[] arguments) throws HiveException {
        String jsonString = arguments[0].get().toString();
        String clazzString = arguments[1].get().toString();

        if (!isValid(jsonString, clazzString)) {
            throw new UDFArgumentException("argument's format error. ");
        }

        if (pbClass == null) {
            try {
                pbClass = Class.forName(clazzString);
            } catch (Exception e) {
                throw new HiveException("function load jar error，" + e.getMessage());
            }
        }

        byte[] json2PB = json2PB(jsonString);


        return Base64.getEncoder().encodeToString(json2PB);
    }

    private byte[] json2PB(String jsonData) {

        byte[] pbData = null;

        try {

            Method method = pbClass.getMethod("newBuilder");

            Message.Builder protoBuilder = (Message.Builder) method.invoke(null, null);

            ByteArrayInputStream is = new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));

            JsonFormat fmt = new JsonFormat();

            fmt.merge(is, protoBuilder);


            pbData = protoBuilder.build().toByteArray();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pbData;


    }

    private boolean isValid(String jsonString, String clazzString) {

        if (StringUtils.isEmpty(jsonString) && JSONValidator.from(jsonString).validate()) {
            return false;
        }

        if (StringUtils.isEmpty(clazzString) || !clazzString.contains("$")) {
            return false;
        }

        return true;

    }

    @Override
    public String getDisplayString(String[] children) {
        return functionName;
    }
}
