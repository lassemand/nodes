package com.amazingco.storage;

import com.amazingco.model.Backup;
import com.amazingco.model.Node;

import java.io.IOException;

public interface NodeStorage {

    void store(Node[] nodes) throws IOException;
    Backup get() throws IOException;

}
