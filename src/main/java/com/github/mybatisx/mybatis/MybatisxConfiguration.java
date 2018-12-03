package com.github.mybatisx.mybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

public class MybatisxConfiguration extends Configuration {

    protected final MapperRegistry mapperRegistry = new MybatisxMapperRegistry(this);

    public MybatisxConfiguration() {
        super();
    }

    public MybatisxConfiguration(Environment environment) {
        super(environment);
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

}

