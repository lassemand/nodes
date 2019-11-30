package com.amazingco.node;

import com.amazingco.NodeChildrenHandler;
import com.amazingco.model.Node;
import com.amazingco.storage.NodeStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestNodeChildrenHandler {

    private NodeStorageMock mock;
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
       Node[] nodes = new Node[]{node6, node5, node4, node3, node2, node1, root};
       mock = new NodeStorageMock();
       sut = new NodeChildrenHandler(mock, nodes, root);
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
        //Assert.assertEquals(1, nodes.get(2).getRoot().getRoot().getId());
        //TODO
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
        Node[] nodes = buildTestNodes(10000);
        sut = new NodeChildrenHandler(new NodeStorageMock(), nodes, nodes[9999]);
        sut.updateNodeChildrenIndexes(5000, 9998);
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


   private class NodeStorageMock implements NodeStorage {

       int getCounter, storeCounter = 0;

       @Override
       public Node[] get() {
           getCounter++;
           return null;
       }

       @Override
       public void store(Node[] handler) {
           storeCounter++;
       }
   }

}
