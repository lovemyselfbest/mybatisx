package com.github.mybatisx.aspect;

import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.cache.CacheableOperator;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.config.DataSourceContextHolder;
import com.github.mybatisx.mybatisx.PageUtil;
import com.github.pagehelper.Page;
import lombok.SneakyThrows;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.binding.MapperProxy;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//MethodBeforeAdvice
public class cacheMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        //获得目标方法标签里的值
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();

        var MD = FireFactory.getFactory().getMD(method);
        var sharding = MD.getShardingAnno();
        var dbKey = sharding.getDataSourceFactoryName(null);
        DataSourceContextHolder.setDBKey(dbKey);
        boolean isPaging = false;
        QueryBase firstQuery = null;
        if (args.length == 1) {
            var query = args[0];

            if (query instanceof QueryBase) {
                firstQuery = (QueryBase) query;
                isPaging = PageUtil.IsPageing(firstQuery);

                if (isPaging) {
                    PageUtil.setPageArgs(firstQuery);
                }


            }


        }

        Object v = null;
        if (MD.isUseCache() == false || isPaging) {

            v = invocation.proceed();
        } else if (!IsOnlyCache(args[0], MD.getQueryCacheFields())) {
            v = invocation.proceed();
        } else {
            //处理缓存
            var field = getOnlyCacheField(args[0], MD.getQueryCacheFields());
            var operator = new CacheableOperator(invocation, MD, field);

            v = operator.invoke();

        }


        if (isPaging) {
            //v = (Page) v;
            long count = ((Page) v).getTotal();
            firstQuery.setCount((int)count);
             //FieldUtils.writeField(firstQuery,"Count",(int)count,true);
            //RpcContext.getContext().setAttachment("QUERY_COUNT", String.valueOf(89));
            //  RpcContext.getServerContext().setAttachment("QUERY_COUNT", String.valueOf(count));
        }


        DataSourceContextHolder.clearDBKey();

        return v;
    }

    @SneakyThrows
    private static Field getOnlyCacheField(Object query, Field[] cacheFields) {
        for (var f : cacheFields) {
            var v = FieldUtils.readField(f, query, true);

            if (v != null) {

                return f;


            }
        }
        return null;
    }

    @SneakyThrows
    private static Boolean IsOnlyCache(Object query, Field[] cacheFields) {

        int i = 0;
        for (var f : cacheFields) {
            var v = FieldUtils.readField(f, query, true);

            if (v != null) {

                i++;

            }

            if(i>1){
                return false;
            }
        }

        if (i != 1) {
            return false;
        }

        var fields = FieldUtils.getAllFields(query.getClass());

        for (var field : fields) {

            var ignored = false;
            for (var f : cacheFields) {
                if (f.getName().equals(field.getName())) {
                    ignored = true;
                    break;
                }
            }
            if (ignored)
                continue;
            var v = FieldUtils.readField(field, query, true);

            if (v != null) {

                return false;

            }

        }

        return true;
    }
}
