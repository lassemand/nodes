package com.amazingco;

import com.amazingco.http.HttpMessageHandler;
import com.amazingco.model.Node;
import com.amazingco.storage.InMemoryNodeStorage;
import com.amazingco.storage.NodeStorage;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Node root = new Node(0);
        Node node1 = new Node(1, 0);
        Node node2 = new Node(2, 0);
        Node node3 = new Node(3, 1);
        Node node4 = new Node(4, 1);
        Node node5 = new Node(5, 4);
        Node node6 = new Node(6, 5);
        Node[] nodes = new Node[]{node6, node5, node4, node3, node2, node1, root};
        NodeStorage storage = new InMemoryNodeStorage();
        NodeChildrenHandler nodeChildrenHandler = new NodeChildrenHandler(storage, nodes, root);
        new HttpMessageHandler(8080, nodeChildrenHandler);
    }

}
