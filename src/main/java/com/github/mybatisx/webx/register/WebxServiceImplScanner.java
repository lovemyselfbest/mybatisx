package com.github.mybatisx.webx.register;

import com.github.mybatisx.annotation.WebxService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebxServiceImplScanner implements BeanFactoryPostProcessor {

    private static final List<String> DAO_ENDS = Arrays.asList("impl", "Impl");

    List<String> locationPatterns = new ArrayList<String>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
        for (Class<?> daoClass : findMangoDaoClasses()) {

            var interfaces = daoClass.getInterfaces();
            for (var face : interfaces){

                var anno= face.getAnnotation(WebxService.class);
                if(anno!=null){
                   //开始注册
                    GenericBeanDefinition bf = new GenericBeanDefinition();
                    bf.setBeanClassName(daoClass.getName());
                    // MutablePropertyValues pvs = bf.getPropertyValues();
                    // pvs.addPropertyValue("daoClass", daoClass);
                    bf.setBeanClass(daoClass);//factoryBeanClass
                    //  bf.setPropertyValues(pvs);
                    //  bf.setLazyInit(false);

                    bf.setPrimary(true);
                    dlbf.registerBeanDefinition(daoClass.getName(), bf);
                    //
                }


            }
        }
    }

    private List<Class<?>> findMangoDaoClasses() {
        try {
            List<Class<?>> daos = new ArrayList<Class<?>>();
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (String locationPattern : locationPatterns) {
                Resource[] rs = resourcePatternResolver.getResources(locationPattern);
                for (Resource r : rs) {

                    MetadataReader reader = metadataReaderFactory.getMetadataReader(r);
                   // AnnotationMetadata annotationMD = reader.getAnnotationMetadata();
                   //
                   // if (annotationMD.hasAnnotation(WebxServiceImpl.class.getName())) {
                        ClassMetadata clazzMD = reader.getClassMetadata();


                            var clazz= Class.forName(clazzMD.getClassName());
                            var webxService=AnnotationUtils.findAnnotation(clazz,WebxService.class);
                            if(webxService!=null){
                                daos.add(clazz);
                            }



                   // }
                }
            }
            return daos;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void setPackages(List<String> packages) {
        for (String p : packages) {
            for (String daoEnd : DAO_ENDS) {
                String locationPattern = "classpath*:" + p.replaceAll("\\.", "/") + "/**/*" + daoEnd + ".class";
              //  logger.info("trnas package[" + p + "] to locationPattern[" + locationPattern + "]");
                locationPatterns.add(locationPattern);
            }
        }
    }
}
