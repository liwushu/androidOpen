package com.flying.test.bean;

public class HttpBean<T> {
    public int code;
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "HttpBean{" + "code=" + code + ", msg='" + msg + '\'' + ", data=" + data + '}';
    }
}
