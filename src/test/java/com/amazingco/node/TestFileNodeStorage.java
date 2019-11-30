package com.amazingco.node;

import com.amazingco.model.Node;
import com.amazingco.storage.FileNodeStorage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class TestFileNodeStorage {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testFileIsPersistant() throws InterruptedException, IOException {
        File file = folder.newFile();
        FileNodeStorage initial = new FileNodeStorage(file.getPath());
        initial.store(new Node[]{new Node(11)});
        Node[] firstNodes = initial.get();
        Assert.assertEquals(1, firstNodes.length);
        Assert.assertEquals(11, firstNodes[0].getId());
        Node[] secondNodes = new FileNodeStorage(file.getPath()).get();
        Assert.assertEquals(1, secondNodes.length);
        Assert.assertEquals(11, secondNodes[0].getId());
    }
}
