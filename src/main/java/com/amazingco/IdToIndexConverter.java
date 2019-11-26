package com.amazingco;

import com.amazingco.model.Node;

import java.util.HashMap;
import java.util.Map;

public class IdToIndexConverter {
    private Map<Integer, Integer> idToIndex;

    public IdToIndexConverter(Node[] nodes) {
        Map<Integer, Integer> idToIndex = new HashMap<>();
        for (int i = 0; i<nodes.length; i++) {
            idToIndex.put(nodes[i].getId(), i);
        }
        this.idToIndex = idToIndex;
    }

    public int convert(int id) {
        if (!idToIndex.containsKey(id)) {
            throw new IndexOutOfBoundsException();
        }
        return idToIndex.get(id);
    }
}
