package com.github.mybatisx.sharding;

public interface DatabaseShardingStrategy<T> {


    public String getDataSourceFactoryName(T shardingParameter);

}
