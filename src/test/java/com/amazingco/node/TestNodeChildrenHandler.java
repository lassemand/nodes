package com.amazingco.node;

import com.amazingco.NodeChildrenHandler;
import com.amazingco.model.Node;
import com.amazingco.storage.NodeChildrenHandlerStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class TestNodeChildrenHandler {

    private NodeChildrenHandlerStorageMock mock;
    private NodeChildrenHandler sut;

   @Before
   public void setup() {
       Node root = new Node(0);
       Node node1 = new Node(1, root, root);
       Node node2 = new Node(2, root, root);
       Node node3 = new Node(3, node1, root);
       Node node4 = new Node(4, node1, root);
       Node node5 = new Node(5, node4, root);
       Node node6 = new Node(6, node5, root);
       Node[] nodes = new Node[]{node6, node5, node4, node3, node2, node1, root};
       mock = new NodeChildrenHandlerStorageMock();
       sut = new NodeChildrenHandler(mock, nodes);
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
        sut.updateNodeChildrenIndexes(5, 0);
        List<Node> nodes = sut.getNodeChildren(4);
        Assert.assertEquals(0, nodes.size());
        nodes = sut.getNodeChildren(0);
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
        Assert.assertEquals(1, nodes.get(2).getRoot().getRoot().getId());
    }

    @Test
    public void updatesParent() throws InterruptedException {
        sut.updateNodeChildrenIndexes(5, 1);
        List<Node> children = sut.getNodeChildren(0);
        for (Node node: children) {
            if (node.getId() == 1) {
                Assert.assertEquals(0, node.getParent().getId());
                continue;
            }
            if (node.getId() == 5) {
                Assert.assertEquals(1, node.getParent().getId());
                continue;
            }
            if (node.getId() == 6) {
                Assert.assertEquals(5, node.getParent().getId());
            }
        }
    }

    @Test
    public void performanceTonsOfNodes() throws InterruptedException {
        Node[] nodes = buildTestNodes(10000);
        sut = new NodeChildrenHandler(new NodeChildrenHandlerStorageMock(), nodes);
        sut.updateNodeChildrenIndexes(5000, 9998);
        List<Node> children = sut.getNodeChildren(5001);
        Assert.assertEquals(0, children.size());
    }

    private Node[] buildTestNodes(int amount) {
       Node[] nodes = new Node[amount];
       for (int i = amount -1; i>=0; i--) {
             nodes[i] = i == amount - 1 ? new Node(i) : new Node(i, nodes[i+1], nodes[amount - 1]);
       }
       return nodes;
    }


   private class NodeChildrenHandlerStorageMock implements NodeChildrenHandlerStorage {

       int getCounter, storeCounter = 0;

       @Override
       public BigInteger[] get() {
           getCounter++;
           return null;
       }

       @Override
       public void store(BigInteger[] handler) {
           storeCounter++;
       }
   }

}
