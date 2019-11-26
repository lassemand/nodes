package com.amazingco.model;

public class Node {

    private int id;
    private Node parent;
    private Node root;

    public Node(int id, Node parent, Node root) {
        this.id = id;
        this.parent = parent;
        this.root = root;
    }
    public Node(int id) {
        this.id = id;
        this.root = this;
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
