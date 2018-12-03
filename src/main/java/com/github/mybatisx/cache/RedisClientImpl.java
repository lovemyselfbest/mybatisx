package com.github.mybatisx.cache;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisClientImpl implements  RedisClient {

    private ConcurrentHashMap<String,String>   redis= new ConcurrentHashMap<>();


    @Override
    public <T> T get(String key, Class<T> clazz) {

        var v= redis.get(key);

        var t= JSON.parseObject(v,clazz);

        return t;
    }

    @Override
    public <T> void set(String key, T mode, int expires) {

          var v= JSON.toJSONString(mode);

          redis.putIfAbsent(key,v);

    }
}
