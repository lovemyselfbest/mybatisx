package com.github.mybatisx.webx;

import lombok.*;


@Builder
@AllArgsConstructor

public class ResponseData {

   private int error;
   private String msg;

   private Object data;

   private  Long totalCount;

   private  Long pageCount;
}
