package com.at.protobuf.test;

import com.at.protobuf.model.Gender;
import com.at.protobuf.model.Person;
import com.at.protobuf.model.Work;
import com.at.protobuf.utils.ArrayDecoder;
import com.at.protobuf.utils.FieldData;
import com.at.protobuf.utils.ProtoReader;
import com.at.protobuf.utils.WireFormat;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {

        // 构建一个 Person 对象并序列化为字节数组
        Person person = Person.newBuilder()
                .setName("John Doe")
                .setAge(30)
                .setGender(Gender.Male)
                .setAddress("123 Main Street")
                .setPhone(1234567890L)
                .addSkills("Java")
                .addSkills("Python")
                .addSkills("C++")
                .addAllWorks(
                        Arrays.asList(
                                Work.newBuilder()
                                        .setTimestamp(20241231)
                                        .setCompany("Google")
                                        .setPosition("Software Engineer")
                                        .build(),
                                Work.newBuilder()
                                        .setTimestamp(20250216)
                                        .setCompany("Google")
                                        .setPosition("Product Manager")
                                        .build()
                        )
                )
                .putAllAdditionalInfo(
                        new HashMap<String, Work>(2) {{
                            put("Java", Work.newBuilder().setTimestamp(20250216).setCompany("Google").build());
                            put("C++", Work.newBuilder().setTimestamp(20250216).setCompany("Google").build());
                        }}
                )
                .build();

//        Person.parseFrom()

        // 获取序列化后的字节数组
        byte[] data = person.toByteArray();

//        System.out.println("======================= parse =======================");
        Person pp = Person.parseFrom(data);
//        System.out.println(pp.getName());
//        System.out.println(pp.getAge());
//        System.out.println(pp.getGender());
//        System.out.println(pp.getAddress());
//        System.out.println(pp.getPhone());
//        System.out.println(pp.getSkillsList());

//        System.out.println("======================= parse1 =======================");
//        parse1(data);

//        System.out.println("======================= parse2 =======================");
//        parse2(data);
//
//        System.out.println("======================= parse3 =======================");
//        parse3(data);

//        System.out.println("======================= parse4 =======================");
//        parse4(data);

//        System.out.println("======================= parse5 =======================");
//        parse5(data);

        System.out.println("======================= parse6 =======================");
        parse6(data);

    }

    public static void parse6(byte[] data) throws Exception {

        ProtoReader reader = ProtoReader.newInstance(data);

//        FieldData fieldData = reader.readFieldPair(7);
//        Person person = Person.parseFrom(fieldData.toByteArray());
//        System.out.println("[ArrayDecoder] " + person);
//
//        Work work = Work.parseFrom(fieldData.getValueBytes());
//        System.out.println("[ArrayDecoder] " + work);

        System.out.println("--------------");

        Work newWork = Work.newBuilder()
//                .setTimestamp(20250101)
//                .setTimestamp(20241230)
                .setTimestamp(20250217)
                .setCompany("Alibaba")
                .setPosition("Software Engineer")
                .build();
        byte[] insertValue = newWork.toByteArray();

        // 编码为 Protocol Buffers 格式的字段7条目
        ByteArrayOutputStream fieldStream = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(fieldStream);
        cos.writeTag(7, WireFormat.WIRETYPE_LENGTH_DELIMITED);
        cos.writeMessageNoTag(newWork);
        cos.flush();
//        byte[] insertBytes = fieldStream.toByteArray();

        // 2. 生成字段7的完整编码字节
        byte[] insertBytes = reader.buildFieldData(7, newWork);

        int indexPos = -1;
        boolean done = false;
        while (!done) {
            FieldData fieldData1 = reader.readFieldPair(7);
            if (fieldData1.isEmpty()) {
                done = true;
            } else {
                Work work1 = Work.parseFrom(fieldData1.getValueBytes());
                if (work1.getTimestamp() >= newWork.getTimestamp()) {
                    indexPos = fieldData1.getStartOffset();
                    done = true;
                }
            }
        }

        byte[] newData = data;
        if (indexPos < 0) {
            // 直接插入末端
            newData = ArrayUtils.addAll(newData, insertBytes);
        } else if (indexPos == 0) {
            // 直接插入最前面
            newData = ArrayUtils.addAll(newData, insertBytes);
        } else {
            // 找到插入的位置，直接插入指定位置
            newData = ArrayUtils.insert(indexPos, newData, insertBytes);
        }

        Person person = Person.parseFrom(newData);
        System.out.println("[ArrayDecoder Person] " + person);
        List<Work> worksList = person.getWorksList();
        for (Work work : worksList) {
            System.out.println("[ArrayDecoder Work] " + work);
        }

    }

    public static void parse5(byte[] data) throws Exception {

        ProtoReader reader = ProtoReader.newInstance(data);

//        System.out.println("-------------");
//        FieldData fieldData = reader.readFieldPair(5);
//        Person person = Person.parseFrom(fieldData.toByteArray());
//        System.out.println("Person: " + person);

        boolean done = false;
        while (!done) {
            FieldData fieldData = reader.readFieldPair();
            if (fieldData.isEmpty()) {
                done = true;
            } else {
                Person person = Person.parseFrom(fieldData.toByteArray());
                System.out.println("[ArrayDecoder] " + person);
            }
        }

        System.out.println("-------------");
        FieldData fieldData = reader.readFieldPair(1);
        Person person = Person.parseFrom(fieldData.toByteArray());
        System.out.println("[ArrayDecoder] " + person);

        fieldData = reader.readFieldPair(2);
        person = Person.parseFrom(fieldData.toByteArray());
        System.out.println("[ArrayDecoder] " + person);
    }

    public static void parse4(byte[] data) throws Exception {

//        System.out.println(Person.parseFrom(data));
//        System.out.println("====");

        ArrayDecoder decoder = ArrayDecoder.newInstance(data);

//        FieldData fieldData = decoder.readFieldPair();
//        System.out.println(Person.parseFrom(fieldData.toByteArray()));
//        FieldData fieldData1 = decoder.readFieldPair();
//        System.out.println(Person.parseFrom(fieldData1.toByteArray()));

//        FieldData fieldData = decoder.readFieldPair(1);
//        System.out.println(Person.parseFrom(fieldData.toByteArray()));

//        FieldData fieldData2 = decoder.readFieldPair(2);
//        System.out.println(Person.parseFrom(fieldData2.toByteArray()));


        boolean done = false;
        while (!done) {
            FieldData fieldData = decoder.readFieldPair();
            if (fieldData.isEmpty()) {
                done = true;
            } else {
                Person person = Person.parseFrom(fieldData.toByteArray());
                System.out.println("[ArrayDecoder] " + person);
            }
        }

    }

    public static void parse3(byte[] data) throws IOException {
        // 创建一个 CodedInputStream 实例
        CodedInputStream input = CodedInputStream.newInstance(data);

        // 使用 ArrayDecoder 解析数据
        ArrayDecoder decoder = new ArrayDecoder(data, 0, data.length, false);

        // 手动解析 'address' 字段，假设它的编号是 5
        String address = null;
        while (!input.isAtEnd()) {
            int tag = input.readTag();
            int fieldNumber = tag >> 3;  // 获取字段编号

            if (fieldNumber == 4) {  // Protobuf 中 address 字段的编号是 4
                // 使用 ArrayDecoder 提取地址字段的字节数组
                byte[] addressBytes = decoder.readByteArray();
                // 将字节数组转为字符串
                address = new String(addressBytes);
                System.out.println("[Manual Parsing] Address: " + address);
                break;  // 解析完成后退出
            } else {
                // 跳过当前字段
                input.skipField(tag);
            }
        }

        // 如果没有找到 address 字段
        if (address == null) {
            System.out.println("Address field not found.");
        }
    }

    public static void parse2(byte[] data) throws IOException {
        // 使用 ByteBuffer 包装字节数组
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // 使用 CodedInputStream 解码 Protobuf 数据
        CodedInputStream input = CodedInputStream.newInstance(new ByteArrayInputStream(data));

        while (!input.isAtEnd()) {
            // 获取下一个字段的标签
            int tag = input.readTag();
            // 获取字段编号
            int fieldNumber = tag >> 3;

            if (fieldNumber == 5) {
                long phone = input.readInt64();
                System.out.println("[Manual Parsing] Phone: " + phone);
                break;
            } else {
                // 跳过当前字段
                input.skipField(tag);
            }
        }
    }

    public static void parse1(byte[] data) throws IOException {
        // 使用 ByteBuffer 包装字节数组
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // 使用 CodedInputStream 解码 Protobuf 数据
        CodedInputStream input = CodedInputStream.newInstance(new ByteArrayInputStream(data));

        while (!input.isAtEnd()) {
            // 获取下一个字段的标签
            int tag = input.readTag();
            int fieldNumber = tag >> 3;  // 获取字段编号

            // 根据字段编号判断字段类型并解析
            switch (fieldNumber) {
                case 1: // name (string)
                    String name = input.readString();
                    System.out.println("[Manual Parsing] Name: " + name);
                    break;
                case 2: // age (int32)
                    int age = input.readInt32();
                    System.out.println("[Manual Parsing] Age: " + age);
                    break;
                case 3: // gender (enum)
                    int genderValue = input.readEnum();
                    Gender gender = Gender.forNumber(genderValue);
                    System.out.println("[Manual Parsing] Gender: " + gender);
                    break;
                case 4: // address (string)
                    String address = input.readString();
                    System.out.println("[Manual Parsing] Address: " + address);
                    break;
                case 5: // phone (int64)
                    long phone = input.readInt64();
                    System.out.println("[Manual Parsing] Phone: " + phone);
                    break;
                case 6: // skills (repeated string)
                    String skill = input.readString();
                    System.out.println("[Manual Parsing] Skill: " + skill);
                    break;
                default:
                    // 跳过当前字段
                    input.skipField(tag);
                    break;
            }
        }
    }
}
