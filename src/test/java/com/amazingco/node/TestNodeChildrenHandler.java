package com.amazingco.node;

import com.amazingco.NodeChildrenHandler;
import com.amazingco.model.Node;
import com.amazingco.storage.InMemoryNodeChildrenHandlerStorage;
import com.amazingco.storage.NodeChildrenHandlerStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

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
    public void getNodeChildrenIndexes() {
        BigInteger[] childrenIndexes = sut.getNodeChildrenIndexes();
        Assert.assertNotNull(childrenIndexes);
        Assert.assertEquals(BigInteger.ZERO, childrenIndexes[0]);
        Assert.assertEquals(BigInteger.ONE, childrenIndexes[1]);
        Assert.assertEquals(BigInteger.valueOf(3), childrenIndexes[2]);
        Assert.assertEquals(BigInteger.ZERO, childrenIndexes[3]);
        Assert.assertEquals(BigInteger.ZERO, childrenIndexes[4]);
        Assert.assertEquals(BigInteger.valueOf(15), childrenIndexes[5]);
        int target = (int) (Math.pow(2, 6) - 1);
        Assert.assertEquals(BigInteger.valueOf(target), childrenIndexes[6]);
        Assert.assertEquals(1, mock.getCounter);
        Assert.assertEquals(1, mock.storeCounter);
        sut.getNodeChildrenIndexes();
        Assert.assertEquals(1, mock.getCounter);
        Assert.assertEquals(1, mock.storeCounter);
    }

   @Test
   public void updateNodeChildIndexes() {
       sut.updateNodeChildrenIndexes(5, 2);
       BigInteger[] childrenIndexes = sut.getNodeChildrenIndexes();
       Assert.assertNotNull(childrenIndexes);
       Assert.assertEquals(BigInteger.ZERO, childrenIndexes[0]);
       Assert.assertEquals(BigInteger.ONE, childrenIndexes[1]);
       Assert.assertEquals(BigInteger.ZERO, childrenIndexes[2]);
       Assert.assertEquals(BigInteger.ZERO, childrenIndexes[3]);
       Assert.assertEquals(BigInteger.valueOf(3), childrenIndexes[4]);
       Assert.assertEquals(BigInteger.valueOf(12), childrenIndexes[5]);
       int target = (int) (Math.pow(2, 6) - 1);
       Assert.assertEquals(BigInteger.valueOf(target), childrenIndexes[6]);
       Assert.assertEquals(1, mock.getCounter);
       Assert.assertEquals(1, mock.storeCounter);
       sut.getNodeChildrenIndexes();
       Assert.assertEquals(1, mock.getCounter);
       Assert.assertEquals(1, mock.storeCounter);

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
