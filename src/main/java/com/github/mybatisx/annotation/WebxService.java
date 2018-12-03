package com.github.mybatisx.annotation;

import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebxService {
    String value() default "";
}
