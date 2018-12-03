package com.github.mybatisx.annotation;

import java.lang.annotation.*;


@Target({ElementType.METHOD , ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheBy {

   // String  db() default "";

    String key() default "";

    int ttl() default 3600;

}
