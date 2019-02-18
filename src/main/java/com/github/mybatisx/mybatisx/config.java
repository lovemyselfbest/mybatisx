package com.github.mybatisx.mybatisx;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.github.mybatisx.aspect.AspectJExpressionPointcutX;
import com.github.mybatisx.aspect.cacheMethodInterceptor;
import com.github.mybatisx.config.DynamicDataSource;
import com.github.mybatisx.util.TempUtil;

import com.github.pagehelper.PageInterceptor;
import lombok.SneakyThrows;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.error.ErrorController;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;


public class config implements EnvironmentAware {

    private Environment env;


//@Bean(name="aa")
//@ConditionalOnMissingBean
//public PointcutAdvisor staticMethodMatcherPointcutAdvisor(){
//    var a= new cacheAdvisor();
//    var arount= new cacheMethodAroundAdvice();
//    a.setAdvice(arount);
//    return a;
//}

//    @Bean
//    public ErrorController notFoundException (){
//        return  new NotFoundException();
//    }
    @Bean
    @ConditionalOnMissingBean
    public AspectJExpressionPointcut aspectJExpressionPointcut() {
        var pointcut = new AspectJExpressionPointcutX();
        var pkg = TempUtil.daoPackageNames;
        pointcut.setExpression("execution(public * com.github.userservice..*.*(..))");
        return pointcut;
    }

    @Bean
    @ConditionalOnMissingBean
    public Advisor per() {
        var advice = new cacheMethodInterceptor();
        return new DefaultPointcutAdvisor(aspectJExpressionPointcut(), advice);
    }

//public test getcacheAdvisor(){
//    var p= new ProxyFactoryBean();
//    AspectJExpressionPointcut pointcut= new AspectJExpressionPointcut();
//    pointcut.setExpression("execution(public * com.github.mybatisx_demo_api..*.*(..))");
//    var arount= new cacheMethodAroundAdvice();
//    cacheAdvisor advisor = new cacheAdvisor(pointcut,arount);
//    p.addAdvisor(advisor);
//    p.setProxyTargetClass(true);
//
//   // var p= new ProxyFactoryBean();
//   // p.setInterceptorNames(new String[]{"aa"});
//
//   // p.setProxyTargetClass(true);
//    return new test();
//}

    @Bean
    @ConditionalOnMissingBean
    @SneakyThrows
    public DataSource getDataSource() {


        var dynamicDataSource = new DynamicDataSource();

        var keys = StringUtils.split("order_sz,", ",");

        var dsMaps = new HashMap<Object, Object>();

        for (var key : keys) {

            if (StringUtils.isEmpty(key))
                continue;
            Properties props = new Properties();
            props.put("driverClassName", env.getProperty(String.format("shihang.datasource.%s.driver-class-name", key), ""));
            props.put("url", env.getProperty(String.format("shihang.datasource.%s.url", key), ""));
            props.put("username", env.getProperty(String.format("shihang.datasource.%s.username", key), ""));
            props.put("password", env.getProperty(String.format("shihang.datasource.%s.password", key), ""));
            props.put("minldle", 88);
            props.put("initialSize", "7");
            DataSource dataSource  = DruidDataSourceFactory.createDataSource(props);
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
    public PlatformTransactionManager transactionManager(DataSource ds) {

        return new DataSourceTransactionManager(ds);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
