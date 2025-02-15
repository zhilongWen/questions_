package com.at.protobuf.utils;

import org.apache.commons.lang3.ArrayUtils;


/**
 * @author wenzhilong
 */
public class FieldData {

    private final byte[] tagBytes;
    private final byte[] lengthBytes;
    private final byte[] valueBytes;

    private final int tag;
    private final int fieldNumber;
    private final int tagType;

    private final int startOffset;

    private final int length;

    private static final FieldData EMPTY_INSTANCE = new FieldData(new byte[0], new byte[0], new byte[0], 0, 0, 0, 0);

    public FieldData(byte[] tagBytes, byte[] lengthBytes, byte[] valueBytes,
                     int tag, int fieldNumber, int tagType, int startOffset) {
        this.tagBytes = tagBytes;
        this.lengthBytes = lengthBytes;
        this.valueBytes = valueBytes;
        this.tag = tag;
        this.fieldNumber = fieldNumber;
        this.tagType = tagType;
        this.startOffset = startOffset;

        this.length = tagBytes.length + lengthBytes.length + valueBytes.length;

    }

    public static FieldData emptyInstance() {
        return EMPTY_INSTANCE;
    }

    public boolean isEmpty() {
        return tag == 0;
    }


    public byte[] getTagBytes() {
        return tagBytes;
    }

    public byte[] getLengthBytes() {
        return lengthBytes;
    }

    public byte[] getValueBytes() {
        return valueBytes;
    }

    public int getTag() {
        return tag;
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public int getTagType() {
        return tagType;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getLength() {
        return length;
    }

    public byte[] toByteArray() {
        if (lengthBytes.length > 0) {
            return ArrayUtils.addAll(ArrayUtils.addAll(tagBytes, lengthBytes), valueBytes);
        }

        return ArrayUtils.addAll(tagBytes, valueBytes);
    }

}
