package com.github.mybatisx.webx.register;

import com.github.mybatisx.annotation.WebxService;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

public class AnnotationScanner  extends ClassPathBeanDefinitionScanner {

    public AnnotationScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
    @Override
    protected void registerDefaultFilters() {
        //扫描规则
        this.addIncludeFilter(new AnnotationTypeFilter(WebxService.class));
        //this.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
    }
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            //BeanFactory.getBean的方法跟进去后有一个判断是不是FactroyBean类型的。如果是从FactroyBean.getObejct获取
            //RefrenceAnnotationFactoryBean 实现了FactoryBean

            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
           definition.setBeanClass(RefrenceAnnotationFactoryBean.class);

           //var mutablePropertyValues= definition.getPropertyValues();
           //mutablePropertyValues.add("interfaceClazz",holder.getSource());
          //  definition.setPropertyValues(mutablePropertyValues);

          //  this.getRegistry().registerBeanDefinition(holder.getBeanName(), definition);
        }
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        //return  true;
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }


}
