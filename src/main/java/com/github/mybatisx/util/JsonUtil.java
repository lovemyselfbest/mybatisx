package com.github.mybatisx.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.github.mybatisx.webx.ResponseData;

import java.lang.reflect.Type;
import java.util.List;

public class JsonUtil {

//    public static String toJSONString(Object v){
//
//        var t= v.getClass();
//
//            if (TypeResolver.isJavaType(t)) {
//                return TypeResolver.castValueType(t, v);
//            } else if (t == String.class) {
//                return (String) v;
//            } else {
//                var v3 = JSON.toJSONString(v);
//                return v3;
//            }
//
//    }

    public static <T> ResponseData<T> parse2list(String json, Class<T> clazz) {

    //  var  type2=new TypeReference<ResponseData<T>>(type) {};
        ParameterizedTypeImpl inner = new ParameterizedTypeImpl(new Type[]{clazz}, null, List.class);
        ParameterizedTypeImpl outer = new ParameterizedTypeImpl(new Type[]{inner}, null, ResponseData.class);
        return JSONObject.parseObject(json, outer);
    }

    public static <T> ResponseData<T> parseToMap(String json, Class<T> type) {
        return JSON.parseObject(json,
                new TypeReference<ResponseData<T>>(type) {});
    }
}
