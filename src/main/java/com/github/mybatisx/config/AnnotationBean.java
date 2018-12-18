package com.github.mybatisx.config;

import com.github.mybatisx.annotation.WebxReference;
import com.github.mybatisx.annotation.WebxService;
import com.github.mybatisx.sdk.Sdk;
import com.github.mybatisx.util.SpringUtils;
import com.github.mybatisx.util.WebxReferenceUtil;
import org.bouncycastle.cms.PasswordRecipientId;
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

@Component
@Order(Integer.MAX_VALUE-20)
public class AnnotationBean implements DisposableBean, BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware, EnvironmentAware {
   // @Autowired
   // private Sdk sdk;

    //    public AnnotationBean(Sdk sdk){
//        this.sdk=sdk;
//    }
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

      var webxService=  AnnotationUtils.findAnnotation(bean.getClass(), WebxService.class);

        if(webxService!=null){


        }

        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                WebxReference reference = field.getAnnotation(WebxReference.class);
                if (reference != null) {
                    Object value = refer(reference, field.getType());
                    if (value != null) {
                        field.set(bean, value);
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


        var version= reference.value();
        if(version.startsWith("${") && version.endsWith("}")){
            var  envKey=version.substring(2,version.length()-1);
            version= env.getProperty(envKey,"");
        }

        WebxReferenceUtil.add(referenceClass.getPackageName(),version);

        // var key= referenceClass.getPackageName() + ":" + reference.version();
        // ReferenceBean<?> referenceConfig = referenceConfigs.get(key);
        // if (referenceConfig == null) {
        // referenceConfig = new ReferenceBean<Object>(reference);

        //   }
        // referenceConfigs.putIfAbsent(key, referenceConfig);
        // referenceConfig = referenceConfigs.get(key);
        // return referenceConfig.get();
        //var sdk = SpringUtils.getBean(Sdk.class);
        var sdk=  ctx.getBean(Sdk.class);
        var obj = sdk.getHandler(referenceClass);

        return null;
     //   return obj;
    }

   private Environment env;
    @Override
    public void setEnvironment(Environment environment) {
        env= environment;
    }
}
