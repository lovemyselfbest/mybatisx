package com.github.mybatisx.sdk;

import com.google.common.reflect.Reflection;

public class Sdk {

    public static <T> T getHandler(Class<T> faceClazz){

        var handler= new FeignHandler();

        return Reflection.newProxy(faceClazz,handler);
    }
}
