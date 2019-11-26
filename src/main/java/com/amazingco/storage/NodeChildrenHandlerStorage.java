package com.amazingco.storage;

import java.math.BigInteger;

public interface NodeChildrenHandlerStorage {
     BigInteger[] get();
     void store(BigInteger[] handler);
}
