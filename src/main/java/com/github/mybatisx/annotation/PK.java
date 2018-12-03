package com.github.mybatisx.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PK {
    boolean autoIncrement() default false;
}
