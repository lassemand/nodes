package com.amazingco.serialization;

import com.amazingco.model.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Serializer {

    public static void serialize(OutputStream out, int value) throws IOException {
        writeInt(out, encodeZigZag(value));
    }

    private static int encodeZigZag(int value) {
        return value << 1 ^ value >> 31;
    }

    private static void writeInt(OutputStream out, int value) throws IOException {
        while((value & -128) != 0) {
            out.write((byte)(value & 127 | 128));
            value >>>= 7;
        }
        out.write((byte)value);
    }

    public static void serialize(OutputStream out, Node[] values) throws IOException {
        Objects.requireNonNull(values);
        serialize(out, values.length);
        for(int i = 0; i < values.length; ++i) {
            serialize(out, values[i].getId());
            serialize(out, values[i].getParentId());
            out.write(values[i].serialize());
        }
    }

    private static int decodeZigZag(int value) {
        return value >>> 1 ^ -(value & 1);
    }

    public static Node[] deserializeNodes(InputStream in) throws IOException {
        int len = deserializeInt(in);
        Node[] nodes = new Node[len];
        for (int i = 0; i<len; i++) {
            int id = deserializeInt(in);
            int parentId = deserializeInt(in);
            nodes[i] = new Node(id, parentId);
        }
        return nodes;
    }

    private static int readInt(InputStream in) throws IOException {
        int result = 0;
        for(int shift = 0; shift < 32 && in.available() >= 1; shift += 7) {
            byte b = (byte)in.read();
            result |= (b & 127) << shift;
            if ((b & 128) == 0) {
                return result;
            }
        }
        throw new IOException("Error decoding integer value");
    }

    public static int deserializeInt(InputStream in) throws IOException {
        return decodeZigZag(readInt(in));
    }
}