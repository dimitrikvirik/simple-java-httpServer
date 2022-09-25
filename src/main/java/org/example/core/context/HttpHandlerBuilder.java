package org.example.core.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.core.annotations.Body;
import org.example.core.annotations.Query;
import org.example.core.enums.HttpMethod;

import java.io.IOException;
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

        private Map<String, String> queryMap;

        private final Method method;
        private final Object instance;


        private HttpExecutor(Method method, Object instance) {
            this.method = method;
            this.instance = instance;
        }

        public String getResponse() {
            try {
                Object[] parameters = getParameters();
                return HttpServerContext.objectmapper.writeValueAsString(method.invoke(instance, parameters));
            } catch (IllegalAccessException | IOException | InvocationTargetException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        private Object[] getParameters() throws IOException {
            Object[] parameters = new Object[method.getParameterCount()];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = method.getParameters()[i];
                if (parameter.getType().equals(HttpExchange.class)) {
                    parameters[i] = exchange;
                } else if (parameter.isAnnotationPresent(Body.class)) {
                    parameters[i] = HttpServerContext.objectmapper.readValue(exchange.getRequestBody(), parameter.getType());
                } else if (parameter.isAnnotationPresent(Query.class)) {
                    String queryKey;

                    String value = parameter.getAnnotation(Query.class).value();
                    if (value.equals("")) {
                        queryKey = decapitalize(parameter.getName());
                    } else {
                        queryKey = value;
                    }
                    if (queryMap.containsKey(queryKey)) {
                        String s = queryMap.get(queryKey);
                        if(s != null){
                            Object o = HttpServerContext.objectmapper.convertValue(s, parameter.getType());
                            parameters[i] = o;
                        }
                    }

                }
            }
            return parameters;
        }

        public void setExchange(HttpExchange exchange) {
            this.exchange = exchange;
        }

        public void setQueryMap(Map<String, String> queryMap) {
            this.queryMap = queryMap;
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
            Map<String, String> queryMap = new HashMap<>();

            String query = exchange.getRequestURI().getQuery();
            if (query != null)
                for (String s : query.split("&")) {
                    String[] split = s.split("=");
                    String val = null;
                    if (split[1] != null) {
                        val = split[1];
                    }
                    queryMap.put(split[0], val);
                }

            if (httpExecutor != null) {
                httpExecutor.setExchange(exchange);
                httpExecutor.setQueryMap(queryMap);
                response = httpExecutor.getResponse();
                exchange.sendResponseHeaders(200, response.length());
            } else {
                response = "Page Not found";
                exchange.sendResponseHeaders(404, response.length());
            }
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        };
    }

    public static String decapitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        char[] c = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }


}
