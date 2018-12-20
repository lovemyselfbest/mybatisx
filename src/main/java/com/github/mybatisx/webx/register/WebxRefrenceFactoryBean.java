package com.github.mybatisx.webx.register;

import org.springframework.beans.factory.FactoryBean;

public class WebxRefrenceFactoryBean<T> implements FactoryBean<T> {

    public WebxRefrenceFactoryBean(){}

    public WebxRefrenceFactoryBean(Class<T> mapperInterface) {
        this.interfaceClazz = mapperInterface;
    }
    @Override
    public T getObject() throws Exception {
        return (T) InterfaceProxy.newInstance(interfaceClazz);

    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClazz;

    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Class<T>  interfaceClazz;

}
