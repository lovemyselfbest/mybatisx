package com.github.mybatisx.webx.error;


import com.alibaba.fastjson.JSON;
import com.github.mybatisx.webx.ResponseData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        String mm = "";
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String ErrorHandler(HttpServletRequest req, Exception ex) {

        var map = new HashMap<String,Object>();
        map.put("Url:",req.getRequestURL().toString());
        map.put("QueryString:",req.getQueryString());
        map.put("Method:",req.getMethod());


            var  jsonBody = req.getAttribute("JSON_REQUEST_BODY");
            if(jsonBody!=null){
                map.put("Body:",jsonBody);
            }



        var builder = ResponseData.builder().error(503).msg(ex.getMessage()).data(map);


        var res = builder.build();

        String v = JSON.toJSONString(res);
         return v;
    }
}
