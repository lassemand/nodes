package com.amazingco.storage;

import com.amazingco.model.Backup;
import com.amazingco.model.Node;

public class InMemoryNodeStorage implements NodeStorage {

    private Backup backup;

    @Override
    public void store(Node[] nodes) {
        Node root = null;
        for (int i  = 0; i<nodes.length; i++) {
            if (nodes[i].getParentId() == -1) {
                root = nodes[i];
                break;
            }
        }
        this.backup = new Backup(root, nodes);
    }

    @Override
    public Backup get() {
        return backup;
    }
}
