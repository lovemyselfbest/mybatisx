package com.github.mybatisx.mybatisx;

import com.github.mybatisx.base.QueryBase;
import com.github.pagehelper.PageHelper;
import org.springframework.util.ObjectUtils;

public class PageUtil {


    public static  void setPageArgs(QueryBase query){

        if(!ObjectUtils.isEmpty(query.getSkip()) && !ObjectUtils.isEmpty(query.getTake())){

            PageHelper.startPage(query.getSkip()/query.getTake(), query.getTake());
        }
        if(ObjectUtils.isEmpty(query.getSkip()) && !ObjectUtils.isEmpty(query.getTake())){
            PageHelper.startPage(1, query.getTake());
        }

        if(ObjectUtils.isEmpty(query.getSkip()) && !ObjectUtils.isEmpty(query.getTake())){
            PageHelper.startPage(1, query.getSkip());
        }
    }

    public static Boolean IsPageing(QueryBase query) {

        if(!ObjectUtils.isEmpty(query.getSkip())){

            return true;
        }


        return false;
    }
}
