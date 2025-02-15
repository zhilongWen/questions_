package com.at.flatbuffer.test;

import com.at.flatbuffer.model.Gender;
import com.at.flatbuffer.model.Person;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {

        byte[] data = createPerson();

        readPerson(data);

        System.out.println("========================= test1 =========================");
//        test1(data);

        System.out.println("========================= parsePhone =========================");
        parsePhone(data);
    }

    static void parsePhone(byte[] data) {

        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // --------------------------------------------------
        // 根对象解析
        // --------------------------------------------------
        // 1. 获取根对象起始位置
        int rootPosition = bb.getInt(bb.position()) + bb.position();
        System.out.println("\n[Root Position] " + rootPosition);

        // 2. 获取vtable位置
        int vtableOffset = rootPosition - bb.getInt(rootPosition);
        System.out.println("[VTable Offset] " + vtableOffset);

        // 3. 获取vtable长度（以2字节为单位）
        int vtableSize = bb.getShort(vtableOffset) & 0xFFFF;
        System.out.println("[VTable Size] " + vtableSize + " shorts");

        // --------------------------------------------------
        // 字段解析规范
        // --------------------------------------------------
        // 字段索引映射：
        // 0: name (string)
        // 1: age (int)
        // 2: gender (byte)
        // 3: address (string)
        // 4: phone (int64)
        // 5: skills (vector)

        // --------------------------------------------------
        // 1. 解析 phone 字段
        // --------------------------------------------------
        int phoneFieldOffset = vtableOffset + 12; // 字段4在vtable中的位置
        int phoneOffsetInTable = bb.getShort(phoneFieldOffset) & 0xFFFF;

        if (phoneOffsetInTable != 0) {
            long phoneValue = bb.getLong(rootPosition + phoneOffsetInTable);
            System.out.println("[Manual Parsing] Phone: " + phoneValue);
        }

    }

    static void test1(byte[] data) {

        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // --------------------------------------------------
        // 根对象解析
        // --------------------------------------------------
        // 1. 获取根对象起始位置
        int rootPosition = bb.getInt(bb.position()) + bb.position();
        System.out.println("\n[Root Position] " + rootPosition);

        // 2. 获取vtable位置
        int vtableOffset = rootPosition - bb.getInt(rootPosition);
        System.out.println("[VTable Offset] " + vtableOffset);

        // 3. 获取vtable长度（以2字节为单位）
        int vtableSize = bb.getShort(vtableOffset) & 0xFFFF;
        System.out.println("[VTable Size] " + vtableSize + " shorts");

        // --------------------------------------------------
        // 字段解析规范
        // --------------------------------------------------
        // 字段索引映射：
        // 0: name (string)
        // 1: age (int)
        // 2: gender (byte)
        // 3: address (string)
        // 4: phone (int64)
        // 5: skills (vector)

        // --------------------------------------------------
        // 1. 解析 name 字段
        // --------------------------------------------------
        int nameFieldOffset = vtableOffset + 4; // 字段0在vtable中的位置
        int nameOffsetInTable = bb.getShort(nameFieldOffset) & 0xFFFF;

        if (nameOffsetInTable != 0) {
            int nameAddress = rootPosition + nameOffsetInTable;
            int stringOffset = bb.getInt(nameAddress);
            int stringStart = nameAddress + stringOffset;
            int stringLength = bb.getInt(stringStart);

            byte[] strBytes = new byte[stringLength];
            bb.position(stringStart + 4);
            bb.get(strBytes);
            System.out.println("\n[Manual Parsing] Name: " + new String(strBytes));
        }

        // --------------------------------------------------
        // 2. 解析 age 字段
        // --------------------------------------------------
        int ageFieldOffset = vtableOffset + 6; // 字段1在vtable中的位置
        int ageOffsetInTable = bb.getShort(ageFieldOffset) & 0xFFFF;

        if (ageOffsetInTable != 0) {
            int ageValue = bb.getInt(rootPosition + ageOffsetInTable);
            System.out.println("[Manual Parsing] Age: " + ageValue);
        }

        // --------------------------------------------------
        // 3. 解析 gender 字段
        // --------------------------------------------------
        int genderFieldOffset = vtableOffset + 8; // 字段2在vtable中的位置
        int genderOffsetInTable = bb.getShort(genderFieldOffset) & 0xFFFF;

        if (genderOffsetInTable != 0) {
            byte genderValue = bb.get(rootPosition + genderOffsetInTable);
            System.out.println("[Manual Parsing] Gender: " +
                    Gender.name(genderValue));
        }

        // --------------------------------------------------
        // 4. 解析 address 字段
        // --------------------------------------------------
        int addressFieldOffset = vtableOffset + 10; // 字段3在vtable中的位置
        int addressOffsetInTable = bb.getShort(addressFieldOffset) & 0xFFFF;

        if (addressOffsetInTable != 0) {
            int addressAddress = rootPosition + addressOffsetInTable;
            int stringOffset = bb.getInt(addressAddress);
            int stringStart = addressAddress + stringOffset;
            int stringLength = bb.getInt(stringStart);

            byte[] strBytes = new byte[stringLength];
            bb.position(stringStart + 4);
            bb.get(strBytes);
            System.out.println("[Manual Parsing] Address: " + new String(strBytes));
        }

        // --------------------------------------------------
        // 5. 解析 phone 字段
        // --------------------------------------------------
        int phoneFieldOffset = vtableOffset + 12; // 字段4在vtable中的位置
        int phoneOffsetInTable = bb.getShort(phoneFieldOffset) & 0xFFFF;

        if (phoneOffsetInTable != 0) {
            long phoneValue = bb.getLong(rootPosition + phoneOffsetInTable);
            System.out.println("[Manual Parsing] Phone: " + phoneValue);
        }

        // --------------------------------------------------
        // 6. 解析 skills 数组
        // --------------------------------------------------
        int skillsFieldOffset = vtableOffset + 14; // 字段5在vtable中的位置
        int skillsOffsetInTable = bb.getShort(skillsFieldOffset) & 0xFFFF;

        if (skillsOffsetInTable != 0) {
            int vectorAddress = rootPosition + skillsOffsetInTable;
            int vectorOffset = bb.getInt(vectorAddress);
            int vectorStart = vectorAddress + vectorOffset;

            int vectorLength = bb.getInt(vectorStart);
            System.out.println("\n[Manual Parsing] Skills (" + vectorLength + " items):");

            for (int i = 0; i < vectorLength; i++) {
                int elementOffset = vectorStart + 4 + i * 4;
                int stringOffsetInVector = bb.getInt(elementOffset);
                int stringStartInVector = vectorStart + stringOffsetInVector;

                int strLen = bb.getInt(stringStartInVector);
                byte[] strBytes = new byte[strLen];
                bb.position(stringStartInVector + 4);
                bb.get(strBytes);
                System.out.println(" - Skill " + (i + 1) + ": " + new String(strBytes));
            }
        }
    }

    static byte[] createPerson() {
        // 序列化：创建一个 Person 对象并序列化
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);

        // 创建 skills 数组
        int[] skills = {builder.createString("C++"), builder.createString("Java"), builder.createString("Python")};
        int skillsVector = Person.createSkillsVector(builder, skills);

        // 创建 Person 对象
