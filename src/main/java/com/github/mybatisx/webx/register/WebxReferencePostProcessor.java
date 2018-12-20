package com.github.mybatisx.webx.register;

import com.github.mybatisx.annotation.WebxReference;
import com.github.mybatisx.annotation.WebxRequestMapping;
import com.github.mybatisx.annotation.WebxService;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.sdk.Sdk;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Set;

@Component

public class WebxReferencePostProcessor implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware, EnvironmentAware {

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (beanName.toLowerCase().contains("userservice")) {
            String mm = "";
        }
//
//        Set<Class<?>> faceSet = new LinkedHashSet<>();
//
//        if(bean instanceof RefrenceAnnotationFactoryBean){
//
//            var clazz0 = ((RefrenceAnnotationFactoryBean) bean).getObjectType();
//            faceSet.add(clazz0);
//        }
//
//      var webxService=  AnnotationUtils.findAnnotation(bean.getClass(), WebxService.class);
//
//        if(webxService!=null){
//
//
//            var clazz= bean.getClass();
//
//            if(clazz.isInterface()){
//                faceSet.add(clazz);
//            }
//
//             if(!clazz.isInterface()){
//
//                var faces= clazz.getInterfaces();
//                for (var face:faces){
//
//                    faceSet.add(face);
//                }
//
//            }
//        }
//        for (var faceClazz:faceSet){
//
//            var anno= faceClazz.getAnnotation(WebxService.class);
//            if(anno!=null){
//                var ms= faceClazz.getMethods();
//                for(var m : ms){
//
//                    var webxRequestMapping= AnnotationUtils.findAnnotation(m, WebxRequestMapping.class);
//                    if(webxRequestMapping!=null){
//                        var md= FireFactory.getFactory().setMD(m,faceClazz);
//                        String mm="";
//                    }
//                }
//
//            }
//        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                var reference = field.getAnnotation(WebxReference.class);
                if (reference != null) {
                    Object value = refer(reference, field.getType());
                    if (value != null) {
                        field.set(bean, value);
                        var ms= field.getType().getMethods();
                        for(var m : ms){

                            var webxRequestMapping= AnnotationUtils.findAnnotation(m, WebxRequestMapping.class);
                            if(webxRequestMapping!=null){

                                 FireFactory.getFactory().setMD(m,value.getClass());

                            }
                        }
                    }
                }
            } catch (Throwable e) {
                // logger.error("Failed to init remote service reference at filed " + field.getName() + " in class " + bean.getClass().getName() + ", cause: " + e.getMessage(), e);
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    private Object refer(WebxReference reference, Class<?> referenceClass) { //method.getParameterTypes()[0]


//        var version= reference.value();
//        if(version.startsWith("${") && version.endsWith("}")){
//            var  envKey=version.substring(2,version.length()-1);
//            version= env.getProperty(envKey,"");
//        }


       // WebxReferenceUtil.add(referenceClass.getPackageName(), version);

        // var key= referenceClass.getPackageName() + ":" + reference.version();
        // ReferenceBean<?> referenceConfig = referenceConfigs.get(key);
        // if (referenceConfig == null) {
        // referenceConfig = new ReferenceBean<Object>(reference);

        //   }
        // referenceConfigs.putIfAbsent(key, referenceConfig);
        // referenceConfig = referenceConfigs.get(key);
        // return referenceConfig.get();
        //var sdk = SpringUtils.getBean(Sdk.class);
        var sdk = ctx.getBean(Sdk.class);
        var obj = sdk.getHandler(referenceClass);
        return obj;
    }

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }
}
