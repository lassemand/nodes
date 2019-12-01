package com.amazingco.node;

import com.amazingco.model.Backup;
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
    public void testFileIsPersistant() throws IOException {
        File file = folder.newFile();
        FileNodeStorage initial = new FileNodeStorage(file.getPath());
        initial.store(new Node[]{new Node(11)});
        Backup backup = initial.get();
        Assert.assertEquals(1, backup.getNodes().length);
        Assert.assertEquals(11, backup.getNodes()[0].getId());
        Backup secondBackup = new FileNodeStorage(file.getPath()).get();
        Assert.assertEquals(1, secondBackup.getNodes().length);
        Assert.assertEquals(11, secondBackup.getNodes()[0].getId());
    }

    @Test
    public void testBackupDoesOverrideCurrentFile() throws IOException {
        File file = folder.newFile();
        FileNodeStorage initial = new FileNodeStorage(file.getPath());
        initial.store(new Node[]{new Node(11)});
        initial.store(new Node[]{new Node(12)});
        Backup backup = initial.get();
        Assert.assertEquals(1, backup.getNodes().length);
        Assert.assertEquals(12, backup.getNodes()[0].getId());
    }
}
