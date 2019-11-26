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
    public static Node root;

    public NodeChildrenHandler(NodeChildrenHandlerStorage nodeChildrenStorage, Node[] nodes) {
        Objects.requireNonNull(nodes);
        Objects.requireNonNull(nodeChildrenStorage);
        this.nodeChildrenStorage = nodeChildrenStorage;
        this.nodes = nodes;
        this.idToIndexConverter = new IdToIndexConverter(nodes);
        this.nodeChildren = getNodeChildrenIndexes();
    }

    /**
     * Get the indexes of all node children and uses this indexes to get the nodes
     * This is locked such the no corrupted data may be obtained
     * @param id
     * @return
     * @throws InterruptedException
     */
    public List<Node> getNodeChildren(int id) throws InterruptedException {
        int index = idToIndexConverter.convert(id);
        semaphore.acquire();
        BigInteger indexes = nodeChildren[index];
        semaphore.release();
        BitSet bitIndexes = BitSet.valueOf(indexes.toByteArray());
        IntStream stream = bitIndexes.stream();
        List<Node> childNodes = stream.mapToObj(x -> nodes[x]).collect(Collectors.toList());
        setHeight(childNodes, getHeightOfNode(nodes[index]), nodes[index].getId());
        return childNodes;
    }

    private int getHeightOfNode(Node node) {
        int height = 0;
        while(node.getId() != node.getRoot().getId()) {
            height++;
            node = node.getParent();
        }
        return height;
    }

    /**
     * Updates the n x n matrix with corresponding bits. Gets a 64 factor speedup due to using bits as row
     * @param sourceId
     * @param targetId
     * @throws InterruptedException
     */
    public void updateNodeChildrenIndexes(int sourceId, int targetId) throws InterruptedException {
        int sourceIndex = idToIndexConverter.convert(sourceId);
        int targetIndex = idToIndexConverter.convert(targetId);
        BigInteger sourceIndexes = nodeChildren[sourceIndex].xor(BigInteger.valueOf(2).pow(sourceIndex));
        semaphore.acquire();
        boolean isSourceRoot = nodes[sourceIndex].getRoot().getId() == nodes[sourceIndex].getId();
        if (!isSourceRoot) {
            int sourceParentIndex = idToIndexConverter.convert(nodes[sourceIndex].getParent().getId());
            updateNodeChildrenIndexes(sourceIndexes, sourceParentIndex, targetIndex);
        } else {
            root = nodes[targetIndex];
        }
        updateNodeChildrenIndexes(sourceIndexes, targetIndex, sourceIndex);
        nodes[sourceIndex].setParent(nodes[targetIndex]);
        semaphore.release();
    }

    /**
     * This is \BigTheta(n) instead of placing the calculation on the Node, because it uses dynamic programming to reuse calculations
     * @param nodes
     * @param parentHeight
     * @param parentId
     */
    private void setHeight(List<Node> nodes, int parentHeight, int parentId) {
        int[] nodeHeights = new int[this.nodes.length];
        Map<Integer, List<Node>> waitingNodes = new HashMap<>();
        for (Node node: nodes) {
            int parentIndex = idToIndexConverter.convert(node.getParent().getId());
            int index = idToIndexConverter.convert(node.getId());
            if (node.getParent().getId() == parentId) {
                nodeHeights[index] = parentHeight + 1;
                notifyWaitingNodes(waitingNodes.getOrDefault(index, new ArrayList<>()), waitingNodes, nodeHeights, nodeHeights[index]);
            } else if (nodeHeights[parentIndex] > 0) {
                nodeHeights[index] = nodeHeights[parentIndex] + 1;
                notifyWaitingNodes(waitingNodes.getOrDefault(index, new ArrayList<>()), waitingNodes, nodeHeights, nodeHeights[index]);
            } else {
                List<Node> waitingNode = waitingNodes.getOrDefault(parentIndex, new ArrayList<>());
                waitingNode.add(node);
                waitingNodes.put(parentIndex, waitingNode);
            }
        }
        for (Node node: nodes) {
            node.setHeight(nodeHeights[idToIndexConverter.convert(node.getId())]);
        }
    }

    private void notifyWaitingNodes(List<Node> nodes, Map<Integer, List<Node>> waitingNodes, int[] nodeHeights, int nodeHeight) {
        for (Node waitingNode: nodes) {
            int index = idToIndexConverter.convert(waitingNode.getId());
            nodeHeights[index] = nodeHeight + 1;
            notifyWaitingNodes(waitingNodes.getOrDefault(index, new ArrayList<>()), waitingNodes, nodeHeights, nodeHeight + 1);
        }
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
        boolean isRoot = nodes[index].getRoot().getId() != nodes[index].getId();
        if (isRoot) {
            int parentIndex = idToIndexConverter.convert(nodes[index].getParent().getId());
            updateNodeChildrenIndexes(sourceIndexes, parentIndex, oppositeIndex);
        }
    }

    private BigInteger[] createNodeChildrenIndexes(Node[] nodes) {
        Objects.requireNonNull(nodes);
        BigInteger[] indexes = new BigInteger[nodes.length];
        Arrays.fill(indexes, BigInteger.ZERO);
        for (int i = 0; i<nodes.length; i++) {
            boolean isRoot = nodes[i].getParent() == null;
            if (isRoot) {
                root = nodes[i];
                continue;
            }
            BigInteger sourceIndexes = indexes[i].xor(BigInteger.valueOf(2).pow(i));
            int parentIndex = idToIndexConverter.convert(nodes[i].getParent().getId());
            indexes[parentIndex] = sourceIndexes.xor(indexes[parentIndex]);
        }
        return indexes;
    }
}
