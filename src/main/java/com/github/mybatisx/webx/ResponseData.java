package com.github.mybatisx.webx;

import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseData<T> {

   private int error;
   private Object msg;

   private T data;

   private  Long totalCount;

   private  Long pageCount;
}
