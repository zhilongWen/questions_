package com.at.udf;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zero
 * @create 2022-09-29
 */
@Description(
        name = "get_json_array",
        value = "",
        extended = "Example："
                + "select\n"
                + "    id,\n"
                + "    get_json_object(jsonStr,\"$.address\") as o_address,\n"
                + "    get_json_object(jsonStr,\"$.address.feature\") as o_address_feature,\n"
                + "    get_json_object(jsonStr,\"$.address.feature.f0\") as o_address_feature,\n"
                + "\n"
                + "    get_json_array(jsonStr,\"$.address\") as a_address,\n"
                + "    get_json_array(jsonStr,\"$.address.feature\") as a_address_feature,\n"
                + "    get_json_array(jsonStr,\"$.address.feature.f0\") as a_address_feature_f0\n"
                + "from (\n"
                + "    select\n"
                + "        \"11\" as id,\n"
                + "        '{\"id\":123213,\"name\":{\"first_name\":\"xiaoxiao\",\"last_name\":\"明明\"},\"address\":[{\"city\":\"北京\",\"area_id\":\"0001\",\"feature\":[{\"f0\":\"北京123\",\"f1\":\"北京1234\"}]},{\"city\":\"上海\",\"area_id\":\"0002\",\"feature\":[{\"f0\":\"huangpujiang123\",\"f1\":\"huangpujiang1234\"}]},{\"city\":\"广州\",\"area_id\":\"0003\",\"feature\":[{\"f0\":\"小蛮腰123\",\"f1\":\"小蛮腰1234\"}]}]}\n"
                + "' as jsonStr\n"
                + ") a"
)
public class GenericUDFJSONArray extends GenericUDF {

    //org.apache.hadoop.hive.ql.udf.generic.GenericUDFArray

    private final String functionName = "get_json_array";
    private final String opName;

    private PrimitiveObjectInspector arg01;
    private PrimitiveObjectInspector arg02;

    private transient ObjectInspectorConverters.Converter[] converters;
    private transient ArrayList<Object> ret = new ArrayList<Object>();

    private final JsonFactory jsonFactory = new JsonFactory();
    private final ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

    private static final JavaType MAP_TYPE = TypeFactory.fromClass(Map.class);

    public GenericUDFJSONArray() {
        this.opName = getClass().getName();
    }

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {

        if (arguments == null || arguments.length != 2) {
            throw new UDFArgumentException("The function " + functionName + " requires two arguments.");
        }

        for (int i = 0; i < 2; i++) {
            ObjectInspector.Category category = arguments[i].getCategory();
            if (category != ObjectInspector.Category.PRIMITIVE) {
                throw new UDFArgumentTypeException(i, "The "
                        + GenericUDFUtils.getOrdinal(i + 1)
                        + " argument of " + opName + "  is expected to a "
                        + ObjectInspector.Category.PRIMITIVE.toString().toLowerCase() + " type, but "
                        + category.toString().toLowerCase() + " is found");
            }
        }

        arg01 = (PrimitiveObjectInspector) arguments[0];
        arg02 = (PrimitiveObjectInspector) arguments[1];

        GenericUDFUtils.ReturnObjectInspectorResolver returnOIResolver = new GenericUDFUtils.ReturnObjectInspectorResolver(true);

        converters = new ObjectInspectorConverters.Converter[arguments.length];

        ObjectInspector returnOI = returnOIResolver.get(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        for (int i = 0; i < arguments.length; i++) {
            converters[i] = ObjectInspectorConverters.getConverter(arguments[i], returnOI);
        }


        return ObjectInspectorFactory.getStandardListObjectInspector(returnOI);
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {

        ret.clear();

        String jsonString = arguments[0].get().toString();
        String pathString = arguments[1].get().toString();


        if (!isValid(jsonString, pathString)) {
            return null;
        }

        String[] pathExpr = pathString.split("\\.", -1);

        Object extractObject = null;
        try {
            extractObject = objectMapper.readValue(jsonString, MAP_TYPE);
        } catch (IOException e) {
            return null;
        }

        for (int i = 1; i < pathExpr.length; i++) {
            if (extractObject == null) {
                return null;
            }
            extractObject = extract(extractObject, pathExpr[i], false);
        }

//        System.out.println(extractObject);

        if (extractObject instanceof List) {
            ret.addAll((List<Object>) extractObject);
        }

        return ret;
    }

    private Object extract(Object json, String path, boolean skipMapProc) {

        json = extract_json_withkey(json, path);

        return json;
    }


    @SuppressWarnings("unchecked")
    private Object extract_json_withkey(Object json, String path) {
        if (json instanceof List) {
            List<Object> jsonArray = new ArrayList<Object>();
            for (int i = 0; i < ((List<Object>) json).size(); i++) {
                Object json_elem = ((List<Object>) json).get(i);
                Object json_obj = null;
                if (json_elem instanceof Map) {
                    json_obj = ((Map<String, Object>) json_elem).get(path);
                } else {
                    continue;
                }
                if (json_obj instanceof List) {
                    for (int j = 0; j < ((List<Object>) json_obj).size(); j++) {
                        jsonArray.add(((List<Object>) json_obj).get(j));
                    }
                } else if (json_obj != null) {
                    jsonArray.add(json_obj);
                }
            }
            return (jsonArray.size() == 0) ? null : jsonArray;
        } else if (json instanceof Map) {
            return ((Map<String, Object>) json).get(path);
        } else {
            return null;
        }
    }


    public boolean isValid(String jsonString, String pathString) {

        if (StringUtils.isEmpty(jsonString)
                || StringUtils.isEmpty(pathString)
                || pathString.charAt(0) != '$') {
            return false;
        }

        try {
            JSONObject.parseObject(jsonString);
        } catch (Exception e) {
            return false;
        }


        return true;

    }

    @Override
    public String getDisplayString(String[] children) {
        return "解析json中的array";
    }
}
