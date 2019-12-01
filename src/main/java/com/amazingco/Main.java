package com.amazingco;

import com.amazingco.handler.NodeChildrenHandler;
import com.amazingco.http.HttpMessageHandler;
import com.amazingco.model.Backup;
import com.amazingco.model.Configuration;
import com.amazingco.model.Node;
import com.amazingco.storage.FileNodeStorage;
import com.amazingco.storage.NodeStorage;
import com.beust.jcommander.JCommander;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
        JCommander.newBuilder()
                .addObject(configuration)
                .build()
                .parse(args);
        File f = new File(configuration.getBackupPath());
        NodeStorage storage = new FileNodeStorage(configuration.getBackupPath());
        NodeChildrenHandler nodeChildrenHandler;
        if (f.exists()) {
            Backup backup = storage.get();
            nodeChildrenHandler = new NodeChildrenHandler(backup.getNodes(), backup.getRoot());
        } else {
            Node[] nodes = buildTestNodes(20000);
            nodeChildrenHandler = new NodeChildrenHandler(nodes, nodes[19999]);
        }
        new HttpMessageHandler(8080, nodeChildrenHandler, storage);
    }

    private static Node[] buildTestNodes(int amount) {
        Node[] nodes = new Node[amount];
        for (int i = amount -1; i>=0; i--) {
            nodes[i] = i == amount - 1 ? new Node(i) : new Node(i, nodes[i+1].getId());
        }
        return nodes;
    }
}
