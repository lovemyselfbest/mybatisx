package com.github.mybatisx.config;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;


public class MapperScannerRegistrar2 implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, EnvironmentAware {


    private Environment env;
    private ResourceLoader resourceLoader;
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        List<String> basePackages = new ArrayList<String>();

        var packageStr = env.getProperty("com.github.mybatisx.mapperScan", "");
        var keys = StringUtils.split(packageStr, ",");


        for (String pkg : keys) {
            if (StringUtils.isNotEmpty(pkg)) {
                basePackages.add(pkg);
            }
        }

        String[] strings = new String[basePackages.size()];

        basePackages.toArray(strings);
        scanner.registerFilters();
        scanner.doScan(strings);
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        List<String> basePackages = new ArrayList<String>();

        var packageStr = env.getProperty("com.github.mybatisx.mapperScan", "");
        var keys = StringUtils.split(packageStr, ",");


        for (String pkg : keys) {
            if (StringUtils.isNotEmpty(pkg)) {
                basePackages.add(pkg);
            }
        }

        String[] strings = new String[basePackages.size()];

        basePackages.toArray(strings);
        scanner.registerFilters();
        scanner.doScan(strings);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
