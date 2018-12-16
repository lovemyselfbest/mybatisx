package com.github.mybatisx.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class WebxConfig  implements EnvironmentAware {


    @Bean
    public RestTemplate restTemplate(){

        return  new  RestTemplate();
    }



    @Bean
    @ConditionalOnMissingBean(ZookeeperRegistration.class)
    public ServiceInstanceRegistration serviceInstanceRegistration(ApplicationContext context, ZookeeperDiscoveryProperties properties) {
        String appName = context.getEnvironment().getProperty("spring.application.name",
                "application");
        String version = context.getEnvironment().getProperty("spring.application.version",
                "v1");
        appName=String.join("/",appName,version);
        String host = properties.getInstanceHost();
        if (!StringUtils.hasText(host)) {
            throw new IllegalStateException("instanceHost must not be empty");
        }

        ZookeeperInstance zookeeperInstance = new ZookeeperInstance(context.getId(),
                appName, properties.getMetadata());
        ServiceInstanceRegistration.RegistrationBuilder builder = ServiceInstanceRegistration.builder().address(host)
                .name(appName).payload(zookeeperInstance)
                .uriSpec(properties.getUriSpec());

        if (properties.getInstanceSslPort() != null) {
            builder.sslPort(properties.getInstanceSslPort());
        }
        if (properties.getInstanceId() != null) {
            builder.id(properties.getInstanceId());
        }


        // TODO add customizer?

        return builder.build();
    }

    private Environment env;
    @Override
    public void setEnvironment(Environment environment) {
        this.env= environment;
    }
}
