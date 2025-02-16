package com.at.flatbuffer.test;

import com.at.flatbuffer.model.Gender;
import com.at.flatbuffer.model.KeyValue;
import com.at.flatbuffer.model.Person;
import com.at.flatbuffer.model.Work;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {

        byte[] data = createPerson();

        // --------------------------------------------------------------
        // 反序列化
        // --------------------------------------------------------------

         System.out.println("========================= parse =========================");
         parse(data);

//        test1(data);

        System.out.println("========================= parse1 =========================");
        parse1(data);

    }

    public static void parse1(byte[] data) {
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
        // 1. 解析 name 字段
        // --------------------------------------------------
        int nameOffsetInTable = getFieldOffset(bb, vtableOffset, 1);
        if (nameOffsetInTable != 0) {
            int nameAddress = rootPosition + nameOffsetInTable;
            String name = readString(bb, nameAddress);
            System.out.println("\n[Manual Parsing] Name: " + name);
        }

        // --------------------------------------------------
        // 2. 解析 age 字段
        // --------------------------------------------------
        int ageOffsetInTable = getFieldOffset(bb, vtableOffset, 2);
        if (ageOffsetInTable != 0) {
            int ageValue = bb.getInt(rootPosition + ageOffsetInTable);
            System.out.println("[Manual Parsing] Age: " + ageValue);
        }

        // --------------------------------------------------
        // 3. 解析 gender 字段
        // --------------------------------------------------
        int genderOffsetInTable = getFieldOffset(bb, vtableOffset, 3);
        if (genderOffsetInTable != 0) {
            byte genderValue = bb.get(rootPosition + genderOffsetInTable);
            System.out.println("[Manual Parsing] Gender: " +
                    Gender.name(genderValue));
        }

        // --------------------------------------------------
        // 4. 解析 address 字段
        // --------------------------------------------------
        int addressOffsetInTable = getFieldOffset(bb, vtableOffset, 4);
        if (addressOffsetInTable != 0) {
            int addressAddress = rootPosition + addressOffsetInTable;
            String address = readString(bb, addressAddress);
            System.out.println("[Manual Parsing] Address: " + address);
        }

        // 6. 解析 phone 字段
        // 字段 5：phone 字段
        int phoneOffset = getFieldOffset(bb, vtableOffset, 5);
        if (phoneOffset != 0) {
            long phone = bb.getLong(rootPosition + phoneOffset);
            System.out.println("Phone: " + phone);
        }

        // ---------------------------------------------------
        // 解析嵌套的数组/对象（例如 works 和 skills）
        // ---------------------------------------------------

        // 7. 解析 skills 字段（string 数组）
        // 字段 6：skills 字段（数组）
        int skillsOffset = getFieldOffset(bb, vtableOffset, 6);  // 获取字段6（skills）偏移量
        if (skillsOffset != 0) {
            // 计算实际的数组起始地址
            int skillsAddr = rootPosition + skillsOffset;
            // 获取数组长度
            int skillsLength = bb.getInt(skillsAddr);

            // 确保 skillsLength 在有效范围内
            if (skillsLength < 0 || skillsLength > 1000) {
                System.out.println("Invalid skills length: " + skillsLength);
                return;
            }

            System.out.println("Skills length: " + skillsLength);

            // 逐个读取每个 skill 字符串
            for (int i = 0; i < skillsLength; i++) {
                // 计算当前 skill 字符串的偏移量
                int skillOffset = bb.getInt(skillsAddr + 4 + i * 4);  // 获取每个 skill 字符串的偏移量

                // 检查偏移量是否有效
                if (skillOffset < 0 || skillOffset > bb.capacity()) {
                    System.out.println("Invalid skill offset: " + skillOffset);
                    return;
                }

                // 读取并打印 skill 字符串
                String skill = readString(bb, rootPosition + skillOffset);
                System.out.println("Skill: " + skill);
            }
        }


    }

    // 读取字符串字段
    static String readString(ByteBuffer bb, int address) {
        // 获取字符串偏移量
        int stringOffset = bb.getInt(address);
        int stringAddr = address + stringOffset;
        // 获取字符串长度
        int stringLength = bb.getInt(stringAddr);
        byte[] strBytes = new byte[stringLength];
        // 跳过字符串长度字段
        bb.position(stringAddr + 4);
        bb.get(strBytes);
        return new String(strBytes);
    }

    // 获取 vtable 中字段的偏移量
    static int getFieldOffset(ByteBuffer bb, int vtableOffset, int fieldIndex) {
        // 获取字段偏移量
        return bb.getShort(vtableOffset + 2 + fieldIndex * 2) & 0xFFFF;
    }

    // ---------------------


    public static void parse(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);

        // 从 byte buffer 中读取 Person 对象
        Person personObj = Person.getRootAsPerson(bb);

        // 打印反序列化后的数据
        System.out.println("Name: " + personObj.name());
        System.out.println("Age: " + personObj.age());
        System.out.println("Gender: " + personObj.gender());
        System.out.println("Address: " + personObj.address());
        System.out.println("Phone: " + personObj.phone());

        // 读取 skills
        System.out.println("Skills: ");
        for (int i = 0; i < personObj.skillsLength(); i++) {
            System.out.println("  - " + personObj.skills(i));
        }

        // 读取 works
        System.out.println("Works: ");
        for (int i = 0; i < personObj.worksLength(); i++) {
            Work work = personObj.works(i);
            System.out.println("  - Company: " + work.company() +
                    ", Position: " + work.position() +
                    ", Start Date: " + work.startDate() +
                    ", End Date: " + work.endDate());
        }

        // 读取 additional_info (KeyValue)
        System.out.println("Additional Info: ");
        for (int i = 0; i < personObj.additionalInfoLength(); i++) {
            KeyValue keyValue = personObj.additionalInfo(i);
            Work additionalWork = keyValue.value();
            System.out.println("  - Key: " + keyValue.key() +
                    ", Company: " + additionalWork.company() +
                    ", Position: " + additionalWork.position());
        }
    }

    public static byte[] createPerson() {
        // 创建 FlatBufferBuilder 用于构建序列化数据
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);

        // 1. 序列化 skills 数组
        int skill1 = builder.createString("Java");
        int skill2 = builder.createString("Python");
        int skillVector = Person.createSkillsVector(builder, new int[]{skill1, skill2});

        // 2. 序列化 works 数组
        int work1 = createWork(builder, 1633036800L, "Company A", "Developer", "Developing software", 1609459200L, 1633036800L);
        int work2 = createWork(builder, 1633123200L, "Company B", "Senior Developer", "Leading development", 1612137600L, 1633219200L);
        int worksVector = Person.createWorksVector(builder, new int[]{work1, work2});

        // 3. 序列化 additional_info map（KeyValue 数组）
        int key1 = builder.createString("Key1");
        int key2 = builder.createString("Key2");
        int value1 = work1; // Work 对象
        int value2 = work2; // Work 对象
        int additionalInfoVector = Person.createAdditionalInfoVector(builder, new int[]{
                createKeyValue(builder, key1, value1),
                createKeyValue(builder, key2, value2)
        });

        // 4. 序列化 Person 对象
        int name = builder.createString("John Doe");
        int address = builder.createString("1234 Elm St");
        long phone = 1234567890L;

        Person.startPerson(builder);
        Person.addName(builder, name);
        Person.addAge(builder, 30);
        Person.addGender(builder, Gender.Male);  // Gender 枚举
        Person.addAddress(builder, address);
        Person.addPhone(builder, phone);
        Person.addSkills(builder, skillVector);
        Person.addWorks(builder, worksVector);
        Person.addAdditionalInfo(builder, additionalInfoVector);
        int person = Person.endPerson(builder);

        builder.finish(person);

        // 获取序列化后的数据
        return builder.sizedByteArray();
    }

    // Helper methods to create nested objects
    private static int createWork(FlatBufferBuilder builder, long timestamp, String company, String position, String description, long startDate, long endDate) {
        int companyOffset = builder.createString(company);
        int positionOffset = builder.createString(position);
        int descriptionOffset = builder.createString(description);
        Work.startWork(builder);
        Work.addTimestamp(builder, timestamp);
        Work.addCompany(builder, companyOffset);
        Work.addPosition(builder, positionOffset);
        Work.addDescription(builder, descriptionOffset);
        Work.addStartDate(builder, startDate);
        Work.addEndDate(builder, endDate);
        return Work.endWork(builder);
    }

    private static int createKeyValue(FlatBufferBuilder builder, int key, int value) {
        KeyValue.startKeyValue(builder);
        KeyValue.addKey(builder, key);
        KeyValue.addValue(builder, value);
        return KeyValue.endKeyValue(builder);
    }
}
