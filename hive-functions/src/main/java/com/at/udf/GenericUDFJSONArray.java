package com.at.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

/**
 * @author zero
 * @create 2022-09-29
 */
@Description(
        name = "get_json_array"
)
public class GenericUDFJSONArray extends GenericUDF {



    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        return null;
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        return null;
    }

    @Override
    public String getDisplayString(String[] children) {
        return null;
    }
}
