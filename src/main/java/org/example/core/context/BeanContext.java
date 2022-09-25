package org.example.core.context;

import java.util.HashMap;
import java.util.Map;

public class BeanContext {


    private static final Map<String, Object> beans = new HashMap<>();

    public static   void addBean(String name, Object instance) {
        if(beans.get(name) != null){
            System.out.println("Bean already exist. please specify other name");
            System.exit(-1);
        }

        beans.put(name, instance);
    }

    public static Object getBean(Class<?> clazz) {
        return beans.values().stream().filter(o -> o.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public static Object getBean(String name) {
        return beans.get(name);
    }
}
