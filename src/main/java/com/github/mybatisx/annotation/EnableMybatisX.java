package com.github.mybatisx.annotation;


import com.github.mybatisx.aspect.cacheAspect;
import com.github.mybatisx.config.MybatisxConfig;
import com.github.mybatisx.webx.WebxMvcConfigurationSupport;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClientImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited //
@ImportAutoConfiguration({ cacheAspect.class, MybatisxConfig.class,WebxMvcConfigurationSupport.class})
public @interface EnableMybatisX {
}
