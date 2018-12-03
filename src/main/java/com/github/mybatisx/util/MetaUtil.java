package com.github.mybatisx.util;

import com.github.mybatisx.annotation.Column;
import com.github.mybatisx.base.QueryMeta;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MetaUtil {

    private static Map<Class<?>, Set<String>> allTypeMaps = new LinkedHashMap<>();

    public static Set<String> getAllFields(Class<?> clazz) {

        if (allTypeMaps.containsKey(clazz))
            return allTypeMaps.get(clazz);

        var fileds = FieldUtils.getAllFields(clazz);
        Set<String> names = new HashSet<>();
        for (var filed : fileds) {

            names.add(filed.getName());
        }

        allTypeMaps.putIfAbsent(clazz, names);

        return names;
    }

    private static Map<Class<?>, Field[]> allTypeMaps2 = new LinkedHashMap<>();

    public static Field[] getAllFields2(Class<?> clazz) {

        if (allTypeMaps2.containsKey(clazz))
            return allTypeMaps2.get(clazz);

        var fileds = FieldUtils.getAllFields(clazz);


        allTypeMaps2.putIfAbsent(clazz, fileds);

        return fileds;
    }


    public static Class<?> getModelClassByQueryType(Class<?> queryClazz) {

        Type type = queryClazz.getGenericSuperclass();
        //获取返回值的泛型参数
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            type = actualTypeArguments[0];
        }

        if (type.getClass().isArray()) {
            type = type.getClass().getComponentType();
        }

        return (Class<?>) type;
    }

    private static Map<Class<?>, QueryMeta> allQueryMetas = new LinkedHashMap<>();

    public static QueryMeta getQueryMeta(Class<?> clazz) {


        if (allQueryMetas.containsKey(clazz))
            return allQueryMetas.get(clazz);

        var modelClazz = getModelClassByQueryType(clazz);

        var fileds = FieldUtils.getAllFields(clazz);

        var queryMeta = new QueryMeta();

        for (var filed : fileds) {

            var queryFiledName = filed.getName();

            if(StringUtils.equalsIgnoreCase(queryFiledName,"take"))
                continue;
            if(StringUtils.equalsIgnoreCase(queryFiledName,"skip"))
                continue;
            if(StringUtils.equalsIgnoreCase(queryFiledName,"count"))
                continue;
            if(StringUtils.equalsIgnoreCase(queryFiledName,"orderfield"))
                continue;
            if(StringUtils.equalsIgnoreCase(queryFiledName,"OrderDirection"))
                continue;

            String modelFiledName = queryFiledName;





            var filedx = new QueryMeta.Filedx();

            try {

                if(modelFiledName.contains("_")) {
                    var arr = modelFiledName.split("_");
                    var na = arr[0];
                    Field field2 = modelClazz.getDeclaredField(na);
                    var dbName= field2.getName();
                    var cloumn = field2.getAnnotation(Column.class);

                    if (cloumn != null) {
                        dbName = cloumn.value().trim();
                    }
                    filedx.setCloumnName(queryFiledName);
                    filedx.setDbCloumnName(dbName);
                    filedx.setThisType(field2.getType());


                    queryMeta.getFiled2Filedx().putIfAbsent(filed, filedx);
                }
            } catch (NoSuchFieldException e) {
                // e.printStackTrace();

            }


        }
        queryMeta.setQueryClazz(clazz);
        queryMeta.setModelClazz(modelClazz);
        allQueryMetas.putIfAbsent(clazz, queryMeta);

        return queryMeta;

    }
}
