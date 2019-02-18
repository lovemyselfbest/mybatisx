package com.github.mybatisx.webx.register;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;


@Log4j2
public class Listener2 implements ApplicationListener<ContextStoppedEvent> {
    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {

        log.info("ContextStoppedEvent 事件开始");

        log.info("ContextStoppedEvent 事件结束");
    }
}
