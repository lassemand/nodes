package com.amazingco.node;

import com.amazingco.model.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class TestNode {

    private Node sut;

   @Before
   public void setup() {
       sut = new Node(UUID.randomUUID().toString());
   }

    @Test
    public void parentNodeShouldOverride() {

    }

}
