package com.github.mybatisx.webx.register;

import cn.le55.zookeeper.DefaultZkClient;
import cn.le55.zookeeper.ZkClient;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
public class Listener1 implements ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {

   @Autowired
    private ZkClient zk;

    @Override
    @SneakyThrows
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("ContextRefreshedEvent 事件开始注册");

        String appName = env.getProperty("spring.application.name", "");

        if (StringUtils.isEmpty(appName)) {
            throw new IllegalArgumentException("spring.application.name is not set");
        }

//        var versionName = String.format("%s.version", appName);
//
//        String version = env.getProperty(versionName, "");
//
//        if (StringUtils.isEmpty(version)) {
//            throw new IllegalArgumentException(String.format("%s.version is not set", appName));
//        }

      //  appName = String.join("/", appName, version);

        String host = env.getProperty("MY_APP_HOSTIP", "127.0.0.1");


        String port = env.getProperty("MY_APP_PORT", "8080");

      //  DefaultZkClient zk = new DefaultZkClient("10.1.62.23:2181");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        zk.createEphemeral("/service/"+appName+"/"+date,host+port);

        log.info("ContextRefreshedEvent 事件结束注册");

       // zk.close();
    }

    @Override
    public void setEnvironment(Environment environment) {
this.env=environment;
    }
    private  Environment env;
}
