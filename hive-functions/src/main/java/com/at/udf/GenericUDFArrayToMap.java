package com.at.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.*;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author zero
 * @create 2022-09-29
 */
@Description(
        name = "arr_to_map",
        value = "_FUNC_(ARRAY(args...)) - Return a MAP for array data value and frequency",
        extended = "Example：SELECT arr_to_map(ARRAY(1,2,1,3)) AS val_fre, return {1:2,2:1,3:1}"
)
public class GenericUDFArrayToMap extends GenericUDF {

    public final String functionName = "arr_to_map";

    private MapObjectInspector restIO;
    private ListObjectInspector argumentIo;

    private final Map<Long, Long> ret = new HashMap<>(150);

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {

        if (objectInspectors == null || objectInspectors.length != 1) {
            throw new UDFArgumentException("The function " + functionName + " only accepts one argument.");
        } else if (!(objectInspectors[0] instanceof ListObjectInspector)) {
            throw new UDFArgumentException("The function argument must List type, but " + objectInspectors[0].getTypeName() + " is found");
        }

        argumentIo = (ListObjectInspector) objectInspectors[0];

        restIO = ObjectInspectorFactory.getStandardMapObjectInspector(
                PrimitiveObjectInspectorFactory.javaLongObjectInspector,
                PrimitiveObjectInspectorFactory.javaLongObjectInspector
        );

        return restIO;
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {

        ret.clear();

        List<Long> fvLongList = getLongList(deferredObjects[0], argumentIo);

        convert(fvLongList);


        return ret;
    }

    @Override
    public String getDisplayString(String[] strings) {
        return "统计数组中每个字符出现的次数，以map形式返回";
    }

    private List<Long> getLongList(@Nonnull final DeferredObject arg, @Nonnull final ListObjectInspector listIO) throws HiveException {

        Object obj = arg.get();
        if (obj == null) {
            return null;
        }

        List<?> data = listIO.getList(obj);

        int size = data.size();

        if (size == 0) {
            return Collections.emptyList();
        }

        Long[] ary = new Long[size];

        for (int i = 0; i < size; i++) {
            Object o = data.get(i);
            if (o != null) {
                ary[i] = Long.parseLong(o.toString());
            }
        }

        return Arrays.asList(ary);
    }

    private void convert(List<Long> args) {

        args
                .stream()
                .collect(Collectors.groupingBy(e -> e))
                .entrySet()
                .forEach(new Consumer<Map.Entry<Long, List<Long>>>() {
                    @Override
                    public void accept(Map.Entry<Long, List<Long>> entry) {
//                        System.out.println("key = " + entry.getKey() + " ,value = " + entry.getValue().size());
                        ret.put(entry.getKey(), (long) entry.getValue().size());
                    }
                });

    }

}
