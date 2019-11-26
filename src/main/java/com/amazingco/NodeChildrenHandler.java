package com.amazingco;

import com.amazingco.model.Node;
import com.amazingco.storage.NodeChildrenHandlerStorage;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NodeChildrenHandler {

    private Node[] nodes;
    private BigInteger[] nodeChildren;
    private Map<Integer, Integer> idToIndex;
    private final NodeChildrenHandlerStorage nodeChildrenStorage;

    public NodeChildrenHandler(NodeChildrenHandlerStorage nodeChildrenStorage, Node[] nodes) {
        Objects.requireNonNull(nodes);
        Objects.requireNonNull(nodeChildrenStorage);
        this.nodeChildrenStorage = nodeChildrenStorage;
        this.nodes = nodes;
        this.idToIndex = buildIdToIndexes(nodes);
    }

    private Map<Integer, Integer> buildIdToIndexes(Node[] nodes) {
        Map<Integer, Integer> idToIndex = new HashMap<>();
        for (int i = 0; i<nodes.length; i++) {
            idToIndex.put(nodes[i].getId(), i);
        }
        return idToIndex;
    }

    public BigInteger[] getNodeChildrenIndexes() {
        boolean isCached = nodeChildren != null;
        if (isCached) {
            return nodeChildren;
        }
        BigInteger[] nodeChildrenIndexes = nodeChildrenStorage.get();
        boolean isStored = nodeChildrenIndexes != null;
        if (isStored) {
            return nodeChildrenIndexes;
        }
        nodeChildren = createNodeChildrenIndexes(nodes);
        nodeChildrenStorage.store(nodeChildren);
        return nodeChildren;
    }

    public synchronized void updateNodeChildrenIndexes(int sourceId, int targetId) {
        nodeChildren = getNodeChildrenIndexes();
        int sourceIndex = idToIndex.get(sourceId);
        int targetIndex = idToIndex.get(targetId);
        BigInteger sourceIndexes = nodeChildren[sourceIndex].xor(BigInteger.valueOf(2).pow(sourceIndex));
        boolean isRoot = nodes[sourceIndex].getRoot().getId() == nodes[sourceIndex].getId();
        if (!isRoot) {
            int sourceParentIndex = idToIndex.get(nodes[sourceIndex].getParent().getId());
            updateNodeChildrenIndexes(sourceIndexes, sourceParentIndex, targetIndex);
        }
        updateNodeChildrenIndexes(sourceIndexes, targetIndex, sourceIndex);
    }

    private void updateNodeChildrenIndexes(BigInteger sourceIndexes, int index, int oppositeIndex) {
        boolean isCommonAncestor = nodeChildren[index].testBit(oppositeIndex);
        if (isCommonAncestor)
            return;

        BigInteger newTargetIndexes = nodeChildren[index].xor(sourceIndexes);
        nodeChildren[index] = newTargetIndexes;
        int parentIndex = idToIndex.get(nodes[index].getParent().getId());
        updateNodeChildrenIndexes(sourceIndexes, parentIndex, oppositeIndex);
    }

    private BigInteger[] createNodeChildrenIndexes(Node[] nodes) {
        Objects.requireNonNull(nodes);
        BigInteger[] indexes = new BigInteger[nodes.length];
        Arrays.fill(indexes, BigInteger.ZERO);
        for (int i = 0; i<nodes.length; i++) {
            boolean isRoot = nodes[i].getRoot().getId() == nodes[i].getId();
            if (isRoot)
                continue;

            BigInteger sourceIndexes = indexes[i].xor(BigInteger.valueOf(2).pow(i));
            int parentIndex = idToIndex.get(nodes[i].getParent().getId());
            indexes[parentIndex] = sourceIndexes.xor(indexes[parentIndex]);
        }
        return indexes;
    }
}
