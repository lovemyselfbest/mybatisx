package com.github.mybatisx.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {


    @Override
    protected Object determineCurrentLookupKey() {

        // log.debug("数据源为{}", DataSourceContextHolder.getDBKey());
        return  DataSourceContextHolder.getDBKey();
    }
}
