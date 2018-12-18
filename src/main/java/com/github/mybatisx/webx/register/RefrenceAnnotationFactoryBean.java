package com.github.mybatisx.webx.register;

import org.springframework.beans.factory.FactoryBean;

public class RefrenceAnnotationFactoryBean<T> implements FactoryBean<T> {

    public RefrenceAnnotationFactoryBean(){}

    public RefrenceAnnotationFactoryBean(Class<T> mapperInterface) {
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
