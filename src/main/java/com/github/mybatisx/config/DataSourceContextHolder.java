package com.github.mybatisx.config;

public class DataSourceContextHolder {


    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    // 设置数据源名
    public static void setDBKey(String dbType) {

        contextHolder.set(dbType);
    }

    // 获取数据源名
    public static String getDBKey() {
        return (contextHolder.get());
    }

    // 清除数据源名
    public static void clearDBKey() {
        contextHolder.remove();
    }
}
