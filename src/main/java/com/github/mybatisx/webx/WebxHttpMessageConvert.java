package com.github.mybatisx.webx;

import com.alibaba.fastjson.JSON;

import com.github.pagehelper.Page;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求时，通过{@link HttpMessageConverter}将流转换为对象<br/>
 * ，响应时通过其将对象转为流。
 * <p>
 * 注意：此转换器支持的格式是application/vson , VSON<br/>
 * <p>
 * 格式 name:vv;age:27;date:2017;
 *

 */
public class WebxHttpMessageConvert extends AbstractHttpMessageConverter<Object>
        implements GenericHttpMessageConverter<Object> {

    private final static Class<?> requestMapClazz = new HashMap<String, String>().getClass();

    private static final String ENTRY_SEPARATOR = ":";

    private static final String ENTRY_TERMINATOR = ";";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public WebxHttpMessageConvert() {
        // 此转换器支持的格式是application/vson
        super(new MediaType("application", "dubbo", DEFAULT_CHARSET));
    }

    @Override
    protected boolean supports(Class<?> clazz) {

        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {

        return canRead(mediaType);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {

        return canRead(mediaType);
    }


    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return readMap(inputMessage);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return readMap(inputMessage);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {

        return canWrite(mediaType);
    }

    /**
     * @see GenericHttpMessageConverter
     */


    /*
    这两个是新加的，之前的上面没有
     */
    //




    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {

        return true;
    }

    @Override
    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeMap(o, outputMessage);
    }


    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        writeMap(o, outputMessage);
    }


    private void writeMap(Object o, HttpOutputMessage outputMessage) throws IOException {


        var builder= ResponseData.builder().error(0).msg("").data(o);

        if(o instanceof Page){
            Long total= Long.valueOf(((Page) o).getTotal());
            Long  pageNum=Long.valueOf(((Page) o).getPages());
            builder.totalCount(total).pageCount(pageNum);

        }

        String v = JSON.toJSONString(builder.build());
        outputMessage.getBody().write(v.getBytes());


    }

    /**
     * @param inputMessage 输入的内容格式 name:vv;age:27;date:2017;
     * @return {@link RequestData}
     * @throws IOException                     e
     * @throws HttpMessageNotReadableException e
     */
    private Object readMap(HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // 从inputMessage中读取内容
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = inputMessage.getBody();
        byte[] b = new byte[1024];
        int length;
        while ((length = inputStream.read(b)) != -1) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(length);
            byteBuffer.put(b, 0, length);
            byteBuffer.flip();
            stringBuilder.append(DEFAULT_CHARSET.decode(byteBuffer).array());
        }

        // 将内容截开
//        String[] list = stringBuilder.toString().split(ENTRY_TERMINATOR);
//        Map<String, String> map = new HashMap<String, String>(list.length);
//        for (String element : list) {
//            String[] keyValue = element.split(ENTRY_SEPARATOR);
//            map.put(keyValue[0], keyValue[1]);
//
        Map<String, String> map = (Map<String, String>) JSON.parseObject(stringBuilder.toString(), requestMapClazz);
        RequestData requestData = new RequestData();
        requestData.setData(map);
        return requestData;
    }}

