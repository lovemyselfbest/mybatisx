package com.github.mybatisx.webx;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.github.mybatisx.annotation.WebxService;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.util.TypeResolver;
import org.springframework.core.MethodParameter;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
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


        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String method = httpServletRequest.getMethod();

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
        Map<String,String> jsonBodyMap = getRequestBody(webRequest);

        var method2 = parameter.getMethod();

        var MD = FireFactory.getFactory().getMD(method2);

        var index = parameter.getParameterIndex();

        var PD = MD.getParameterDescriptors().get(index);

        Class<?> parameterType = PD.getRawType();

       // String v = httpServletRequest.getParameter(String.format(paramPrefix, index + 1));

        Object value = jsonBodyMap.get(String.format(paramPrefix, index + 1));

        // 获取的注解后的类型 Long
        //Class<?> parameterType = parameter.getParameterType();
        // 通过注解的value或者参数名解析，能拿到value进行解析
        if (value != null) {
            //基本类型
            if (parameterType.isPrimitive()) {
                return parsePrimitive(parameterType.getName(), value);
            }
            // 基本类型包装类
            if (isBasicDataTypes(parameterType)) {
                return parseBasicTypeWrapper(parameterType, value);
                // 字符串类型
            } else if (parameterType == String.class) {
                return value.toString();
            }
            // 其他复杂对象
            return JSON.parseObject(value.toString(), parameterType);
        }



        Object result = parameterType.newInstance();
        // 非基本类型，不允许解析所有字段，返回null


        return  result;


////

    }

    /**
     * 基本类型解析
     */
    private Object parsePrimitive(String parameterTypeName, Object value) {
        final String booleanTypeName = "boolean";
        if (booleanTypeName.equals(parameterTypeName)) {
            return Boolean.valueOf(value.toString());
        }
        final String intTypeName = "int";
        if (intTypeName.equals(parameterTypeName)) {
            return Integer.valueOf(value.toString());
        }
        final String charTypeName = "char";
        if (charTypeName.equals(parameterTypeName)) {
            return value.toString().charAt(0);
        }
        final String shortTypeName = "short";
        if (shortTypeName.equals(parameterTypeName)) {
            return Short.valueOf(value.toString());
        }
        final String longTypeName = "long";
        if (longTypeName.equals(parameterTypeName)) {
            return Long.valueOf(value.toString());
        }
        final String floatTypeName = "float";
        if (floatTypeName.equals(parameterTypeName)) {
            return Float.valueOf(value.toString());
        }
        final String doubleTypeName = "double";
        if (doubleTypeName.equals(parameterTypeName)) {
            return Double.valueOf(value.toString());
        }
        final String byteTypeName = "byte";
        if (byteTypeName.equals(parameterTypeName)) {
            return Byte.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 基本类型包装类解析
     */
    private Object parseBasicTypeWrapper(Class<?> parameterType, Object value) {
        if (Number.class.isAssignableFrom(parameterType)) {
            Number number = (Number) value;
            if (parameterType == Integer.class) {
                return number.intValue();
            } else if (parameterType == Short.class) {
                return number.shortValue();
            } else if (parameterType == Long.class) {
                return number.longValue();
            } else if (parameterType == Float.class) {
                return number.floatValue();
            } else if (parameterType == Double.class) {
                return number.doubleValue();
            } else if (parameterType == Byte.class) {
                return number.byteValue();
            }
        } else if (parameterType == Boolean.class) {
            return value.toString();
        } else if (parameterType == Character.class) {
            return value.toString().charAt(0);
        }
        return null;
    }

    /**
     * 判断是否为基本数据类型包装类
     */
    private boolean isBasicDataTypes(Class clazz) {
        Set<Class> classSet = new HashSet<>();
        classSet.add(Integer.class);
        classSet.add(Long.class);
        classSet.add(Short.class);
        classSet.add(Float.class);
        classSet.add(Double.class);
        classSet.add(Boolean.class);
        classSet.add(Byte.class);
        classSet.add(Character.class);
        return classSet.contains(clazz);
    }

    /**
     * 获取请求体JSON字符串
     */
    private  Map<String,String> getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        // 有就直接获取
        Map<String,String> jsonBodyMap = (HashMap<String,String>) webRequest.getAttribute(JSONBODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        // 没有就从请求中读取
        if (jsonBodyMap == null) {
            try {

               var jsonBody = IOUtils.readAll(servletRequest.getReader());

                jsonBodyMap = JSON.parseObject(jsonBody,Map.class);


                webRequest.setAttribute(JSONBODY_ATTRIBUTE, jsonBodyMap, NativeWebRequest.SCOPE_REQUEST);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonBodyMap;
    }

}

