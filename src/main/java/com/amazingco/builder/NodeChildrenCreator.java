package com.amazingco.builder;

import com.amazingco.model.Node;

import java.math.BigInteger;
import java.util.Objects;

public class NodeChildrenCreator {

    private Node[] nodes;
    private BigInteger[] nodeChildren;
    private final NodeChildrenStorage nodeChildrenStorage;

    public NodeChildrenCreator(NodeChildrenStorage nodeChildrenStorage) {
        this.nodeChildrenStorage = nodeChildrenStorage;
    }

    public BigInteger[] getNodeChildrenIndexes(Node[] nodes) {
        this.nodes = nodes;
        if (nodeChildren != null) {
            return nodeChildren;
        }
        BigInteger[] nodeChildrenIndexes = nodeChildrenStorage.get();
        if (nodeChildrenIndexes != null) {
            return nodeChildrenIndexes;
        }
        nodeChildren = createNodeChildrenIndexes(nodes);
        nodeChildrenStorage.store(nodeChildren);
        return nodeChildren;
    }

    public synchronized void updateNodeChildrenIndexes(int sourceIndex, int targetIndex) {
        if (nodeChildren == null) {
            throw new RuntimeException("UpdateChildrenIndexes called while not being initialized");
        }
        BigInteger newTargetIndexes = nodeChildren[targetIndex].xor(nodeChildren[sourceIndex]);
        nodeChildren[targetIndex] = newTargetIndexes;
        if (nodes[targetIndex].getRoot().getId() != nodes[targetIndex].getId()) {
            int parentIndex = nodes[targetIndex].getParent().getId();
            updateNodeChildrenIndexes(sourceIndex, parentIndex);
        }
    }

    private BigInteger[] createNodeChildrenIndexes(Node[] nodes) {
        Objects.requireNonNull(nodes);
        BigInteger[] indexes = new BigInteger[nodes.length];
        for (int i = 0; i<nodes.length; i++) {
            BigInteger sourceIndexes = BigInteger.valueOf(2).pow(i);
            BigInteger targetIndexes = sourceIndexes.xor(indexes[i]);
            indexes[nodes[i].getParent().getId()] = targetIndexes;
        }
        return indexes;
    }
}
