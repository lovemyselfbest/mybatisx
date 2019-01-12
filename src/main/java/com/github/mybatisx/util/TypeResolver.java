package com.github.mybatisx.util;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public class TypeResolver {


    public static Type getActualType(Type type) {

        //获取返回值的泛型参数
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            type = actualTypeArguments[0];
        }

        if (type.getClass().isArray()) {
            type = type.getClass().getComponentType();
        }

        return type;
    }

    public static Class<?> getActualType2(Class<?> clazz) {


        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }

        return clazz;
    }

    public static boolean isJavaType(Class<?> clazz) {

        if (clazz.isPrimitive())
            return true;

        if(Serializable.class.isAssignableFrom(clazz))
            return false;

        try {
            boolean b = ((Class) clazz.getField("TYPE").get(null)).isPrimitive();
            if (b == true)
                return b;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static Object getDefaultValue(Class<?> clazz) {

        Object ret = null;

        if (clazz == int.class) {
            ret = 1;
        }
        if (clazz == long.class) {
            ret = 1;
        }

        if (clazz == String.class) {
            ret = "\"String\"";
        } else if (clazz == BigInteger.class) {
            ret = 1;
        } else if (clazz == Long.class) {
            ret = 1L;
        } else if (clazz == Integer.class) {
            ret = 1;
        } else if (clazz == Short.class) {
            ret = 1;
        } else if (clazz == Byte.class) {
            ret = 1;
        } else if (clazz == BigDecimal.class) {
            ret = 1L;
        } else if (clazz == Double.class) {
            ret = 1;
        } else if (clazz == Float.class) {
            ret = 1;
        } else if (clazz == Boolean.class) {
            ret = false;
        }


        return ret;

    }

    public static Object castValueType(Class<?> clazz,String value) {


        if (clazz == String.class) {
            return  value;
        } else if (clazz == int.class) {
            return Integer.valueOf(value).intValue();

        } else if (clazz == double.class) {
            return Double.valueOf(value).doubleValue();
        } else if (clazz == BigInteger.class) {
            return  "";
        } else if (clazz == float.class) {
            return Float.valueOf(value).floatValue();
        }else if (clazz == long.class) {
            return  Long.parseLong(value);
        }else if (clazz == Long.class) {
            return  Long.valueOf(value);
        } else if (clazz == Integer.class) {
            return  Integer.valueOf(value);
        } else if (clazz == Short.class) {
            return  Short.valueOf(value);
        } else if (clazz == Byte.class) {
            return  Byte.valueOf(value);
        } else if (clazz == BigDecimal.class) {
            return  BigDecimal.valueOf(Long.valueOf(value));
        } else if (clazz == Double.class) {
            return  Double.valueOf(value);
        } else if (clazz == Float.class) {
            return  Float.valueOf(value);
        } else if (clazz == Boolean.class) {
            return  Boolean.valueOf(value);
        }
        return "";

    }
}
