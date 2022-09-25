package org.example.core.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerContext {

    private static HttpServer httpServer;

    public static final ObjectMapper objectmapper = new ObjectMapper();

    public static HttpServer get() {
        if (httpServer == null) {
            try {
                httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
                httpServer.setExecutor(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return httpServer;
    }


}
