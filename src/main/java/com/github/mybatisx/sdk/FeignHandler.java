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
@Order(Integer.MAX_VALUE-1)
public class FeignHandler implements InvocationHandler {

    public FeignHandler(){
        System.out.println("66");
    }
    private static HttpHeaders headers;

    static {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

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



       var dao = method.getDeclaringClass();
        var pkName = dao.getPackageName();
       var version= WebxReferenceUtil.getValue(pkName);
       // var version = env.getProperty(pkName.toLowerCase(), "");
        if (StringUtils.isEmpty(version)) {
            throw new IllegalArgumentException(StringUtils.join("包名：", "pkName ", "没有配置版本号"));
        }
        var webx = dao.getAnnotation(WebxService.class);

        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        int i = 1;
        for (var arg : args) {
            postParameters.add("param" + i, JSON.toJSONString(arg));
            i++;
        }

        var servicePath = String.join("/", webx.value(), version);


        var MD = FireFactory.getFactory().getMD(method);

        var uri = MD.getDaoClass().getSimpleName().toLowerCase() + "/" + MD.getMethod().getName() + "?city=sz";

        var params = new HttpEntity<MultiValueMap<String, Object>>(postParameters, headers);

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
            throw new BizException(response.getMsg());
        } else if (response.getError() > 0) {
            throw new Exception(response.getMsg());
        }
        return response.getData();
    }
}
