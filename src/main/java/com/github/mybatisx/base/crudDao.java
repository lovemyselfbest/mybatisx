package com.github.mybatisx.base;

import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.annotation.WebxRequestMapping;
import com.github.mybatisx.base.ModelBase;
import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.descriptor.Generic;
import com.github.mybatisx.util.SQL;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface crudDao<T extends ModelBase, TQ> extends Generic<T, TQ> {

    @WebxRequestMapping
    @Select(SQL.Select)

    List<T> select(TQ query);

    @WebxRequestMapping
    @Insert(SQL.Insert)
    @SelectKey(statement = SQL.SelectKey, keyColumn = "",keyProperty = "", before = false, resultType =Long.class )
    public  Long insert(T model);

    @WebxRequestMapping
    @Update(SQL.Update)
    public  int update(T model);

}