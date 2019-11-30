package com.amazingco.storage;

import com.amazingco.model.Node;

public interface NodeStorage {

    void store(Node[] nodes);
    Node[] get();

}
