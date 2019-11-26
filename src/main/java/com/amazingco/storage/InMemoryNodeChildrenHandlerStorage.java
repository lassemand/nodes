package com.amazingco.storage;

import java.math.BigInteger;

public class InMemoryNodeChildrenHandlerStorage implements NodeChildrenHandlerStorage {

    private BigInteger[] nodeChildIndexes;

    public BigInteger[] get() {
        return nodeChildIndexes;
    }

    public void store(BigInteger[] nodeChildIndexes) {
        this.nodeChildIndexes = nodeChildIndexes;
    }
}
