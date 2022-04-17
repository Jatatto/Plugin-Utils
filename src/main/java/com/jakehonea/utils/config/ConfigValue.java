package com.jakehonea.utils.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({FIELD, TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {
    String value() default "";
}