//        int name = builder.createString("John Doe");
        int address = builder.createString("123 Main Street");
        long phone = 1234567890L;

        Person.startPerson(builder);
//        Person.addName(builder, name);
        Person.addAge(builder, 30);
        Person.addGender(builder, Gender.Male);
        Person.addAddress(builder, address);
        Person.addPhone(builder, phone);
        Person.addSkills(builder, skillsVector);

        int person = Person.endPerson(builder);

        builder.finish(person);

        return builder.sizedByteArray();
    }

    static void readPerson(byte[] data) {
        // 反序列化：从字节数组中反序列化一个 Person 对象
        Person person = Person.getRootAsPerson(ByteBuffer.wrap(data));

        // 获取字段值
        String nameValue = person.name();
        int ageValue = person.age();
        String genderValue = person.gender() == Gender.Male ? "Male" : "Female";
        String addressValue = person.address();
        long phoneValue = person.phone();
        String[] skillsValue = new String[person.skillsLength()];
        for (int i = 0; i < person.skillsLength(); i++) {
            skillsValue[i] = person.skills(i);
        }

        // 打印反序列化后的数据
        System.out.println("Name: " + nameValue);
        System.out.println("Age: " + ageValue);
        System.out.println("Gender: " + genderValue);
        System.out.println("Address: " + addressValue);
        System.out.println("Phone: " + phoneValue);
        System.out.println("Skills: " + String.join(", ", skillsValue));
    }
}
