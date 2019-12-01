package com.amazingco.model;

public class Node {

    private int id;
    private int height;
    private int parentId;
    private int rootId;

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

    public int getRootId() {
        return rootId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
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
}
