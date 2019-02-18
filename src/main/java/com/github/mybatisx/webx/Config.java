package com.github.mybatisx.webx;


import cn.le55.zookeeper.DefaultZkClient;
import cn.le55.zookeeper.ZkClient;
import com.github.mybatisx.webx.register.WebxServiceImplScanner;
import com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@ConditionalOnWebApplication
public class Config implements EnvironmentAware {


    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(ZkClient.class)
    public ZkClient zkClient() {

        return new DefaultZkClient("10.1.62.23:2181");
    }

    @Bean
    @ConditionalOnMissingBean(WebxServiceImplScanner.class)
    public WebxServiceImplScanner webxServiceImplScanner() {

        List<String> packages = Lists.newArrayList("**.implement.**");

        var scanner = new WebxServiceImplScanner();
        scanner.setPackages(packages);

        return scanner;

    }


//    @Bean
//   @ConditionalOnProperty(value = "spring.cloud.zookeeper.discovery.register", havingValue = "true")
//    public ServiceInstanceRegistration serviceInstanceRegistration(ApplicationContext ctx, ZookeeperDiscoveryProperties properties) {
//
//        String appName = env.getProperty("spring.application.name", "");
//
//        if (StringUtils.isEmpty(appName)) {
//            throw new IllegalArgumentException("spring.application.name is not set");
//        }
//
//        var versionName = String.format("%s.version", appName);
//
//        String version = env.getProperty(versionName, "");
//
//        if (StringUtils.isEmpty(version)) {
//            throw new IllegalArgumentException(String.format("%s.version is not set", appName));
//        }
//
//        appName = String.join("/", appName, version);
//
//        String host = env.getProperty("MY_APP_HOSTIP", "");
//        if(StringUtils.isEmpty(host)){
//            host = properties.getInstanceHost();
//        }
//
//        if (!StringUtils.hasText(host)) {
//            throw new IllegalStateException("instanceHost must not be empty");
//        }
//
//        String port = env.getProperty("MY_APP_PORT", "");
//
//        if (StringUtils.hasText(port)) {
//            properties.setInstancePort(Integer.parseInt(port));
//        }
//        ZookeeperInstance zookeeperInstance = new ZookeeperInstance(ctx.getId(), appName, properties.getMetadata());
//        ServiceInstanceRegistration.RegistrationBuilder builder = ServiceInstanceRegistration.builder().address(host)
//                .name(appName).payload(zookeeperInstance)
//                .uriSpec(properties.getUriSpec());
//
//        if (properties.getInstanceSslPort() != null) {
//            builder.sslPort(properties.getInstanceSslPort());
//        }
//        if (properties.getInstanceId() != null) {
//            builder.id(properties.getInstanceId());
//        }
//
//
//        // TODO add customizer?
//
//        return builder.build();
//    }

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
