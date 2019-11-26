package com.amazingco.model;

import com.amazingco.NodeChildrenHandler;

public class Node {

    private int id;
    private Node parent;

    public Node(int id, Node parent, Node root) {
        this.id = id;
        this.parent = parent;
    }
    public Node(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Node getParent() {
        return parent;
    }

    public Node getRoot() {
        return NodeChildrenHandler.root;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getHeight() {
        if (getId() == getRoot().getId()) {
            return 0;
        }
        return getParent().getHeight() + 1;
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
