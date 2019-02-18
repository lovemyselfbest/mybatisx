package com.github.mybatisx.annotation;



import com.github.mybatisx.sdk.Sdk;
import com.github.mybatisx.util.SpringUtils;
import com.github.mybatisx.webx.Config;

import com.github.mybatisx.webx.error.GlobalExceptionHandler;
import com.github.mybatisx.webx.WebxMvcConfigurationSupport;


import com.github.mybatisx.webx.register.*;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited //
//FeignHandler.class,
@ImportAutoConfiguration(value = {SpringUtils.class, Listener1.class, Listener2.class, Listener3.class, WebxServiceScanner.class, Config.class,   Sdk.class,WebxReferencePostProcessor.class, WebxMvcConfigurationSupport.class, GlobalExceptionHandler.class})
public @interface EnableWebx {
    @AliasFor("webxServiceScan")
    String[] value() default {};

    @AliasFor("value")
    String[] webxServiceScan() default {};
}
