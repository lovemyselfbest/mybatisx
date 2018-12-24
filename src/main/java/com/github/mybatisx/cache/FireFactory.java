package com.github.mybatisx.cache;


import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.base.ModelBase;
import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.descriptor.MethodDescriptor;
import com.github.mybatisx.descriptor.MethodUtil;
import com.github.mybatisx.util.MetaUtil;
import com.github.mybatisx.util.TypeResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FireFactory {

    private static FireFactory factory = null;

    private static final Object locker = new Object();


    private static Map<Method, MethodDescriptor> fireLineMap;

    public static FireFactory getFactory() {
        if (factory == null) {
            synchronized (locker) {
                if (factory == null) {
                    fireLineMap = new HashMap<Method, MethodDescriptor>();
                    factory = new FireFactory();

                }
            }
        }

        return factory;
    }


    private String defaultdb = "order_sz";

    public MethodDescriptor getMD(Method method) {
        var MD = fireLineMap.get(method);
        if (MD == null) {
            var clazz = method.getDeclaringClass();
            MD = setMD(method, clazz);
            System.out.println("运行时获取MD，" + method.getName() + "  " + method.getDeclaringClass().getName());

        }
        return MD;


    }

    public MethodDescriptor setMD(Method method, Class<?> daoClazz) {

        var opt = fireLineMap.values().stream().filter(p -> p.getMethod() == method).findFirst();
        if (opt.isPresent())
            return opt.get();


        var methodDescriptor = MethodUtil.getMethodDescriptor(daoClazz, method, false);


        var parTypes = methodDescriptor.getParameterDescriptors();

        if (parTypes.size() == 1) {
            var arg0type = parTypes.get(0).getRawType();

            if (ModelBase.class.isAssignableFrom(arg0type)) {

                var field = CacheUtil.getCacheField(arg0type);

                if (field != null) {

                    var cacheBy = field.getAnnotation(CacheBy.class);
                    var cachePrefix = cacheBy.key();
                    if (StringUtils.isEmpty(cachePrefix)) {
                        cachePrefix = arg0type.getName() + "_";
                    }

                    methodDescriptor.setCacheField(field);
                    methodDescriptor.setCachePrefix(cachePrefix);
                    methodDescriptor.setCacheTTL(cacheBy.ttl());
                    methodDescriptor.setUseCache(true);
                    // var keyType = TypeResolver.getActualType2(field.getType());

                    methodDescriptor.setKeyType(field.getType());
                }

            }

            if (QueryBase.class.isAssignableFrom(arg0type)) {

                var modeType =   MetaUtil.getModelClassByQueryType(arg0type);
              //  var modeType = TypeResolver.getActualType(arg0type);

                var cacheField = CacheUtil.getCacheField( modeType);

                if (cacheField != null) {
                    var cacheBy = cacheField.getAnnotation(CacheBy.class);
                    var cachePrefix = cacheBy.key();
                    if (StringUtils.isEmpty(cachePrefix)) {
                        cachePrefix = arg0type.getName() + "_";
                    }

                    methodDescriptor.setCacheField(cacheField);
                    methodDescriptor.setCachePrefix(cachePrefix);
                    methodDescriptor.setCacheTTL(cacheBy.ttl());
                    methodDescriptor.setUseCache(true);
                    // var keyType = TypeResolver.getActualType2(field.getType());
                    methodDescriptor.setKeyType(cacheField.getType());
                }

                var fields = FieldUtils.getAllFields(arg0type);

                var cacheFields = new ArrayList<Field>();


                for (var field : fields) {

                    var fieldNameqian = field.getName();

                    if (fieldNameqian.contains("_")) {
                        var arr = fieldNameqian.split("_");

                        fieldNameqian = arr[0];
                    }


                    if (cacheField.getName().equalsIgnoreCase(fieldNameqian)) {

                        cacheFields.add(field);
                    }
                }
                var fieldArray = new Field[cacheFields.size()];

                methodDescriptor.setQueryCacheFields(cacheFields.toArray(fieldArray));


            }

        }


        // var fire = builder.build();

        fireLineMap.putIfAbsent(method, methodDescriptor);

        return methodDescriptor;

    }
}
