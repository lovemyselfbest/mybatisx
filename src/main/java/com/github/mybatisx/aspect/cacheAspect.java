package com.github.mybatisx.aspect;

import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.cache.CacheableOperator;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.config.DataSourceContextHolder;
import com.github.mybatisx.mybatisx.PageUtil;
import com.github.pagehelper.Page;

import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.binding.MapperProxy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Aspect
public class cacheAspect {

    public cacheAspect() {

        System.out.println("33");
    }


//    @Pointcut("execution(public * com.*.*.dao..*.*(..))")
//    public void dao() {
//    }
//    @Pointcut("@annotation(com.github.mybatisx.annotation.WebxService)")
//    public void dao() {
//    }
   // @Around("dao()")
    public Object beforeSwitchDb(ProceedingJoinPoint point) throws Throwable {

        //获得目标方法标签point
        var k1 = (MethodInvocationProceedingJoinPoint) point;

        var k3 = k1.getTarget();
        var handler = Proxy.getInvocationHandler(k3);
        var m = (MapperProxy) handler;
      //  var dao = (Class<?>) FieldUtils.readDeclaredField(m, "mapperInterface", true);

        //  ((MapperProxy) ((Proxy) ((ReflectiveMethodInvocation) ().methodInvocation).target).h).mapperInterface

        var methodSignature = (MethodSignature) point.getSignature();

        //获得目标方法标签里的值
        Method method = methodSignature.getMethod();

        var MD = FireFactory.getFactory().getMD(method);

        var sharding =MD.getShardingAnno();
        var  dbKey= sharding.getDataSourceFactoryName(null);

        DataSourceContextHolder.setDBKey(dbKey);

        boolean isPaging= false;
        var args = point.getArgs();

        QueryBase firstQuery=null;

        if(args.length==1){

            var query= point.getArgs()[0];

            if(query instanceof QueryBase){
                firstQuery=(QueryBase)query;
                 isPaging = PageUtil.IsPageing(firstQuery);

                if (isPaging) {
                    PageUtil.setPageArgs(firstQuery);
                }


            }


        }

        Object v= null;
        if (MD.isUseCache() == false) {

            v= point.proceed(args);
       // }
        //else if (!isPaging || !IsOnlyCache(args[0], MD.getQueryCacheField())) {
        //    v= point.proceed(args);
        } else {
            //处理缓存

           // var operator = new CacheableOperator(point, MD);

           // v = operator.invoke();

        }




        if (isPaging) {
            v= (Page)v;
           // long count = ((Page) v).getTotal();

          //  FieldUtils.writeField(firstQuery,"Count",(int)count,true);
            //RpcContext.getContext().setAttachment("QUERY_COUNT", String.valueOf(89));
            //  RpcContext.getServerContext().setAttachment("QUERY_COUNT", String.valueOf(count));
        }



        DataSourceContextHolder.clearDBKey();

        return v;
    }

    @SneakyThrows
    private static Boolean IsOnlyCache(Object query, Field cacheField) {


        var fields = FieldUtils.getAllFields(query.getClass());

        for (var field : fields) {

            if (cacheField.getName().equals(field.getName()))
                continue;

            var v = FieldUtils.readField(field, query,true);

            if (v != null) {

                return false;

            }

        }

        return true;
    }
}
