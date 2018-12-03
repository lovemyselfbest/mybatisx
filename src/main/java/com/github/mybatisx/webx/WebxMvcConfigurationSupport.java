package com.github.mybatisx.webx;


import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 9)
public class WebxMvcConfigurationSupport extends WebMvcConfigurationSupport {

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        var handlerMapping = new WebxRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);

        handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());

        PathMatchConfigurer configurer = getPathMatchConfigurer();
        if (configurer.isUseSuffixPatternMatch() != null) {
            handlerMapping.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
        }
        if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
            handlerMapping.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
        }
        if (configurer.isUseTrailingSlashMatch() != null) {
            handlerMapping.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
        }
        if (configurer.getPathMatcher() != null) {
            handlerMapping.setPathMatcher(configurer.getPathMatcher());
        }
        if (configurer.getUrlPathHelper() != null) {
            handlerMapping.setUrlPathHelper(configurer.getUrlPathHelper());
        }

        return handlerMapping;
    }

    @Bean
    @Override
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {

        var adapter = super.requestMappingHandlerAdapter();

        //HttpMessageConvert
        List<HttpMessageConverter<?>> converters = new ArrayList();
        converters.add(converter());

        // argumentResolvers
        var argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
        argumentResolvers.add(new WebxMethodArgumentResolver());
        adapter.setArgumentResolvers(argumentResolvers);

        // returnValueHandlers
        var returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
        var jsonReturnValueResolver = new WebxMethodReturnValueHandler();
        jsonReturnValueResolver.setMessageConverter(converter());
        returnValueHandlers.add(jsonReturnValueResolver);
        //adapter.setCustomReturnValueHandlers(returnValueHandlers2);
        adapter.setReturnValueHandlers(returnValueHandlers);
        return adapter;
    }

    //会覆盖掉Spring MVC 默认注册的多个HttpMessageConverter。
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //super.configureMessageConverters(converters);

        converters.add(converter());
    }

    //仅添加一个自定义的HttpMessageConverter，不覆盖默认注册的HttpMessageConverter。
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.extendMessageConverters(converters);
        //converters.add(converter());
    }

    @Bean
    public WebxHttpMessageConvert converter() {

        return new WebxHttpMessageConvert();
    }

}
