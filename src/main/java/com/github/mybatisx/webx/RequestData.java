package com.github.mybatisx.webx;

import java.util.Map;

public class RequestData {

    private Map<String, String> data;

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String toString() {
        return "{\"data\":" + data + "}";
    }
}
