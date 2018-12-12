package com.github.mybatisx.config;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.github.mybatisx.mybatis.LangDriverx;
import com.github.mybatisx.mybatis.MyBatisInterceptor;

import com.github.mybatisx.mybatis.MybatisxConfiguration;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperInstance;
import org.springframework.cloud.zookeeper.serviceregistry.ServiceInstanceRegistration;
import org.springframework.cloud.zookeeper.serviceregistry.ZookeeperRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

@Configuration

public class MybatisxConfig implements EnvironmentAware {


    private Environment env;

    public MybatisxConfig(){
        String  mm="";
    }

    @Bean
    public RestTemplate restTemplate(){

        return  new  RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(ZookeeperRegistration.class)
    public ServiceInstanceRegistration serviceInstanceRegistration( ApplicationContext context, ZookeeperDiscoveryProperties properties) {
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
    @Bean
    @ConditionalOnMissingBean
    public DataSource getDataSource()  {


        var dynamicDataSource = new DynamicDataSource();

        var keys = StringUtils.split("order_sz,", ",");

        var dsMaps = new HashMap<Object, Object>();

        for (var key : keys) {

            if(StringUtils.isEmpty(key))
                continue;
            Properties props = new Properties();
            props.put("driverClassName", env.getProperty(String.format("shihang.datasource.%s.driver-class-name", key), ""));
            props.put("url", env.getProperty(String.format("shihang.datasource.%s.url", key), ""));
            props.put("username", env.getProperty(String.format("shihang.datasource.%s.username", key), ""));
            props.put("password", env.getProperty(String.format("shihang.datasource.%s.password", key), ""));

            DataSource dataSource = null;
            try {
                dataSource = DruidDataSourceFactory.createDataSource(props);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dsMaps.put(key, dataSource);
        }
        dynamicDataSource.setTargetDataSources(dsMaps);
        return dynamicDataSource;
    }


    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory getSqlSessionFactory(DataSource ds) throws Exception {


        var factory = new SqlSessionFactoryBean();
        factory.setConfiguration(new MybatisxConfiguration());
        factory.setDataSource(ds);

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] setMapperLocations = null;
        try {
            setMapperLocations = resolver.getResources("classpath*:mappers/**/*.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        factory.setMapperLocations(setMapperLocations);

        // 设置别名包
//        sqlSessionFactoryBean.setTypeAliasesPackage("com.taotao.cart.pojo");


        var pro = new Properties();
        pro.setProperty("helperDialect", "mysql");
        var pageInterceptor = new PageInterceptor();
       pageInterceptor.setProperties(pro);

       var interceptors = new Interceptor[2];
       interceptors[0] = pageInterceptor;
       var it1 = new MyBatisInterceptor();
       interceptors[1] = it1;
        factory.setPlugins(interceptors);


//        Properties mybatisProperties = new Properties();
//        mybatisProperties.setProperty("mapperLocations", environment.getRequiredProperty("mybatis.mapperLocations"));
//
//        bean.setConfigurationProperties(mybatisProperties);


        var factory2 = factory.getObject();
        factory2.getConfiguration().setLogImpl(Log4j2Impl.class);

        factory2.getConfiguration().setDefaultScriptingLanguage(LangDriverx.class);
      //  var kk=factory2.getConfiguration().getIncompleteMethods();
        // factory2.getConfiguration().addIncompleteMethod(null);
        return factory2;

    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env= environment;
    }
}
