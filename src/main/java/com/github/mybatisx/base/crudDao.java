package com.github.mybatisx.base;

import com.github.mybatisx.annotation.CacheBy;
import com.github.mybatisx.annotation.WebxRequestMapping;
import com.github.mybatisx.base.ModelBase;
import com.github.mybatisx.base.QueryBase;
import com.github.mybatisx.descriptor.Generic;
import com.github.mybatisx.util.SQL;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface crudDao<T extends ModelBase, TQ> extends Generic<T, TQ> {

    @WebxRequestMapping
    @Insert(SQL.Insert)
    @SelectKey(statement = SQL.SelectKey, keyColumn = "",keyProperty = "", before = false, resultType =Long.class )
    public  Long insert(T model);

    @WebxRequestMapping
    @Update(SQL.Update)
    public  int update(T model);

    @WebxRequestMapping
    @Select(SQL.Select)
    List<T> select(TQ query);

    @WebxRequestMapping
    @Delete(SQL.Delete)
    public int delete(T model);

}
