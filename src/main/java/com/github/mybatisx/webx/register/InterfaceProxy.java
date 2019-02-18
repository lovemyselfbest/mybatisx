package com.github.mybatisx.webx.register;

import com.github.mybatisx.annotation.WebxService;
import com.github.mybatisx.util.SpringUtils;
import org.checkerframework.checker.index.qual.PolyUpperBound;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class InterfaceProxy implements InvocationHandler {


    private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object ret = null;
        try {

            var dao = method.getDeclaringClass();

            var anno = AnnotationUtils.findAnnotation(dao,WebxService.class);

            if(anno!=null){
                //var beanName = dao.getName() + "Impl";
                var bean = SpringUtils.getBean(dao);
                ret = method.invoke(bean, args);
            }


        } catch (Exception ex) {
            System.err.print(ex);
        } finally {
            System.out.println(df.format(new Date()) + "  " + proxy.getClass() + "   ObjectProxy execute:" + method.getName());

        }


//        java.util.Map<Thread, StackTraceElement[]> ts = Thread.getAllStackTraces();
//
//        StackTraceElement[] ste = ts.get(Thread.currentThread());
//        for (StackTraceElement s : ste) {
//            System.out.println(s.toString());
//        }

        return ret;
    }

    public static <T> T newInstance(Class<T> innerInterface) {
        ClassLoader classLoader = innerInterface.getClassLoader();
        Class[] interfaces = new Class[]{innerInterface};
        InterfaceProxy proxy = new InterfaceProxy();
        var v = (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
        //System.out.println("-->"+v.getClass());
        return v;
    }
}
