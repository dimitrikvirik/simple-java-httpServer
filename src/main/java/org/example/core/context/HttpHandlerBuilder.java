package org.example.core.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.core.enumns.HttpMethod;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class HttpHandlerBuilder {

    private static final Map<String, HttpExecutor> httpExecutorMap = new HashMap<>();

    private static class HttpExecutor {
        private HttpExchange exchange;

        private final Method method;
        private final Object instance;


        private HttpExecutor(Method method, Object instance) {
            this.method = method;
            this.instance = instance;
        }

        public String getResponse() {
            try {
                Object[] parameters = new Object[method.getParameterCount()];
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = method.getParameters()[i];
                    if (parameter.getType().equals(HttpExchange.class)) {
                        parameters[i] = exchange;
                    }
                }
                return HttpServerContext.objectmapper.writeValueAsString(method.invoke(instance, parameters));
            } catch (IllegalAccessException | InvocationTargetException | JsonProcessingException e) {
                e.printStackTrace();
               return e.getMessage();
            }
        }

        public void setExchange(HttpExchange exchange) {
            this.exchange = exchange;
        }
    }


    public static void addEndpoint(String path, HttpMethod method, Object instance, Method classMethod) {
        httpExecutorMap.put(path + "_" + method.name(), new HttpExecutor(classMethod, instance));
    }

    public static HttpHandler getHandler() {
        return exchange -> {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            HttpExecutor httpExecutor = httpExecutorMap.get(path + "_" + requestMethod);

            String response;

            if (httpExecutor != null) {
                httpExecutor.setExchange(exchange);
                response = httpExecutor.getResponse();
            } else {

                response = "Endpoint with this method not found";
            }

            exchange.sendResponseHeaders(200, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        };
    }


}
