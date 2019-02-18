package com.github.mybatisx.webx.register;

import cn.le55.zookeeper.ZkClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@Log4j2
public class Listener3 implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private ZkClient zk;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        log.info("ContextClosedEvent 事件开始");
        zk.close();
        log.info("ContextClosedEvent 事件结束");
    }
}
