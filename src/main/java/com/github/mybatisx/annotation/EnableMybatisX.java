package com.github.mybatisx.annotation;


import com.github.mybatisx.aspect.cacheAspect;
import com.github.mybatisx.config.MapperScannerRegistrar3;
import com.github.mybatisx.config.MybatisxConfig;
import com.github.mybatisx.sdk.FeignHandler;
import com.github.mybatisx.sdk.Sdk;
import com.github.mybatisx.webx.WebxMvcConfigurationSupport;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClientImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited //
@ImportAutoConfiguration({ cacheAspect.class, Sdk.class, FeignHandler.class, MybatisxConfig.class,WebxMvcConfigurationSupport.class})
@Import({MapperScannerRegistrar3.class})
public @interface EnableMybatisX {

    @AliasFor("mapperScan")
    String[] value() default {};

    @AliasFor("value")
    String[] mapperScan() default {};
}
