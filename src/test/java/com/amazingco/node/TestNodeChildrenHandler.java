package com.amazingco.node;

import com.amazingco.handler.NodeChildrenHandler;
import com.amazingco.model.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestNodeChildrenHandler {

    private NodeChildrenHandler sut;

   @Before
   public void setup() {
       Node root = new Node(0);
       Node node1 = new Node(1, 0);
       Node node2 = new Node(2, 0);
       Node node3 = new Node(3, 1);
       Node node4 = new Node(4, 1);
       Node node5 = new Node(5, 4);
       Node node6 = new Node(6, 5);
       Node[] nodes = new Node[]{node2, node5, node4, node6, root, node3, node1};
       sut = new NodeChildrenHandler(nodes, root);
   }

    @Test
    public void getNodeChildren() throws InterruptedException {
        List<Node> nodes = sut.getNodeChildren(4);
        Assert.assertEquals(2, nodes.size());
        Assert.assertTrue(nodes.contains(new Node(5)));
        Assert.assertTrue(nodes.contains(new Node(6)));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getNodeChildrenIndexesOutOfRange() throws InterruptedException {
        sut.getNodeChildren(-1);
        sut.getNodeChildren(7);
    }

    @Test
    public void updateNodeChildren() throws InterruptedException {
        sut.updateNodeChildrenIndexes(5, 2);
        List<Node> nodes = sut.getNodeChildren(4);
        Assert.assertEquals(0, nodes.size());
        nodes = sut.getNodeChildren(2);
        Assert.assertTrue(nodes.contains(new Node(5)));
        Assert.assertTrue(nodes.contains(new Node(6)));
    }

    @Test
    public void updateNodeChildrenSamePath() throws InterruptedException {
        sut.updateNodeChildrenIndexes(5, 1);
        List<Node> nodes = sut.getNodeChildren(4);
        Assert.assertEquals(0, nodes.size());
        nodes = sut.getNodeChildren(1);
        Assert.assertEquals(4, nodes.size());
        Assert.assertTrue(nodes.contains(new Node(3)));
        Assert.assertTrue(nodes.contains(new Node(4)));
        Assert.assertTrue(nodes.contains(new Node(5)));
        Assert.assertTrue(nodes.contains(new Node(6)));
    }

    @Test
    public void updatesRoot() throws InterruptedException {
        sut.updateNodeChildrenIndexes(0, 1);
        List<Node> nodes = sut.getNodeChildren(1);
        Assert.assertEquals(1, sut.getRoot().getId());
        boolean isFound = false;
        for (Node node: nodes) {
            if (node.getId() == 0) {
                isFound = true;
                break;
            }
        }
        Assert.assertTrue(isFound);
    }

    @Test
    public void updatesParent() throws InterruptedException {
        sut.updateNodeChildrenIndexes(5, 1);
        List<Node> children = sut.getNodeChildren(1);
        for (Node node: children) {
            if (node.getId() == 1) {
                Assert.assertEquals(0, node.getParentId());
                continue;
            }
            if (node.getId() == 5) {
                Assert.assertEquals(1, node.getParentId());
                continue;
            }
            if (node.getId() == 6) {
                Assert.assertEquals(5, node.getParentId());
            }
        }
    }

    @Test
    public void updatesHeight() throws InterruptedException {
        List<Node> children = sut.getNodeChildren(1);
        for (Node node: children) {
            if (node.getId() == 6) {
                Assert.assertEquals(4, node.getHeight());
            }
            if (node.getId() == 5) {
                Assert.assertEquals(3, node.getHeight());
            }
            if (node.getId() == 4) {
                Assert.assertEquals(2, node.getHeight());
            }
            if (node.getId() == 3) {
                Assert.assertEquals(2, node.getHeight());
            }
        }
    }

    @Test
    public void performanceTonsOfNodes() throws InterruptedException {
        Node[] nodes = buildTestNodes(20000);
        sut = new NodeChildrenHandler(nodes, nodes[19999]);
        long start = System.nanoTime();
        sut.updateNodeChildrenIndexes(5000, 19999);
        long end = System.nanoTime();
        System.out.println(end-start);
        List<Node> children = sut.getNodeChildren(5001);
        Assert.assertEquals(0, children.size());
    }

    private Node[] buildTestNodes(int amount) {
       Node[] nodes = new Node[amount];
       for (int i = amount -1; i>=0; i--) {
           nodes[i] = i == amount - 1 ? new Node(i) : new Node(i, nodes[i+1].getId());
       }
       return nodes;
    }
}
