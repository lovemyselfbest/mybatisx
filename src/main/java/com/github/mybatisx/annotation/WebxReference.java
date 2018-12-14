package com.github.mybatisx.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebxReference {
    @AliasFor("version")
    String value() default "";
    @AliasFor("value")
    String version() default "";
}
