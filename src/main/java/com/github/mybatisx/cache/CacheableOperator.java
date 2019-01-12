package com.github.mybatisx.cache;

import com.github.mybatisx.descriptor.MethodDescriptor;
import com.github.mybatisx.util.SpringUtils;
import lombok.SneakyThrows;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.reflect.FieldUtils;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class CacheableOperator {


    private static final Map<String, Fire> contextMap = new HashMap<>();


    private MethodDescriptor MD;

    private MethodInvocation point;
    private Field hitField;
    private Object cacheKeyValue;
    private Object cacheArg;

    @SneakyThrows
    public CacheableOperator(MethodInvocation point, MethodDescriptor md, Field field) {

        this.point = point;
        this.MD = md;
        hitField = field;
        cacheArg = point.getArguments()[0];
        cacheKeyValue = FieldUtils.readField(field, cacheArg, true);

    }


    public Object invoke()  {

     return  hitField.getType().isArray()? multipleKeysCache(MD.getKeyType(), MD.getReturnDescriptor().getMappedClass())
             :singleKeyCache(MD.getReturnDescriptor().getMappedClass());
    }

    @SneakyThrows
     private <V> Object singleKeyCache(Class<V> redisValueType){

        var cachePrefix = MD.getCachePrefix();

        var key = String.format("%s%s",cachePrefix,cacheKeyValue);

        var v= CacheUtil.getCached(key,redisValueType);

        var addableObj = new AddableObject<V>(redisValueType);

        if(v!=null){
            addableObj.add(redisValueType.cast(v));
        }else{

            Object dbValues = this.point.proceed();

            var redis = SpringUtils.getBean(RedisClient.class);

            for (Object dbItem : new Iterables(dbValues)) {
                // db数据添加入结果


                addableObj.add(redisValueType.cast(dbItem));
                // 添加入缓存

                Object key2 = FieldUtils.readField(MD.getCacheField(), dbItem, true);

                var keyStr = cachePrefix + key2;
                redis.set(keyStr, dbItem, MD.getCacheTTL());



            }
        }

        return addableObj.getReturn();
     }

@SneakyThrows
    private <K, V> Object multipleKeysCache(Class<K> keyType, Class<V> redisValueType)  {

        var iterables = new Iterables(cacheKeyValue);
        var cachePrefix = MD.getCachePrefix();
        Set<String> keys = new HashSet<>();
        for (Object obj : iterables) {
            keys.add(cachePrefix + obj);
        }

        var redisResults = CacheUtil.getCached(keys, redisValueType);

        var addableObj = new AddableObject<V>(redisValueType);


        var hitKeys = new ArrayList<String>();
        var missKeys = new ArrayList<String>();

        // 筛选出缓存命中和丢失的数据
        var missKeysActual = new HashSet<K>();

        for (var key : iterables) {
            var key2 = cachePrefix + key;

            Object value = redisResults != null ? redisResults.get(key2) : null;
            if (value == null) {
                missKeysActual.add(keyType.cast(key));
                missKeys.add(String.valueOf(key));
            } else {

                addableObj.add(redisValueType.cast(value));

                hitKeys.add(String.valueOf(key));

            }
        }


        if (!missKeys.isEmpty()) { // 有缓存没命中的数据


            K[] keyArray = (K[]) Array.newInstance(keyType, missKeysActual.size());

            var v = missKeysActual.toArray(keyArray);

            FieldUtils.writeField(hitField, cacheArg, v, true);

            Object dbValues = this.point.proceed();
            var redis = SpringUtils.getBean(RedisClient.class);

            // 用于 debug log
            var needSetKeys = new ArrayList<String>();

            for (Object dbItem : new Iterables(dbValues)) {
                // db数据添加入结果


                addableObj.add(redisValueType.cast(dbItem));
                // 添加入缓存

                Object key = FieldUtils.readField(MD.getCacheField(), dbItem, true);

                var keyStr = cachePrefix + key;
                redis.set(keyStr, dbItem, MD.getCacheTTL());


                // missCacheByActualObjs.remove(cacheByActualObj);


                needSetKeys.add(keyStr);

            }


        }
        return addableObj.getReturn();
    }


    private class AddableObject<T> {

        List<T> hitValueList;
        Set<T> hitValueSet;
        Class<T> valueClass;


        public AddableObject(Class<T> valueClass) {

            var methodReturnType = MD.getReturnDescriptor().getRawType();

            if (methodReturnType.isAssignableFrom(Set.class)) {

                hitValueSet = new HashSet<T>();

            } else if (methodReturnType.isAssignableFrom(Arrays.class)) {

                hitValueList = new ArrayList<T>();

            } else { // Collection,List,LinkedList或数组或单个值都先使用LinkedList

                hitValueList = new LinkedList<T>();

            }
            this.valueClass = valueClass;
        }

        public void add(T v) {
            if (hitValueList != null) {
                hitValueList.add(v);
            } else {
                hitValueSet.add(v);
            }
        }

        public Object getReturn() {
            var methodReturnType = (Class<?>) MD.getReturnDescriptor().getRawType();
            if (methodReturnType.isAssignableFrom(List.class)
                    || methodReturnType.isAssignableFrom(Collection.class)) {

                return hitValueList;

            } else if (methodReturnType.isAssignableFrom(Set.class)) {

                return hitValueSet;

            } else if (methodReturnType.isArray()) {

                return toArray(hitValueList, valueClass);

            } else {
                return !hitValueList.isEmpty() ? hitValueList.get(0) : null;
            }
        }

        private <T> Object toArray(List<T> list, Class<T> clazz) {
            Object array = Array.newInstance(clazz, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            return array;
        }

        @Override
        public String toString() {
            return hitValueList != null ? hitValueList.toString() : hitValueSet.toString();
        }
    }

}
