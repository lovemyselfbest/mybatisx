package com.github.mybatisx.webx;

import com.alibaba.fastjson.JSON;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.util.TypeResolver;
import lombok.val;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WebxMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final static Class<?> requestMapClazz = new HashMap<String, String>().getClass();


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }
    private static final String paramPrefix = "param%d";
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
//       String method = httpServletRequest.getMethod();
//        String params;
//        if ("GET".equals(method)) {
//            params = httpServletRequest.getQueryString();
//        } else {
//            BufferedReader reader = httpServletRequest.getReader();
//            StringBuilder lines = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                lines.append(line);
//            }
//            params = lines.toString();
//        }

        var method2 = parameter.getMethod();

        var MD = FireFactory.getFactory().getMD(method2);

        var index = parameter.getParameterIndex();

        var PD = MD.getParameterDescriptors().get(index);

        Class<?> parType = PD.getRawType();
        String v = httpServletRequest.getParameter(String.format(paramPrefix, index+1));
        if (v == null) {

            return TypeResolver.getDefaultValue(parType);
        } else {
            if (TypeResolver.isJavaType(parType)) {
                return TypeResolver.castValueType(parType, v);
            } else if (parType == String.class) {
                return v;
            } else {
                var v3 = JSON.parseObject(v, parType);
                return v3;
            }
        }


    }

}

