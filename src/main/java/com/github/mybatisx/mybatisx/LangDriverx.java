package com.github.mybatisx.mybatisx;

import com.github.mybatisx.annotation.Column;
import com.github.mybatisx.annotation.ID;
import com.github.mybatisx.annotation.Ignore;

import com.github.mybatisx.annotation.Table;
import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.cache.FireFactory;
import com.github.mybatisx.descriptor.MethodUtil;
import com.github.mybatisx.util.MetaUtil;
import com.github.mybatisx.util.SQL;
import com.google.common.base.CaseFormat;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import javax.sound.midi.Track;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Allen
 * @date 2018/3/9
 * <p>
 * 自定义Update注解,用于动态生成Update语句
 */
public class LangDriverx extends XMLLanguageDriver implements LanguageDriver {

    /**
     * Pattern静态申明
     */
    private final Pattern inPattern = Pattern.compile("#\\{(\\w+)\\}");
    //private final Pattern inPattern = Pattern.compile("\\(#\\{(\\w+)\\}\\)");


    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    private void checkAutoSqlGenerator(Class<?> modelClazz) {

        if (modelClazz == MapperMethod.ParamMap.class)
            throw new IllegalArgumentException("参数不对");

        var fields = MetaUtil.getAllFields2(modelClazz);

        if(QueryBase.class.isAssignableFrom(modelClazz))
            return;


        Field field_ID = null;
        for (Field field : fields) {
            var id = field.getAnnotation(ID.class);
            if (id != null) {
                field_ID = field;
                break;
            }
        }
        if (field_ID == null)
            throw new IllegalArgumentException(String.format("table %s have not primary key", modelClazz.getName()));


    }



