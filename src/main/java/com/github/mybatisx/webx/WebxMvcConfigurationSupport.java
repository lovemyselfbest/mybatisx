package com.github.mybatisx.webx;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;



public class WebxMvcConfigurationSupport extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*swagger-ui*/
        registry.addResourceHandler("swagger-ui2.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars2/**").addResourceLocations("classpath:/META-INF/resources/webjars2/");
    }



    @Bean
    //@Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping2() {
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

    @Autowired
    RequestMappingHandlerAdapter adapter;

    @PostConstruct
    public void  post(){



        List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();
       var msgConverterX = new HttpMessageConvertX();
       converters.add(msgConverterX);


        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>(adapter.getArgumentResolvers());
        List<HandlerMethodArgumentResolver> customResolvers = adapter.getCustomArgumentResolvers();
        if (customResolvers != null) {
            argumentResolvers.removeAll(customResolvers);
            argumentResolvers.addAll(0, customResolvers);
        }
        // 自定义的WebxMethodArgumentResolver前置
        argumentResolvers.add(0, new WebxMethodArgumentResolver());
       adapter.setArgumentResolvers(argumentResolvers);
        ///
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>(adapter.getReturnValueHandlers());
        var jsonReturnValueResolver = new WebxMethodReturnValueHandler();
        jsonReturnValueResolver.setMessageConverter(msgConverterX);
        returnValueHandlers.add(jsonReturnValueResolver);
        //adapter.setCustomReturnValueHandlers(returnValueHandlers);
        adapter.setReturnValueHandlers(returnValueHandlers);
    }

    //@Bean
    //@Override
   // @ConditionalOnMissingBean
//    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
//
//        var adapter = super.requestMappingHandlerAdapter();
//
//        //HttpMessageConvert
//        List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();
//        var msgConverterX = new HttpMessageConvertX();
//        converters.add(msgConverterX);
//
//        // argumentResolvers
//        var argumentResolvers = adapter.getArgumentResolvers();//new ArrayList<HandlerMethodArgumentResolver>();
//        argumentResolvers.add(new WebxMethodArgumentResolver());
//
//        adapter.setArgumentResolvers(argumentResolvers);
//
//        // returnValueHandlers
//        var returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
//        var jsonReturnValueResolver = new WebxMethodReturnValueHandler();
//        jsonReturnValueResolver.setMessageConverter(msgConverterX);
//        returnValueHandlers.add(jsonReturnValueResolver);
//        //adapter.setCustomReturnValueHandlers(returnValueHandlers2);
//        adapter.setReturnValueHandlers(returnValueHandlers);
//        return adapter;
//    }

    //会覆盖掉Spring MVC 默认注册的多个HttpMessageConverter。
//    @Override
//    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //super.configureMessageConverters(converters);
//
//        converters.add(converter());
//
//        var mm="";
//    }

    //仅添加一个自定义的HttpMessageConverter，不覆盖默认注册的HttpMessageConverter。
//    @Override
//    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        super.extendMessageConverters(converters);
//        converters.add(converter());
//    }
//
//    @Bean
//    public HttpMessageConvertX converter() {
//
//        return new HttpMessageConvertX();
//    }

}
