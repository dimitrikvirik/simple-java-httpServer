package org.example.core;

import com.sun.net.httpserver.HttpServer;
import org.example.core.context.HttpHandlerBuilder;
import org.example.core.context.HttpServerContext;
import org.example.core.processor.ApiAnnotationProcessor;

public class SimpleApi {

    public static void run(String basePackage){


        ApiAnnotationProcessor.process(basePackage);

        HttpServer httpServer = HttpServerContext.get();
        httpServer.createContext("/", HttpHandlerBuilder.getHandler());
        httpServer.start();
    }
}
