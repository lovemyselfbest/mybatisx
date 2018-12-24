package com.github.mybatisx.cache;

import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.util.MetaUtil;
import com.github.mybatisx.util.SpringUtils;
import com.github.mybatisx.util.TypeResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CacheUtil {

    public  static Field getCacheField(Class<?> clazz){

        var fields = FieldUtils.getAllFields(clazz);

        for (var field : fields) {

            var cacheBy = field.getAnnotation(CacheBy.class);

            if (cacheBy != null) {

                return field;

            }

        }
        return null;
    }
    public static Map<String, Object> getCached(Set<String> keys, Class<?> clazz) {
        var ret = new HashMap<String, Object>();
        var redis = SpringUtils.getBean(RedisClient.class);
        for (String key : keys) {
            var v = redis.get(key, clazz);
            if (v != null) {
                ret.putIfAbsent(key, v);
            }
        }

        return ret;
    }

    public static Object getCached(String key, Class<?> clazz) {

        var redis = SpringUtils.getBean(RedisClient.class);

            var v = redis.get(key, clazz);
            if (v != null) {
               return v;
            }


        return null;
    }
}
