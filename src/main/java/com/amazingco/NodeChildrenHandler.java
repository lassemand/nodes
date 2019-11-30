package com.amazingco;

import com.amazingco.model.Node;
import com.amazingco.storage.NodeStorage;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NodeChildrenHandler {

    private Node[] nodes;
    private BigInteger[] nodeChildren;
    private IdToIndexConverter idToIndexConverter;
    private final NodeStorage nodeStorage;
    private Semaphore semaphore = new Semaphore(1);
    public static Node root;

    public NodeChildrenHandler(NodeStorage nodeStorage, Node[] nodes, Node root) {
        Objects.requireNonNull(nodes);
        Objects.requireNonNull(nodeStorage);
        this.nodeStorage = nodeStorage;
        this.root = root;
        this.nodes = sortByChildrenFirst(nodes, root);
        this.idToIndexConverter = new IdToIndexConverter(nodes);
        this.nodeChildren = createNodeChildrenIndexes(nodes);
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
        Node[] childNodesArray = new Node[childNodes.size()];
        Map<Integer, Integer> heights = getHeights(childNodes.toArray(childNodesArray), getHeightOfNode(nodes[index]), id);
        for (Node node: childNodes) {
            node.setHeight(heights.get(node.getId()));
        }
        return childNodes;
    }

    private int getHeightOfNode(Node node) {
        int height = 0;
        while(node.getId() != node.getRoot().getId()) {
            height++;
            node = nodes[idToIndexConverter.convert(node.getParentId())];
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
            int sourceParentIndex = idToIndexConverter.convert(nodes[sourceIndex].getParentId());
            updateNodeChildrenIndexes(sourceIndexes, sourceParentIndex, targetIndex);
        } else {
            root = nodes[targetIndex];
        }
        updateNodeChildrenIndexes(sourceIndexes, targetIndex, sourceIndex);
        nodes[sourceIndex].setParentId(nodes[targetIndex].getId());
        nodeStorage.store(nodes);
        semaphore.release();
    }

    /**
     * This is \BigTheta(n) instead of placing the calculation on the Node, because it uses dynamic programming to reuse calculations
     * @param nodes
     * @param parentHeight
     * @param parentId
     */
    private Map<Integer, Integer> getHeights(Node[] nodes, int parentHeight, int parentId) {
        Map<Integer, Integer> nodeHeights = new HashMap<>();
        Map<Integer, List<Node>> waitingNodes = new HashMap<>();
        for (Node node: nodes) {
            if (node.getId() == root.getId()){
                nodeHeights.put(node.getId(), 0);
                notifyWaitingNodes(waitingNodes.getOrDefault(node.getId(), new ArrayList<>()), waitingNodes, nodeHeights);
                continue;
            }
            if (node.getParentId() == parentId) {
                nodeHeights.put(node.getId(), parentHeight + 1);
                notifyWaitingNodes(waitingNodes.getOrDefault(node.getId(), new ArrayList<>()), waitingNodes, nodeHeights);
            } else if (nodeHeights.getOrDefault(node.getParentId(), -1) > 0) {
                nodeHeights.put(node.getId(), nodeHeights.get(node.getParentId()) + 1);
                notifyWaitingNodes(waitingNodes.getOrDefault(node.getId(), new ArrayList<>()), waitingNodes, nodeHeights);
            } else {
                List<Node> waitingNode = waitingNodes.getOrDefault(node.getParentId(), new ArrayList<>());
                waitingNode.add(node);
                waitingNodes.put(node.getParentId(), waitingNode);
            }
        }
        return nodeHeights;
    }

    private void notifyWaitingNodes(List<Node> nodes, Map<Integer, List<Node>> waitingNodes, Map<Integer, Integer> nodeHeights) {
        LinkedList<List<Node>> waitingNodesQueue = new LinkedList<>();
        waitingNodesQueue.addFirst(nodes);
        while(!waitingNodesQueue.isEmpty()) {
            for (Node waitingNode: waitingNodesQueue.pop()) {
                nodeHeights.put(waitingNode.getId(), nodeHeights.get(waitingNode.getParentId()) + 1);
                waitingNodesQueue.push(waitingNodes.getOrDefault(waitingNode.getId(), new ArrayList<>()));
            }
        }
    }

    private void updateNodeChildrenIndexes(BigInteger sourceIndexes, int index, int oppositeIndex) {
        boolean isCommonAncestor = nodeChildren[index].testBit(oppositeIndex);
        if (isCommonAncestor)
            return;

        BigInteger newTargetIndexes = nodeChildren[index].xor(sourceIndexes);
        nodeChildren[index] = newTargetIndexes;
        boolean isRoot = nodes[index].getRoot().getId() != nodes[index].getId();
        if (isRoot) {
            int parentIndex = idToIndexConverter.convert(nodes[index].getParentId());
            updateNodeChildrenIndexes(sourceIndexes, parentIndex, oppositeIndex);
        }
    }

    private BigInteger[] createNodeChildrenIndexes(Node[] nodes) {
        Objects.requireNonNull(nodes);
        BigInteger[] indexes = new BigInteger[nodes.length];
        Arrays.fill(indexes, BigInteger.ZERO);
        for (int i = 0; i<nodes.length; i++) {
            if (root.equals(nodes[i]))
                continue;

            BigInteger sourceIndexes = indexes[i].xor(BigInteger.valueOf(2).pow(i));
            int parentIndex = idToIndexConverter.convert(nodes[i].getParentId());
            indexes[parentIndex] = sourceIndexes.xor(indexes[parentIndex]);
        }
        return indexes;
    }

    private Node[] sortByChildrenFirst(Node[] nodes, Node root) {
        if (nodes.length == 0) {
            return nodes;
        }
        Map<Integer, Integer> heights = getHeights(nodes, 0, root.getId());
        Map<Integer, List<Node>> nodeBuckets = new HashMap<>();
        int maxHeight = 0;
        for (Node node: nodes) {
            if (node.getId() == root.getId())
                continue;
            int currentHeight = heights.get(node.getId());
            if (maxHeight < currentHeight)
                maxHeight = currentHeight;

            List<Node> bucket = nodeBuckets.getOrDefault(currentHeight, new ArrayList<>());
            bucket.add(node);
            nodeBuckets.put(currentHeight, bucket);
        }
        List<Node> combined = nodeBuckets.get(maxHeight);
        for (int i = maxHeight - 1; i>=1; i--) {
            combined.addAll(nodeBuckets.get(i));
        }
        combined.add(root);
        Node[] result = new Node[combined.size() + 1];
        return combined.toArray(result);
    }
}
