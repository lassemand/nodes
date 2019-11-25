package com.amazingco.builder;

import java.math.BigInteger;

public interface NodeChildrenStorage {
     BigInteger[] get();
     void store(BigInteger[] handler);
}
