package com.amazingco.builder;

import java.math.BigInteger;

public class InMemoryNodeChildrenHandlerStorage implements NodeChildrenStorage {

    private BigInteger[] nodeChildIndexes;

    public BigInteger[] get() {
        return nodeChildIndexes;
    }

    public void store(BigInteger[] nodeChildIndexes) {
        this.nodeChildIndexes = nodeChildIndexes;
    }
}
