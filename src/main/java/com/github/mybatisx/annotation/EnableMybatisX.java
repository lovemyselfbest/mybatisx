package com.github.mybatisx.annotation;


import com.github.mybatisx.aspect.cacheAspect;
import com.github.mybatisx.config.MapperScannerRegistrar;

import com.github.mybatisx.mybatisx.MybatisxConfig;
import com.github.mybatisx.util.SpringUtils;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited //
@ImportAutoConfiguration({SpringUtils.class, cacheAspect.class,  MybatisxConfig.class})

@Import({MapperScannerRegistrar.class})

public @interface EnableMybatisX {

    @AliasFor("mapperScan")
    String[] value() default {};

    @AliasFor("value")
    String[] mapperScan() default {};
}
