package org.example.core.processor;

import org.example.core.annotations.Api;
import org.example.core.annotations.Request;
import org.example.core.context.BeanContext;
import org.example.core.context.HttpHandlerBuilder;
import org.example.core.enums.HttpMethod;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ApiAnnotationProcessor {

    public static void process(String basePackage) {
        Reflections reflections = new Reflections(basePackage);

        reflections.getTypesAnnotatedWith(Api.class).forEach(aClass ->
                {
                    try {
                        Object instance = aClass.getDeclaredConstructor().newInstance();
                        String name = aClass.getAnnotation(Api.class).name();
                        if(name.equals("")){
                            name = aClass.getSimpleName();
                        }
                        BeanContext.addBean(name,instance);

                        Arrays.stream(aClass.getMethods()).filter(
                                method -> method.isAnnotationPresent(Request.class)
                        ).forEach(method ->  ApiAnnotationProcessor.processMethod(instance,method));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }}

        );
    }

    private static void processMethod(Object instance, Method method) {
        Request request = method.getAnnotation(Request.class);
        String url = request.value();
        HttpMethod httpMethod = request.method();

        String baseUrl = method.getDeclaringClass().getAnnotation(Api.class).value();
        HttpHandlerBuilder.addEndpoint(baseUrl + url, httpMethod, instance, method);
    }
}
