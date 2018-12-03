package com.github.mybatisx.webx;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

public class WebxMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private HttpMessageConverter messageConverter;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        //return returnType.getMethodAnnotation(ResponseJson.class) != null;
        return true;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);

        messageConverter.write(returnValue, new MediaType(MediaType.APPLICATION_JSON, Collections.singletonMap("charset","UTF-8")), outputMessage);
    }

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }
}
