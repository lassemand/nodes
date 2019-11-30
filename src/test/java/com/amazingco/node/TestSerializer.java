package com.amazingco.node;

import com.amazingco.model.Backup;
import com.amazingco.model.Node;
import com.amazingco.serialization.Serializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestSerializer {

    @Test(expected = NullPointerException.class)
    public void testSerializeNull() throws IOException {
        Node[] nodes = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer.serialize(out, nodes);
    }

    @Test
    public void testSerializeEmpty() throws InterruptedException, IOException {
        Node[] nodes = new Node[0];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer.serialize(out, nodes);
        byte[] result = out.toByteArray();
        Assert.assertEquals(1, result.length);
        Assert.assertEquals(0, result[0]);
        Backup backup = Serializer.deserializeNodes(new ByteArrayInputStream(result));
        Assert.assertArrayEquals(nodes, backup.getNodes());
    }

    @Test
    public void testSerializeOne() throws IOException {
        Node[] nodes = new Node[] {new Node(1, 10)};
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer.serialize(out, nodes);
        byte[] result = out.toByteArray();
        Assert.assertEquals(3, result.length);
        Assert.assertArrayEquals(new byte[]{2, 2, 20}, result);
        Backup backup = Serializer.deserializeNodes(new ByteArrayInputStream(result));
        Assert.assertArrayEquals(nodes, backup.getNodes());
    }

    @Test
    public void testSerializeRoot() throws IOException {
        Node[] nodes = new Node[] {new Node(1)};
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer.serialize(out, nodes);
        byte[] result = out.toByteArray();
        Assert.assertEquals(3, result.length);
        Assert.assertArrayEquals(new byte[]{2, 2, 1}, result);
        Backup backup = Serializer.deserializeNodes(new ByteArrayInputStream(result));
        Assert.assertArrayEquals(nodes, backup.getNodes());
        Assert.assertEquals(1, backup.getRoot().getId());
    }

   @Test
   public void testSerializeMany() throws IOException {
       Node[] nodes = new Node[] {new Node(1), new Node(1, 5), new Node(5, 3)};
       ByteArrayOutputStream out = new ByteArrayOutputStream();
       Serializer.serialize(out, nodes);
       byte[] result = out.toByteArray();
       Assert.assertEquals(7, result.length);
       Assert.assertArrayEquals(new byte[]{6, 2, 1, 2, 10, 10, 6}, result);
       Backup backup = Serializer.deserializeNodes(new ByteArrayInputStream(result));
       Assert.assertArrayEquals(nodes, backup.getNodes());
   }

}
