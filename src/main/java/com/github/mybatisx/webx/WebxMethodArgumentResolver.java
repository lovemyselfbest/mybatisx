package com.github.mybatisx.webx;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.github.mybatisx.annotation.WebxService;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.util.TypeResolver;
import com.github.mybatisx.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.MethodParameter;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebxMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final static Class<?> requestMapClazz = new HashMap<String, String>().getClass();
    private static final String JSONBODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        var clazz = parameter.getDeclaringClass();
        var b = AnnotationUtils.findAnnotation(clazz, WebxService.class);
        if (b != null)
            return true;

        return false;
    }

    private static final String paramPrefix = "param%d";

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        //HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        // String method = httpServletRequest.getMethod();

//        if (v == null) {
//
//            return TypeResolver.getDefaultValue(parType);
//        } else {
//            if (TypeResolver.isJavaType(parType)) {
//                return TypeResolver.castValueType(parType, v);
//            } else if (parType == String.class) {
//                return v;
//            } else {
//                var v3 = JSON.parseObject(v, parType);
//                return v3;
//            }
//        }
/////

        Map<String, String> jsonBodyMap = getRequestBody(webRequest);

        var method2 = parameter.getMethod();

        var MD = FireFactory.getFactory().getMD(method2);

        var index = parameter.getParameterIndex();

        var PD = MD.getParameterDescriptors().get(index);

        Class<?> parameterType = PD.getRawType();


        String value = jsonBodyMap.get(PD.getName());

        if (StringUtils.isNotEmpty(value)) {
            //基本类型
            if (parameterType.isPrimitive()) {
                return TypeUtil.parsePrimitive(parameterType.getName(), value);
            }
            // 基本类型包装类
            if (TypeUtil.isBasicDataTypes(parameterType)) {
                return TypeUtil.parseBasicTypeWrapper(parameterType, value);
                // 字符串类型
            } else if (parameterType == String.class) {
                return value.toString();
            }
            // 其他复杂对象
            return JSON.parseObject(value.toString(), parameterType);
        }

        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        String v = httpServletRequest.getParameter(PD.getName());
        //基本类型
        if (parameterType.isPrimitive()) {

            if (StringUtils.isNotEmpty(v)) {
                return TypeUtil.parsePrimitive(parameterType.getName(), v);
            }

        }
        // 基本类型包装类
        if (TypeUtil.isBasicDataTypes(parameterType)) {


            if (StringUtils.isNotEmpty(v)) {
                return TypeUtil.parseBasicTypeWrapper(parameterType, v);
            }

            // 字符串类型
        } else if (parameterType == String.class) {

            return v;
        }
        // 其他复杂对象



       var result= JSON.parseObject(v, parameterType);


        return result;


////

    }



    /**
     * 获取请求体JSON字符串
     */
    private Map<String, String> getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        // 有就直接获取
        Map<String, String> jsonBodyMap = (HashMap<String, String>) webRequest.getAttribute(JSONBODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        // 没有就从请求中读取
        if (jsonBodyMap == null) {
            try {

                var jsonBody = IOUtils.readAll(servletRequest.getReader());

                if (StringUtils.isNotEmpty(jsonBody)) {
                    jsonBodyMap = JSON.parseObject(jsonBody, Map.class);
                } else {
                    jsonBodyMap = new HashMap<String, String>();
                    jsonBodyMap.put("nobodyx", "");
                }


                webRequest.setAttribute(JSONBODY_ATTRIBUTE, jsonBodyMap, NativeWebRequest.SCOPE_REQUEST);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonBodyMap;
    }

}

