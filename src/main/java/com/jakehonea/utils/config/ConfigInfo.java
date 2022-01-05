package com.jakehonea.utils.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigInfo {
    String value() default ConfigHandler.EMPTY_PATH;
}
