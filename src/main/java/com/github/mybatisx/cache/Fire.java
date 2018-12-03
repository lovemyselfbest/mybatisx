package com.github.mybatisx.cache;

import com.github.mybatisx.sharding.DatabaseShardingStrategy;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Getter@Setter@Builder
public class Fire {

    private Method method;

    //private String cachebyFieldName;

    private Field cachebyField4Query;

    private Field cachebyField4Model;

    private String Db;

    private  int cacheTTL;
    private boolean IsCaching;

    private Class<?> methodReturnType;


    private  Class<?> keyType;

    private  Class<?> valueType;
    private String cachePrefix;


    private DatabaseShardingStrategy Sharding;

}