    /**
     * 实现自定义Update注解
     *
     * @param configuration 配置参数
     * @param script        入参
     * @param parameterType 参数类型
     * @return 转换后的SqlSource
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {


        Class<?> mapperClass = MybatisxMapperRegistry.getCurrentMapper();


        Method method = MybatisxMapperAnnotationBuilder.getCurrentMethod();


        var MD = FireFactory.getFactory().setMD(method, mapperClass);


        switch (script) {
            case SQL.Update:
                parameterType = MD.getParameterDescriptors().get(0).getRawType();
                checkAutoSqlGenerator(parameterType);

                var table = parameterType.getAnnotation(Table.class);
                if (table == null)
                    throw new IllegalArgumentException("参数不对");

                var fields = MetaUtil.getAllFields2(parameterType);

                var sb = new StringBuilder();
                sb.append(" update ");
                sb.append(table.value());
                sb.append(" ");
                sb.append("<set>");

                Field pk = null;
                String pkName = "";
                String pkFieldName = "";

                for (Field field : fields) {

                    var id = field.getAnnotation(ID.class);

                    if (id != null) {
                        pk = field;
                        pkFieldName = field.getName();
                        pkName = pkFieldName;
                        var column = field.getAnnotation(Column.class);
                        if (column != null) {
                            pkName = column.value();
                        }
                    }

                }
                if (pk == null)
                    throw new IllegalArgumentException("参数不对3");


                for (Field field : fields) {
                    // 排除被Ignore修饰的变量
                    if (!field.isAnnotationPresent(Ignore.class)) {

                        if (field == pk)
                            continue;

                        var columnName = field.getName();
                        var dbColumnName = columnName;
                        var columnAnna = field.getAnnotation(Column.class);

                        if (columnAnna != null)
                            dbColumnName = columnAnna.value();


                        String tmp = "<if test=\"_field != null\">_column=#{_field},</if>";
                        sb.append(tmp.replaceAll("_field", columnName).replaceAll("_column",
                                CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, dbColumnName)));
                    }
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sb.append("</set>");
                sb.append(String.format(" where %s=#{%s} ", pkName, pkFieldName));
                script = "<script>" + sb.toString() + "</script>";
                break;
            case SQL.Insert:
                parameterType = MD.getParameterDescriptors().get(0).getRawType();
                checkAutoSqlGenerator(parameterType);

                table = parameterType.getAnnotation(Table.class);
                if (table == null)
                    throw new IllegalArgumentException("参数不对");

                fields = MetaUtil.getAllFields2(parameterType);

                sb = new StringBuilder();
                var tmp1 = new StringBuilder();
                var tmp2 = new StringBuilder();
                sb.append(" insert into  ");
                sb.append(table.value());

                //

                ID idAnno=null;
                String idName="";
                for (Field field : fields) {
                    // 排除被Ignore修饰的变量
                    if (!field.isAnnotationPresent(Ignore.class)) {

                       var idAnno2 = field.getAnnotation(ID.class);

                        if(idAnno2 !=null){
                            idAnno=idAnno2;
                            idName= field.getName();
                            var columnID = field.getAnnotation(Column.class);
                            if (columnID != null){
                                idName= columnID.value();
                            }
                            if(idAnno.autoGenerateId()==true)
                                continue;
                        }




                        var columnName = field.getName();
                        var dbColumnName = columnName;
                        var columnAnna = field.getAnnotation(Column.class);

                        if (columnAnna != null)
                            dbColumnName = columnAnna.value();

                        tmp1.append(dbColumnName + ",");
                        tmp2.append("#{" + columnName + "},");

                    }
                }
                tmp1.deleteCharAt(tmp1.lastIndexOf(","));
                tmp2.deleteCharAt(tmp2.lastIndexOf(","));
                sb.append(String.format("(%s) values (%s)", tmp1, tmp2));

                if(idAnno.autoGenerateId()==true){
                   // sb.append(String.format("; select 1 as id",idName));
                }else{
                  //  sb.append(String.format("; select LAST_INSERT_ID() as `%s`",idName));
                }
                script = "<script>" + sb.toString() + "</script>";

                break;
            case SQL.Select:
                parameterType = MD.getParameterDescriptors().get(0).getRawType();
                checkAutoSqlGenerator(parameterType);

                sb = new StringBuilder();

                var queryMeta = MetaUtil.getQueryMeta(parameterType);

                var modelClazz = queryMeta.getModelClazz();

                table = modelClazz.getAnnotation(Table.class);
                if (table == null)
                    throw new IllegalArgumentException("参数不对");

                fields = MetaUtil.getAllFields2(modelClazz);
                sb.append(" select ");
                for (var field : fields) {
                    if (!field.isAnnotationPresent(Ignore.class)) {


                        var columnName = field.getName();
                        var dbColumnName = columnName;
                        var columnAnna = field.getAnnotation(Column.class);

                        if (columnAnna != null)
                            dbColumnName = columnAnna.value();

                        sb.append(String.format(" `%s` as %s,", dbColumnName, columnName));

                    }

                }
                sb.deleteCharAt(sb.lastIndexOf(","));

                sb.append(String.format(" from `%s` where 1=1  ", table.value()));

                var filedxMap = queryMeta.getFiled2Filedx();
                var keys = filedxMap.keySet();

                for (var key : keys) {

                    var filedX = filedxMap.get(key);

                    var columnName = filedX.getCloumnName();
                    var dbColumnName = filedX.getDbCloumnName();

                    if (columnName.contains("_")) {
                        var arr = columnName.split("_");
                        var operation = arr[1];
                        switch (operation) {
                            case "eq":
                                sb.append(String.format("\n<if test=\"%s != null\">" +
                                        " \n <![CDATA[  AND %s = #{%s} ]]>\n" +
                                        "  \n</if>", columnName, dbColumnName, columnName));
                                break;
                            case "in":
                                sb.append(String.format("\n <if test=\"%s != null\">" +
                                        " \n  and %s in " +
                                        " \n  <foreach item=\"item\" index=\"index\" collection=\"%s\"" +
                                        " \n    open=\"(\" separator=\",\" close=\")\">" +
                                        " \n    #{item}" +
                                        " \n  </foreach>" +
                                        " \n</if>", columnName, dbColumnName, columnName, columnName));
                                break;

                            case "lt":
                                sb.append(String.format("\n<if test=\"%s != null\"> <![CDATA[ AND %s < #{%s} ]]> </if>", columnName, arr[0], columnName));
                                break;
                        }
                    }


                }


                script = "<script>" + sb.toString() + "</script>";
                break;
        }


        Pattern p = Pattern.compile(":\\d+(\\.\\w+)?");

        Matcher m = p.matcher(script);

        var maps = new HashMap<Integer, String>();
        int i = 1;
        while (m.find()) {
            maps.putIfAbsent(i, m.group());
            i++;
        }

        var IsParMap = parameterType == MapperMethod.ParamMap.class;


        for (Map.Entry<Integer, String> entry : maps.entrySet()) {

            var k = entry.getKey();
            var v = entry.getValue();
            var vHou = "";
            var vQian = "";
            if (v.contains(".")) {
                var vArray = v.split("\\.");

                if (IsParMap) {
                    vHou = "." + vArray[1];
                } else {
                    vHou = vArray[1];
                }
            }
            if (IsParMap)
                vQian = "param";

            var num = k.toString();
            if (IsParMap == false) {
                num = "";
            }

            var newV = String.format("#{%s%s%s}", vQian, num, vHou);
            script = script.replace(v, newV);

        }

        return super.createSqlSource(configuration, script, parameterType);
    }
}
