package com.github.mybatisx.cache;

public interface RedisClient {
    <T> T get(String key,Class<T> clazz);

    <T> void set(String key, T mode, int expires);

    void remove(String key);
}
