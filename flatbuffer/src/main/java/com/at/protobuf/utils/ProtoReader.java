package com.at.protobuf.utils;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.MessageLite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ProtoReader {

    private ByteBuffer buffer;
    private int pos;

    public static ProtoReader newInstance(final byte[] data) {
        return new ProtoReader(data, 0);
    }

    public ProtoReader(final byte[] data, final int offset) {
        this.buffer = ByteBuffer.allocate(data.length);
        this.buffer.put(data);
        this.pos = offset;
    }

    // 读取 Tag 并返回值和原始字节
    public TagReadResult readTag() {
        int startPos = pos;
        int tag = readRawVarint32();
        int endPos = pos;
        byte[] tagBytes = new byte[endPos - startPos];
        buffer.position(startPos);
        buffer.get(tagBytes);
        return new TagReadResult(tag, tagBytes);
    }

    static class TagReadResult {
        final int tag;
        final byte[] bytes;

        TagReadResult(int tag, byte[] bytes) {
            this.tag = tag;
            this.bytes = bytes;
        }
    }

    private int readRawVarint32() {
        int value = 0;
        int shift = 0;
        byte b;
        do {
            b = buffer.get(pos++);
            value |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return value;
    }

    private byte[] readBytes(int length) {
        byte[] data = new byte[length];
        buffer.get(data);
        pos += length;
        return data;
    }

    public FieldData readFieldPair() throws Exception {
        return readFieldPair(-1);
    }

    public FieldData readFieldPair(int fieldIndex) throws Exception {
        while (pos < buffer.limit()) {
            int startOffset = pos;

            // 1. 读取 Tag
            TagReadResult tagResult = readTag();
            if (tagResult.tag == 0) break;

            int fieldNumber = WireFormat.getTagFieldNumber(tagResult.tag);
            int wireType = WireFormat.getTagWireType(tagResult.tag);

            byte[] valueBytes;
            byte[] valueLengthBytes = new byte[0];

            // 2. 按 Wire Type 读取值
            switch (wireType) {
                case WireFormat.WIRETYPE_VARINT:
                    TagReadResult varintResult = readTag();
                    valueBytes = varintResult.bytes;
                    break;

                case WireFormat.WIRETYPE_FIXED64:
                    valueBytes = readBytes(8);
                    break;

                case WireFormat.WIRETYPE_LENGTH_DELIMITED:
                    // 读取 Length
                    TagReadResult lengthResult = readTag();
                    // 注意: 此处假设 Length 直接为 Varint 值
                    int length = lengthResult.tag;
                    valueLengthBytes = lengthResult.bytes;
                    valueBytes = readBytes(length);
                    break;

                case WireFormat.WIRETYPE_FIXED32:
                    valueBytes = readBytes(4);
                    break;

                default:
                    throw new Exception("Unsupported wire type: " + wireType);
            }

            // 3. 检查是否匹配目标字段
            if (fieldIndex <= 0 || fieldNumber == fieldIndex) {
                return new FieldData(
                        tagResult.bytes,
                        valueLengthBytes,
                        valueBytes,
                        tagResult.tag,
                        fieldNumber,
                        wireType,
                        startOffset
                );
            }
        }
        return FieldData.emptyInstance();
    }

    /**
     * 通用字段编码方法
     *
     * @param fieldNumber 字段号
     * @param wireType    Wire Type (0:VARINT, 1:FIXED64, 2:LENGTH_DELIMITED, 5:FIXED32)
     * @param value       字段值（类型需匹配 wireType）
     * @return 编码后的字节数组
     */
    public byte[] buildFieldData(int fieldNumber, int wireType, Object value) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        CodedOutputStream cos = CodedOutputStream.newInstance(bos);

        // 写入 Tag
        cos.writeTag(fieldNumber, wireType);

        // 根据 Wire Type 编码值
        switch (wireType) {
            case WireFormat.WIRETYPE_VARINT:
                if (value instanceof Long) {
                    cos.writeUInt64NoTag((Long) value);
                } else if (value instanceof Integer) {
                    cos.writeUInt32NoTag((Integer) value);
                } else {
                    throw new IllegalArgumentException("VARINT 类型需要 Long 或 Integer");
                }
                break;

            case WireFormat.WIRETYPE_FIXED64:
                if (value instanceof Long) {
                    cos.writeFixed64NoTag((Long) value);
                } else if (value instanceof Double) {
                    cos.writeDoubleNoTag((Double) value);
                } else {
                    throw new IllegalArgumentException("FIXED64 类型需要 Long 或 Double");
                }
                break;

            case WireFormat.WIRETYPE_LENGTH_DELIMITED:
                if (value instanceof byte[]) {
                    cos.writeByteArrayNoTag((byte[]) value);
                } else if (value instanceof MessageLite) {
                    cos.writeMessageNoTag((MessageLite) value);
                } else {
                    throw new IllegalArgumentException("LENGTH_DELIMITED 类型需要 byte[] 或 MessageLite");
                }
                break;

            case WireFormat.WIRETYPE_FIXED32:
                if (value instanceof Integer) {
                    cos.writeFixed32NoTag((Integer) value);
                } else if (value instanceof Float) {
                    cos.writeFloatNoTag((Float) value);
                } else {
                    throw new IllegalArgumentException("FIXED32 类型需要 Integer 或 Float");
                }
                break;

            default:
                throw new UnsupportedOperationException("不支持的 Wire Type: " + wireType);
        }

        cos.flush();
        return bos.toByteArray();
    }

    // 重载方法（简化常用场景）
    public byte[] buildFieldData(int fieldNumber, MessageLite message) throws IOException {
        return buildFieldData(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED, message);
    }

    public byte[] buildFieldData(int fieldNumber, long varintValue) throws IOException {
        return buildFieldData(fieldNumber, WireFormat.WIRETYPE_VARINT, varintValue);
    }

    public byte[] buildFieldData(int fieldNumber, double fixed64Value) throws IOException {
        return buildFieldData(fieldNumber, WireFormat.WIRETYPE_FIXED64, fixed64Value);
    }

    public byte[] buildFieldData(int fieldNumber, float fixed32Value) throws IOException {
        return buildFieldData(fieldNumber, WireFormat.WIRETYPE_FIXED32, fixed32Value);
    }
}
