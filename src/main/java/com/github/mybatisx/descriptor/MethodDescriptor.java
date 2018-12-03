package com.github.mybatisx.descriptor;

import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.annotation.Sharding;
import com.github.mybatisx.annotation.db;
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
    private  final Method  method;
    private final Class<?> daoClass;
    private final ReturnDescriptor returnDescriptor;
    private final List<ParameterDescriptor> parameterDescriptors;

    private String cachedSQL;

    private MethodDescriptor(
            String name, Method method, Class<?> daoClass, ReturnDescriptor returnDescriptor,
            List<ParameterDescriptor> parameterDescriptors) {
        this.name = name;
        this.method= method;
        this.daoClass = daoClass;
        this.returnDescriptor = returnDescriptor;
        this.parameterDescriptors = Collections.unmodifiableList(parameterDescriptors);
    }

    public static MethodDescriptor create(
            String name,  Method method,Class<?> daoClass, ReturnDescriptor returnDescriptor,
            List<ParameterDescriptor> parameterDescriptors) {
        return new MethodDescriptor(name, method,daoClass, returnDescriptor, parameterDescriptors);
    }

    public Method getMethod(){return  method;}
    public String getName() {
        return name;
    }

    public Class<?> getDaoClass() {
        return daoClass;
    }



    public Type getReturnType() {
        return returnDescriptor.getType();
    }

    public Class<?> getReturnRawType() {
        return returnDescriptor.getRawType();
    }

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
        String sql="";
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
     return  "";
    }


    private final String defaultdb = "order_sz";

    public String getDataSourceFactoryName()  {
        var dbAnno = getAnnotation(db.class);
        if (dbAnno == null) {
            return  "order_sz";
        }
        return dbAnno.value();

    }

    @Nullable
    public DatabaseShardingStrategy getShardingAnno() {

        DatabaseShardingStrategy dbSharding=null;
        var sharding= daoClass.getAnnotation(Sharding.class);

        if(sharding != null){

            try {
               return dbSharding= sharding.databaseShardingStrategy().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

      //  return getAnnotation(Sharding.class);
        return  null;
    }

    public void  init(){
        if(parameterDescriptors.size()==1){

            var type0 = parameterDescriptors.get(0).getRawType();
            if (QueryBase.class.isAssignableFrom(type0)) {

                var fields = FieldUtils.getAllFields(type0);

                for (var field : fields) {

                    var cacheBy = field.getAnnotation(CacheBy.class);

                    if (cacheBy != null) {
                         cachePrefix=cacheBy.key();
                        cacheTTL= cacheBy.ttl();
                        if(StringUtils.isEmpty(cachePrefix)){
                            cachePrefix=type0.getName()+"_";
                        }
                        // builder.cachebyField4Query(field).cachePrefix(cachePrefix).cacheTTL(cacheBy.ttl());

                        var fieldName = field.getName();

                        if (fieldName.contains("_")) {
                            var arr = fieldName.split("_");

                            fieldName = arr[0];
                        }

                        var meta = MetaUtil.getQueryMeta(type0);

                        var modelClazz = meta.getModelClazz();

                        var field4Model = FieldUtils.getField(modelClazz, fieldName, true);

                        if (field4Model != null) {
                             //builder.cachebyField4Model(field4Model).IsCaching(true);
                            this.isUseCache=true;
                            this.modelCacheField=field4Model;
                            this.queryCacheField=field;
                            var keyType = TypeResolver.getActualType2(field4Model.getType());
                            //  builder.keyType(keyType);
                            this.keyType= keyType;
                            this.modelClazz= modelClazz;
                            // builder.valueType(modelClazz);
                            break;
                        }

                    }

                }


            }
        }
    }
    @Getter@Setter
    private boolean isUseCache;
    @Getter@Setter
    private Field queryCacheField;
    @Getter@Setter
    private Field modelCacheField;

    @Getter@Setter
   private  Class<?> keyType;
    @Getter@Setter
   private Class<?> modelClazz;

    public List<Annotation> getAnnotations() {
       var annotations = new ArrayList<Annotation>();

       var a= this.method.getAnnotations();
       var b= this.daoClass.getAnnotations();
        annotations.addAll(Arrays.asList(a));
        annotations.addAll(Arrays.asList(b));
        return   annotations;
    }
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    @Nullable
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return returnDescriptor.getAnnotation(annotationType);
    }
    public  CacheBy getCacheBy(){

   return getAnnotation(CacheBy.class);

    }
    @Getter@Setter
private String cachePrefix;
    @Getter@Setter
private int cacheTTL;
    public boolean isReturnGeneratedId() {
//        return isAnnotationPresent(ReturnGeneratedId.class) ||
//                (name != null && name.contains("ReturnGeneratedId"));

        return  false;
    }

}
