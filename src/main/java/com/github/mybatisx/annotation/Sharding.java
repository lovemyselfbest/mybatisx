package com.github.mybatisx.annotation;

import com.github.mybatisx.sharding.DatabaseShardingStrategy;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sharding {


    /**
     * 数据库切分策略
     *
     * @return
     */
    Class<? extends DatabaseShardingStrategy> databaseShardingStrategy() ;


}
