package com.amazingco.storage;

import com.amazingco.model.Backup;
import com.amazingco.model.Node;
import com.amazingco.serialization.Serializer;

import java.io.*;
import java.util.Objects;

public class FileNodeStorage implements NodeStorage {

    private final String path;

    public FileNodeStorage(String path) {
        Objects.requireNonNull(path);
        this.path = path;
    }

    @Override
    public synchronized void store(Node[] nodes) throws IOException {
        FileOutputStream stream = new FileOutputStream(path);
        Serializer.serialize(stream, nodes);
    }

    @Override
    public Backup get() throws IOException {
        FileInputStream stream = new FileInputStream(path);
        return Serializer.deserializeNodes(stream);
    }
}
