package com.github.mybatisx.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TypeUtil {

    /**
     * 基本类型解析
     */
    public static Object parsePrimitive(String parameterTypeName, Object value) {
        final String booleanTypeName = "boolean";
        if (booleanTypeName.equals(parameterTypeName)) {
            return Boolean.valueOf(value.toString());
        }
        final String intTypeName = "int";
        if (intTypeName.equals(parameterTypeName)) {
            return Integer.valueOf(value.toString());
        }
        final String charTypeName = "char";
        if (charTypeName.equals(parameterTypeName)) {
            return value.toString().charAt(0);
        }
        final String shortTypeName = "short";
        if (shortTypeName.equals(parameterTypeName)) {
            return Short.valueOf(value.toString());
        }
        final String longTypeName = "long";
        if (longTypeName.equals(parameterTypeName)) {
            return Long.valueOf(value.toString());
        }
        final String floatTypeName = "float";
        if (floatTypeName.equals(parameterTypeName)) {
            return Float.valueOf(value.toString());
        }
        final String doubleTypeName = "double";
        if (doubleTypeName.equals(parameterTypeName)) {
            return Double.valueOf(value.toString());
        }
        final String byteTypeName = "byte";
        if (byteTypeName.equals(parameterTypeName)) {
            return Byte.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 基本类型包装类解析
     */
    public static  Object parseBasicTypeWrapper(Class<?> parameterType, Object value) {
        if (Number.class.isAssignableFrom(parameterType)) {

            //  Number number = (Number) value;
            if (parameterType == Integer.class) {
                return NumberUtils.toInt(value.toString());//number.intValue();
            } else if (parameterType == Short.class) {
                return NumberUtils.toShort(value.toString());//  return number.shortValue();
            } else if (parameterType == Long.class) {
                return NumberUtils.toLong(value.toString());// return number.longValue();
            } else if (parameterType == Float.class) {
                return NumberUtils.toFloat(value.toString());//return number.floatValue();
            } else if (parameterType == Double.class) {
                return NumberUtils.toDouble(value.toString());// return number.doubleValue();
            } else if (parameterType == Byte.class) {
                return NumberUtils.toByte(value.toString());//return number.byteValue();
            }
        } else if (parameterType == Boolean.class) {
            return value.toString();
        } else if (parameterType == Character.class) {
            return value.toString().charAt(0);
        }
        return null;
    }

    /**
     * 判断是否为基本数据类型包装类
     */
    public static  boolean isBasicDataTypes(Class clazz) {
        Set<Class> classSet = new HashSet<>();
        classSet.add(Integer.class);
        classSet.add(Long.class);
        classSet.add(Short.class);
        classSet.add(Float.class);
        classSet.add(Double.class);
        classSet.add(Boolean.class);
        classSet.add(Byte.class);
        classSet.add(Character.class);
        return classSet.contains(clazz);
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
}
