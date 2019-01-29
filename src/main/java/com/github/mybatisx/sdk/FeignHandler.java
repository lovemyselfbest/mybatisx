package com.github.mybatisx.sdk;

import com.alibaba.fastjson.JSON;
import com.github.mybatisx.annotation.WebxReference;
import com.github.mybatisx.annotation.WebxService;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.exception.BizException;
import com.github.mybatisx.util.JsonUtil;
import com.github.mybatisx.util.WebxReferenceUtil;
import com.github.mybatisx.webx.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


@Component
@Order(Integer.MAX_VALUE - 1)

public class FeignHandler implements InvocationHandler {

    public FeignHandler() {
        System.out.println("66");
    }

    private static HttpHeaders headers;

    static {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

    }

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        var MD = FireFactory.getFactory().getMD(method);

        String svcName = "",version="";

        var webxService = AnnotationUtils.findAnnotation(MD.getDaoClass(),WebxService.class);

        if (webxService != null) {
            svcName= webxService.value();
            version = webxService.version();
        }
        if (StringUtils.isEmpty(svcName)) {
            throw new IllegalArgumentException(StringUtils.join("包名：", "pkName ", "没有配置服务名"));
        }
        if (StringUtils.isEmpty(version)) {
            throw new IllegalArgumentException(StringUtils.join("包名：", "pkName ", "没有配置版本号"));
        }

        var PD = MD.getParameterDescriptors();
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        int i = 0;
        for (var arg : args) {
            postParameters.add(PD.get(i).getName(),arg);
            i++;
        }

        var servicePath = String.join("/", svcName, version);

        var clazz0 = MD.getDaoClass();
        if (!clazz0.isInterface()) {

            var faces = clazz0.getInterfaces();
            if (faces.length > 0) {
                clazz0 = faces[0];
            }
        }

        var uri = clazz0.getSimpleName() + "/" + MD.getMethod().getName() + "?city=sz";
        uri = uri.toLowerCase();

        var params = new HttpEntity<String>(JSON.toJSONString(postParameters), headers);

        var json = "";
        int retry = 0;
        while (retry < 3) {

            try {

                var serviceInstance = loadBalancerClient.choose(servicePath);

                var hostAndPort = StringUtils.join(serviceInstance.getHost(), ":", serviceInstance.getPort());

                var url = StringUtils.join("http://", hostAndPort, "/", uri);

                json = restTemplate.postForObject(url, params, String.class);
                retry = 3;
            } catch (Exception ex) {

                System.out.println("retry" + retry);
                if (retry == 2) {
                    throw ex;
                }
                retry++;
            } finally {

            }


        }


        var MD_RD = MD.getReturnDescriptor();

        ResponseData response = null;
        if (MD_RD.isList()) {
            var clazz = MD.getReturnDescriptor().getMappedClass();
            response = JsonUtil.parse2list(json, clazz);
        } else {
            var clazz = (Class<?>) MD_RD.getType();
            response = JsonUtil.parseToMap(json, clazz);
        }

        if (response.getError() == 10) {
            throw new BizException(response.getMsg().toString());
        } else if (response.getError() > 0) {
            throw new Exception(response.getMsg().toString());
        }
        return response.getData();
    }
}
