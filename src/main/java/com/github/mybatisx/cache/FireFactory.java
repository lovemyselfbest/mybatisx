package com.github.mybatisx.cache;


import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.annotation.ID;
import com.github.mybatisx.base.ModelBase;
import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.descriptor.MethodDescriptor;
import com.github.mybatisx.descriptor.MethodUtil;

import com.github.mybatisx.util.MetaUtil;
import com.github.mybatisx.util.TypeResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


        var MD = MethodUtil.getMethodDescriptor(daoClazz, method, false);

        var op= MD.getAnnotation(Update.class);
        if(op!=null){
            MD.setMybatisOperation(Update.class);
        }
        var op2= MD.getAnnotation(Select.class);
        if(op2!=null){
            MD.setMybatisOperation(Select.class);
        }

        var op3= MD.getAnnotation(Delete.class);
        if(op3!=null){
            MD.setMybatisOperation(Delete.class);
        }
        var op4= MD.getAnnotation(Insert.class);
        if(op4!=null){
            MD.setMybatisOperation(Insert.class);
        }

        var parTypes = MD.getParameterDescriptors();

        var cacheBy=method.getAnnotation(CacheBy.class);

       if(cacheBy==null)  {
           cacheBy= MD.getAnnotation(CacheBy.class);
       }

        if (cacheBy != null) {

            var script = cacheBy.cacheKey();

            MD.setCachePrefix(cacheBy.prefix());

            Pattern p = Pattern.compile(":\\d+(\\.\\w+)?");

            Matcher m = p.matcher(script);

            var maps = new HashMap<Integer, String>();
            int i = 1;
            while (m.find()) {
                maps.putIfAbsent(i, m.group());
                i++;
            }


            var index=1;
            String defalutFieldName="";

            for (Map.Entry<Integer, String> entry : maps.entrySet()) {

                var k = entry.getKey();
                var v = entry.getValue();

                index=k-1;

                if(v.contains(".")){

                    var p1= v.split("\\.");
                    var p2=p1[p1.length-1];
                    defalutFieldName=p2;
                }


                script= script.replace(":"+k,"#param"+k);

                MD.setCacheKey(script);

            }






            var argItType = parTypes.get(index).getRawType();

            if (ModelBase.class.isAssignableFrom(argItType)) {


                if (StringUtils.isNotEmpty(defalutFieldName)) {



                 var field=FieldUtils.getDeclaredField(argItType, defalutFieldName,true);

                    MD.setCacheField(field);

                    MD.setCacheTTL(cacheBy.ttl());
                    MD.setUseCache(true);
                    MD.setKeyType(field.getType());
                }

            }

            if (QueryBase.class.isAssignableFrom(argItType)) {


                if (StringUtils.isNotEmpty(defalutFieldName)) {

                var argItModleType= MetaUtil.getModelClassByQueryType(argItType);

                    var field=FieldUtils.getDeclaredField(argItModleType, defalutFieldName,true);

                    MD.setCacheField(field);

                    MD.setCacheTTL(cacheBy.ttl());
                    MD.setUseCache(true);

                    MD.setKeyType(field.getType());
                }

                var fields = FieldUtils.getAllFields(argItType);

                var cacheFields = new ArrayList<Field>();

                var inStr= new String[]{"eq","in"};
                for (var field : fields) {

                    var fieldNameqian = field.getName();

                    if (fieldNameqian.contains("_")) {
                        var arr = fieldNameqian.split("_");

                        fieldNameqian = arr[0];

                        if(!ArrayUtils.contains(inStr,arr[1])){
                            continue;
                        }
                    }

                    if (defalutFieldName.equalsIgnoreCase(fieldNameqian)) {

                        cacheFields.add(field);
                    }
                }
                var fieldArray = new Field[cacheFields.size()];

                MD.setQueryCacheFields(cacheFields.toArray(fieldArray));


            }

        }




        fireLineMap.putIfAbsent(method, MD);

        return MD;

    }
}
