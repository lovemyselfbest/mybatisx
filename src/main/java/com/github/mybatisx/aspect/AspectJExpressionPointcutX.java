package com.github.mybatisx.aspect;

import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.cache.FireFactory;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

public class AspectJExpressionPointcutX extends AspectJExpressionPointcut {

    public AspectJExpressionPointcutX() {
        super();
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new DynamicMethodMatcherX();
    }

    private class DynamicMethodMatcherX extends StaticMethodMatcher {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {

           var mapper= AnnotationUtils.findAnnotation(targetClass, Mapper.class);

            if(mapper!=null){
                // var dao= FireFactory.getFactory().getMD(method).getDaoClass();
                return  true;
            }

            return false;
        }


    }

}
