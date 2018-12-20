package com.github.mybatisx.sharding;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CitySharding implements DatabaseShardingStrategy {
    @Override
    public String getDataSourceFactoryName(Object shardingParameter) {

        try {
            var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            var city = request.getParameter("city");


            if (StringUtils.isEmpty(city))
                throw new IllegalArgumentException("");

            return "order_" + city;
        } catch (Exception ex) {

        }
        return "order_sz";
    }
}
