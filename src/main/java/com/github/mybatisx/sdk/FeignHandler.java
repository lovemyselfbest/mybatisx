package com.github.mybatisx.sdk;

import com.alibaba.fastjson.JSON;
import com.github.mybatisx.annotation.WebxService;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.util.JsonUtil;
import com.github.mybatisx.webx.ResponseData;
import org.apache.commons.lang3.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;


@Component
public class FeignHandler implements InvocationHandler {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
     private RestTemplate restTemplate;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        var dao = method.getDeclaringClass();

      var webx = dao.getAnnotation(WebxService.class);

       var servicePath = String.join("/",webx.value(),"v3");

        var serviceInstance = loadBalancerClient.choose(servicePath);

        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "";

       var MD= FireFactory.getFactory().getMD(method);

       url = url +"/"+ MD.getDaoClass().getSimpleName().toLowerCase()+"/"+ MD.getMethod().getName()+"?city=sz";

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        int i=1;
        for(var arg:args){
            postParameters.add("param"+i, JSON.toJSONString(arg));
            i++;
        }


        var headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(postParameters, headers);
        var res = restTemplate.postForObject(url, r, String.class);

       var t1=  MD.getReturnType();


       if(MD.getReturnDescriptor().isList()){
           var clazz= MD.getReturnDescriptor().getMappedClass();
           return JsonUtil.parse2list(res,clazz);
       }






        return null;
    }
}
