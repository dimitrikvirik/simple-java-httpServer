package org.example.core.annotations;

import org.example.core.enums.HttpMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Request {
    String value() default "";
    HttpMethod method() default HttpMethod.GET;

}