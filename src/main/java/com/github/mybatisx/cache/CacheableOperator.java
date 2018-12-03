package com.github.mybatisx.cache;

import com.github.mybatisx.descriptor.MethodDescriptor;
import com.github.mybatisx.util.SpringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Array;
import java.util.*;

public class CacheableOperator {


    private static final Map<String, Fire> contextMap = new HashMap<>();


    private MethodDescriptor MD;

    private ProceedingJoinPoint point;

    public CacheableOperator(ProceedingJoinPoint point, MethodDescriptor md) throws Throwable {

        this.point = point;
        this.MD = md;
        this.args = point.getArgs();
        cacheArg = args[0];
        cacheActualKeys = FieldUtils.readField(md.getQueryCacheField(),cacheArg, true);

    }

    private Object[] args;
    private Object cacheArg;

    private Object cacheActualKeys;


    public Object invoke() throws Throwable {


        return multipleKeysCache(MD.getKeyType(), MD.getReturnRawType());
    }

    private Map<String, Object> getCached(Set<String> keys, Class<?> clazz) {
        var ret = new HashMap<String, Object>();
        var redis = getRedisClient();
        for (String key : keys) {
            var v = redis.get(key, clazz);
            if (v != null) {
                ret.putIfAbsent(key, v);
            }
        }

        return ret;
    }

    private RedisClient getRedisClient() {

        var redis = SpringUtils.getBean(RedisClient.class);
        return redis;
    }

    private <K, V> Object multipleKeysCache(Class<K> keyType, Class<V> redisValueType) throws Throwable {

        var iterables = new Iterables(cacheActualKeys);
        var cachePrefix= MD.getCachePrefix();
        Set<String> keys = new HashSet<>();
        for (Object obj : iterables) {
            keys.add(cachePrefix+obj);
        }

        var redisResults = getCached(keys, redisValueType);

        var addableObj = new AddableObject<V>(redisValueType);


        var hitKeys = new ArrayList<String>();
        var missKeys = new ArrayList<String>();

        // 筛选出缓存命中和丢失的数据
        var missKeysActual = new HashSet<K>();

        for (var key : iterables) {
var key2=cachePrefix+key;

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

            var v=missKeysActual.toArray(keyArray);

            FieldUtils.writeField(MD.getQueryCacheField(),cacheArg,v,true);

            Object dbValues = this.point.proceed(args);
            var redis = getRedisClient();

            // 用于 debug log
            var needSetKeys = new ArrayList<String>();

            for (Object dbItem : new Iterables(dbValues)) {
                // db数据添加入结果


                addableObj.add(redisValueType.cast(dbItem));
                // 添加入缓存

                Object key = FieldUtils.readField(MD.getQueryCacheField(),dbItem,true);

                var keyStr=cachePrefix+key;
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

            var methodReturnType = MD.getReturnRawType();

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
            var methodReturnType =(Class<?>) MD.getReturnType();
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

        private  <T> Object toArray(List<T> list, Class<T> clazz) {
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
