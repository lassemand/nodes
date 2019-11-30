package com.amazingco;

import com.amazingco.http.HttpMessageHandler;
import com.amazingco.model.Node;
import com.amazingco.storage.InMemoryNodeStorage;
import com.amazingco.storage.NodeStorage;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Node root = new Node(0);
        Node node1 = new Node(1, root, root);
        Node node2 = new Node(2, root, root);
        Node node3 = new Node(3, node1, root);
        Node node4 = new Node(4, node1, root);
        Node node5 = new Node(5, node4, root);
        Node node6 = new Node(6, node5, root);
        Node[] nodes = new Node[]{node6, node5, node4, node3, node2, node1, root};
        NodeStorage storage = new InMemoryNodeStorage();
        NodeChildrenHandler nodeChildrenHandler = new NodeChildrenHandler(storage, nodes, root);
        new HttpMessageHandler(8080, nodeChildrenHandler);
    }

}
