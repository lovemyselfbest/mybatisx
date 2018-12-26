package com.github.mybatisx.descriptor;

import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.annotation.Sharding;
import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.sharding.DatabaseShardingStrategy;
import com.github.mybatisx.util.MetaUtil;
import com.github.mybatisx.util.TypeResolver;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MethodDescriptor {


    private final String name;
    private final Method method;
    private final Class<?> daoClass;
    private final ReturnDescriptor returnDescriptor;
    private final List<ParameterDescriptor> parameterDescriptors;

    private String cachedSQL;

    private MethodDescriptor(
            String name, Method method, Class<?> daoClass, ReturnDescriptor returnDescriptor,
            List<ParameterDescriptor> parameterDescriptors) {
        this.name = name;
        this.method = method;
        this.daoClass = daoClass;
        this.returnDescriptor = returnDescriptor;
        this.parameterDescriptors = Collections.unmodifiableList(parameterDescriptors);
    }

    public static MethodDescriptor create(
            String name, Method method, Class<?> daoClass, ReturnDescriptor returnDescriptor,
            List<ParameterDescriptor> parameterDescriptors) {
        return new MethodDescriptor(name, method, daoClass, returnDescriptor, parameterDescriptors);
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public Class<?> getDaoClass() {
        return daoClass;
    }


//    public Type getReturnType() {
//        return returnDescriptor.getType();
//    }
//
//    public Class<?> getReturnRawType() {
//        return returnDescriptor.getRawType();
//    }

    // public List<Annotation> getAnnotations() {
    //  return returnDescriptor.getAnnotations();
    //  }

    public ReturnDescriptor getReturnDescriptor() {
        return returnDescriptor;
    }

    public List<ParameterDescriptor> getParameterDescriptors() {
        return parameterDescriptors;
    }

    public String getSQL() {
        if (cachedSQL != null) {
            return cachedSQL;
        }
        /// SQL sqlAnno = getAnnotation(SQL.class);
        String sql = "";
        //////
        cachedSQL = sql;
        return cachedSQL;
    }

    @Nullable
    public String getGlobalTable() {
//        var dbAnno = getAnnotation(db.class);
//        if (dbAnno == null) {
//            throw new DescriptionException("dao interface expected one @DB " +
//                    "annotation but not found");
//        }
//        String table = null;
//        if (Strings.isNotEmpty(dbAnno.table())) {
//            table = dbAnno.table();
//        }
//        return table;
        return "";
    }


    private final String defaultdb = "order_sz";

    public String getDataSourceFactoryName() {
//        var dbAnno = getAnnotation(db.class);
//        if (dbAnno == null) {
//            return  "order_sz";
//        }
//        return dbAnno.value();
        return "";
    }

    @Nullable
    public DatabaseShardingStrategy getShardingAnno() {

        DatabaseShardingStrategy dbSharding = null;
        var sharding = daoClass.getAnnotation(Sharding.class);

        if (sharding != null) {

            try {
                return dbSharding = sharding.databaseShardingStrategy().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //  return getAnnotation(Sharding.class);
        return null;
    }



    @Getter @Setter
    private boolean isUseCache;
    @Getter
    @Setter
    private Field[] queryCacheFields;
    @Getter
    @Setter
    private Field cacheField;

    @Getter
    @Setter
    private Class<?> mybatisOperation;
    @Getter
    @Setter
    private String cachePrefix;

    @Getter
    @Setter
    private String cacheKey;


    @Getter
    @Setter
    private Class<?> keyType;
    @Getter
    @Setter
    private Class<?> modelClazz;

    @Getter
    @Setter
    private String url;

    public List<Annotation> getAnnotations() {
        var annotations = new ArrayList<Annotation>();

        var a = this.method.getAnnotations();
        var b = this.daoClass.getAnnotations();
        annotations.addAll(Arrays.asList(a));
        annotations.addAll(Arrays.asList(b));
        return annotations;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    @Nullable
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return returnDescriptor.getAnnotation(annotationType);
    }


    @Getter
    @Setter
    private int cacheTTL;

    public boolean isReturnGeneratedId() {
//        return isAnnotationPresent(ReturnGeneratedId.class) ||
//                (name != null && name.contains("ReturnGeneratedId"));

        return false;
    }

}
