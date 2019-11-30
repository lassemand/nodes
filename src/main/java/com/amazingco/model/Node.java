package com.amazingco.model;

import com.amazingco.NodeChildrenHandler;
import com.amazingco.serialization.Serializer;

import java.io.ByteArrayOutputStream;

public class Node {

    private int id;
    private int height;
    private int parentId;

    public Node(int id, int parentId) {
        this.id = id;
        this.parentId = parentId;
    }
    public Node(int id) {
        this.id = id;
        this.parentId = -1;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Integer) {
            return id == (int)obj;
        }
        if (!(obj instanceof Node)) {
            return false;
        }
        Node node = (Node) obj;
        return node.getId() == id;
    }

    public byte[] serialize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        return out.toByteArray();
    }

}
