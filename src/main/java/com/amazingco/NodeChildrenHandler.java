package com.amazingco;

import com.amazingco.model.Node;
import com.amazingco.storage.NodeChildrenHandlerStorage;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NodeChildrenHandler {

    private Node[] nodes;
    private BigInteger[] nodeChildren;
    private IdToIndexConverter idToIndexConverter;
    private final NodeChildrenHandlerStorage nodeChildrenStorage;
    private Semaphore semaphore = new Semaphore(1);

    public NodeChildrenHandler(NodeChildrenHandlerStorage nodeChildrenStorage, Node[] nodes) {
        Objects.requireNonNull(nodes);
        Objects.requireNonNull(nodeChildrenStorage);
        this.nodeChildrenStorage = nodeChildrenStorage;
        this.nodes = nodes;
        this.idToIndexConverter = new IdToIndexConverter(nodes);
        this.nodeChildren = getNodeChildrenIndexes();
    }

    public List<Node> getNodeChildren(int id) throws InterruptedException {
        int index = idToIndexConverter.convert(id);
        semaphore.acquire();
        BigInteger indexes = nodeChildren[index];
        semaphore.release();
        BitSet bitIndexes = BitSet.valueOf(indexes.toByteArray());
        IntStream stream = bitIndexes.stream();
        return stream.mapToObj(x -> nodes[x]).collect(Collectors.toList());
    }

    public void updateNodeChildrenIndexes(int sourceId, int targetId) throws InterruptedException {
        int sourceIndex = idToIndexConverter.convert(sourceId);
        int targetIndex = idToIndexConverter.convert(targetId);
        BigInteger sourceIndexes = nodeChildren[sourceIndex].xor(BigInteger.valueOf(2).pow(sourceIndex));
        boolean isRoot = nodes[sourceIndex].getRoot().getId() == nodes[sourceIndex].getId();
        semaphore.acquire();
        if (!isRoot) {
            int sourceParentIndex = idToIndexConverter.convert(nodes[sourceIndex].getParent().getId());
            updateNodeChildrenIndexes(sourceIndexes, sourceParentIndex, targetIndex);
        }
        updateNodeChildrenIndexes(sourceIndexes, targetIndex, sourceIndex);
        semaphore.release();
    }

    private BigInteger[] getNodeChildrenIndexes() {
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


    private void updateNodeChildrenIndexes(BigInteger sourceIndexes, int index, int oppositeIndex) {
        boolean isCommonAncestor = nodeChildren[index].testBit(oppositeIndex);
        if (isCommonAncestor)
            return;

        BigInteger newTargetIndexes = nodeChildren[index].xor(sourceIndexes);
        nodeChildren[index] = newTargetIndexes;
        if (nodes[index].getRoot().getId() != nodes[index].getId()) {
            int parentIndex = idToIndexConverter.convert(nodes[index].getParent().getId());
            updateNodeChildrenIndexes(sourceIndexes, parentIndex, oppositeIndex);
        }
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
            int parentIndex = idToIndexConverter.convert(nodes[i].getParent().getId());
            indexes[parentIndex] = sourceIndexes.xor(indexes[parentIndex]);
        }
        return indexes;
    }
}
