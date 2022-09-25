package org.example.core.content;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerContext {

    private static HttpServer httpServer;

    public static HttpServer get() {
        if (httpServer == null) {
            try {
                httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
                httpServer.setExecutor(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return httpServer;
    }


}
