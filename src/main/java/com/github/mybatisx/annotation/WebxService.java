package com.github.mybatisx.annotation;



import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)


public @interface WebxService {
    String value() default "";
}
