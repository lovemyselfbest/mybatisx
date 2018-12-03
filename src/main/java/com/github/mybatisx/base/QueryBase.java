package com.github.mybatisx.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter@Setter
public abstract class QueryBase<T extends ModelBase> implements Serializable {
    private Integer Take;
    private Integer Skip;
    private Integer Count;
    private String OrderField;
    private String OrderDirection;
}
