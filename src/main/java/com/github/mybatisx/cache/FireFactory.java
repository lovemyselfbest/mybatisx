package com.github.mybatisx.cache;


import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.annotation.Sharding;
import com.github.mybatisx.annotation.db;
import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.descriptor.MethodDescriptor;
import com.github.mybatisx.descriptor.MethodUtil;
import com.github.mybatisx.sharding.DatabaseShardingStrategy;
import com.github.mybatisx.util.MetaUtil;
import com.github.mybatisx.util.TypeResolver;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Method;
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

    public MethodDescriptor getMD(Method method){
        var MD = fireLineMap.get(method);
            return MD;


    }

    public MethodDescriptor setMD(Method method,Class<?> daoClazz) {

        var opt = fireLineMap.values().stream().filter(p -> p.getMethod() == method).findFirst();
        if (opt.isPresent())
            return opt.get();





        var methodDescriptor = MethodUtil.getMethodDescriptor(daoClazz,method,true);

        //methodDescriptor.isUseCache();

if(daoClazz.getName().contains("UserDao")){

    var mm="";
}

      //  var builder = Fire.builder().method(method).Db(defaultdb).methodReturnType(method.getReturnType());

      //  builder.Sharding(dbSharding);

        var parTypes = method.getParameterTypes();

        if (parTypes.length == 1) {
            var type0 = parTypes[0];
            if (QueryBase.class.isAssignableFrom(type0)) {

                var fields = FieldUtils.getAllFields(type0);

                for (var field : fields) {

                    var cacheBy = field.getAnnotation(CacheBy.class);

                    if (cacheBy != null) {
                        var cachePrefix=cacheBy.key();
                        if(StringUtils.isEmpty(cachePrefix)){
                            cachePrefix=type0.getName()+"_";
                        }



                        methodDescriptor.setQueryCacheField(field);
                        methodDescriptor.setCachePrefix(cachePrefix);
                        methodDescriptor.setCacheTTL(cacheBy.ttl());

                        var fieldName = field.getName();

                        if (fieldName.contains("_")) {
                            var arr = fieldName.split("_");

                            fieldName = arr[0];
                        }

                        var meta = MetaUtil.getQueryMeta(type0);

                        var modelClazz = meta.getModelClazz();

                        var field4Model = FieldUtils.getField(modelClazz, fieldName, true);

                        if (field4Model != null) {

                            methodDescriptor.setModelCacheField(field4Model);
                            methodDescriptor.setUseCache(true);
                            var keyType = TypeResolver.getActualType2(field4Model.getType());

                            methodDescriptor.setKeyType(keyType);

                            break;
                        }

                    }

                }


            }

        }

       // var fire = builder.build();

        fireLineMap.putIfAbsent(method, methodDescriptor);

        return methodDescriptor;

    }
}
