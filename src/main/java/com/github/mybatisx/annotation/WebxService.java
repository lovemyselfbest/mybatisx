package com.github.mybatisx.annotation;



import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WebxService {
    String value() default "";
    String version() default "";
}
