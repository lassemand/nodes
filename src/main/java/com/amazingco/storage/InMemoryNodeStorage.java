package com.amazingco.storage;

import com.amazingco.model.Node;

public class InMemoryNodeStorage implements NodeStorage {

    private Node[] nodes;

    @Override
    public void store(Node[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public Node[] get() {
        return nodes;
    }
}
