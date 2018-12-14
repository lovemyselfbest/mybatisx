package com.github.mybatisx.util;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebxReferenceUtil {

    private static final ConcurrentMap<String, String> referenceConfigs = new ConcurrentHashMap<String, String>();

    public static void add(String key, String value) {

        var b= referenceConfigs.containsKey(key);
        if(b){
            var v= referenceConfigs.get(key);
            if(!v.equalsIgnoreCase(value)){
              throw  new IllegalArgumentException(String.format("%s存在使用多个版本",key));
            }
        }
        referenceConfigs.putIfAbsent(key, value);
    }

    public static String getValue(String key) {
        return referenceConfigs.getOrDefault(key, "");
    }
}
