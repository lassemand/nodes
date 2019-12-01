package com.amazingco.http;

import com.amazingco.handler.NodeChildrenHandler;
import com.amazingco.model.Node;
import com.amazingco.storage.NodeStorage;
import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Method.PUT;

public class HttpMessageHandler extends NanoHTTPD {

    private NodeChildrenHandler nodeChildrenHandler;
    private NodeStorage storage;
    private Pattern patternGet = Pattern.compile("^/api/v1/node/([0-9]+)/descendant");
    private Pattern patternPut = Pattern.compile("^/api/v1/node/([0-9]+)/parent/([0-9]+)");

    public HttpMessageHandler(int port, NodeChildrenHandler nodeChildrenHandler, NodeStorage storage) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        this.nodeChildrenHandler = nodeChildrenHandler;
        this.storage = storage;
        System.out.println("Server is started on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            if (session.getMethod().equals(GET)) {
                Matcher m = patternGet.matcher(session.getUri());
                if (!m.find()) {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Could not find resource under path");
                }
                int id = Integer.parseInt(m.group(1));
                Gson gson = new Gson();
                long start = System.nanoTime();
                List<Node> nodes = nodeChildrenHandler.getNodeChildren(id);
                long middle = System.nanoTime();
                String response = gson.toJson(nodes);
                long end = System.nanoTime();
                System.out.println("Getting nodes: " + (middle-start) + " serialization " + (end-middle));
                return newFixedLengthResponse(Response.Status.OK, "application/json", response);
            }
            if (session.getMethod().equals(PUT)) {
                Matcher m = patternPut.matcher(session.getUri());
                if (!m.find()) {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Could not find resource under path");
                }
                int sourceId = Integer.parseInt(m.group(1));
                int targetId = Integer.parseInt(m.group(2));
                long start = System.nanoTime();
                nodeChildrenHandler.updateNodeChildrenIndexes(sourceId, targetId);
                storage.store(nodeChildrenHandler.getNodes());
                long end = System.nanoTime();
                System.out.println(end-start);
                return newFixedLengthResponse(Response.Status.NO_CONTENT, "", "");
            }
            return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, "Invalid method");
        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        }
    }
}
