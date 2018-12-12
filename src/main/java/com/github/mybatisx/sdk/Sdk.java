package com.github.mybatisx.sdk;

import com.google.common.reflect.Reflection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sdk {

    @Autowired
    private FeignHandler handler;

    public  <T> T getHandler(Class<T> faceClazz){

        return Reflection.newProxy(faceClazz,handler);
    }
}
