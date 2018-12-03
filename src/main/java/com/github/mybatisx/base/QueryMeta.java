package com.github.mybatisx.base;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class QueryMeta {

    @Getter@Setter
    private  Class<?> ModelClazz;
    @Getter@Setter
    private  Class<?> QueryClazz;

    private Map<Field,Filedx> Filed2Filedx;

    public QueryMeta(){

        Filed2Filedx= new HashMap<>();
    }

    public  Map<Field, Filedx> getFiled2Filedx() {
        return Filed2Filedx;
    }

    @Getter@Setter
    public static class Filedx{
        private String cloumnName;
        private  String dbCloumnName;
        private Class<?> thisType;

    }
}
