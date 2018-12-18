package com.github.mybatisx.webx;

import com.github.mybatisx.annotation.WebxRequestMapping;
import com.github.mybatisx.annotation.WebxService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class WebxRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected boolean isHandler(Class<?> beanType) {
        if (beanType.getName().toLowerCase().contains("userserviceimpl")) {

            String mm = "";
        }
        if (beanType.isInterface() != true)
            return false;

        return (AnnotationUtils.findAnnotation(beanType, WebxService.class) != null);
    }

    protected RequestMappingInfo createRequestMappingInfo(Method method, Class<?> handlerType, WebxRequestMapping annotation,
                                                          RequestCondition<?> customCondition) {

        var paths = annotation.value();

        if (paths.length == 0) {
            var sb = new StringBuilder();
            sb.append(StringUtils.substringAfterLast(handlerType.getName(), "."));
            sb.append("/");
            sb.append(method.getName());


//          for(var par : method.getParameterTypes()){
//
//              sb.append("/");
//              sb.append(StringUtils.substringAfterLast(par.getName(),"."));
//
//          }

            var path = sb.toString().toLowerCase();

            paths = new String[]{path};
        }

        var methods = annotation.method();
        if (methods.length == 0)
            methods = new RequestMethod[]{RequestMethod.POST};

        String[] patterns = resolveEmbeddedValuesInPatterns(paths);

        return new RequestMappingInfo(annotation.name(),
                new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(), true, true, null),
                new RequestMethodsRequestCondition(methods),
                new ParamsRequestCondition(annotation.params()), new HeadersRequestCondition(annotation.headers()),
                new ConsumesRequestCondition(annotation.consumes(), annotation.headers()), new ProducesRequestCondition(
                annotation.produces(), annotation.headers(), new ContentNegotiationManager()),
                customCondition);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = null;
        var methodAnnotation = AnnotationUtils.findAnnotation(method, WebxRequestMapping.class);
        if (methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);
            info = createRequestMappingInfo(method, handlerType, methodAnnotation, methodCondition);
            RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                info = createRequestMappingInfo(typeAnnotation, typeCondition).combine(info);
            }
        }
        return info;
    }
}
