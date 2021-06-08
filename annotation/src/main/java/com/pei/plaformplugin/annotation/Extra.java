package com.pei.plaformplugin.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Extra {
    String key() default "";

    boolean booleanValue() default false;

    byte byteValue() default 0;

    short shortValue() default 0;

    int intValue() default 0;

    long longValue() default 0;

    float floatValue() default 0;

    double doubleValue() default 0;

    String stringValue() default "";

    Class<?> classValue() default void.class;
}
