package com.github.mybatisx.annotation;

import com.github.mybatisx.config.AnnotationBean;
import com.github.mybatisx.config.WebxConfig;
import com.github.mybatisx.sdk.FeignHandler;
import com.github.mybatisx.sdk.Sdk;
import com.github.mybatisx.util.SpringUtils;
import com.github.mybatisx.webx.WebxMvcConfigurationSupport;
import com.github.mybatisx.webx.register.WebxServiceImplScanner;
import com.github.mybatisx.webx.register.WebxServiceScanner;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited //
@ImportAutoConfiguration({SpringUtils.class, WebxConfig.class,  FeignHandler.class, Sdk.class, AnnotationBean.class, WebxMvcConfigurationSupport.class})
@Import({WebxServiceScanner.class,AnnotationBean.class})
public @interface EnableWebx {
    @AliasFor("webxServiceScan")
    String[] value() default {};

    @AliasFor("value")
    String[] webxServiceScan() default {};
}
