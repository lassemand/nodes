package com.amazingco.model;

public class Node {

    private int id;
    private Node parent;
    private Node root;

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
        return root;
    }

    public int getHeight() {
        if (getId() == getRoot().getId()) {
            return 0;
        }
        return getParent().getHeight() + 1;
    }
}
