package com.amazingco.model;

import java.util.Objects;

public class Backup {

    private final Node root;
    private final Node[] nodes;

    public Backup(Node root, Node[] nodes) {
        Objects.requireNonNull(nodes);
        this.root = root;
        this.nodes = nodes;
    }

    public Node getRoot() {
        return root;
    }

    public Node[] getNodes() {
        return nodes;
    }
}
