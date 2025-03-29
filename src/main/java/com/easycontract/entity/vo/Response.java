package com.easycontract.entity.vo;

import com.easycontract.entity.constants.ResponseCode;

public class Response<T> {

    private int code;

    private String msg;

    private T data;

    public Response() {}

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseCode.OK, ResponseCode.SUCCESS, data);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(ResponseCode.OK, message, data);
    }

    public static <T> Response<T> success(String message) {
        return new Response<>(ResponseCode.OK, message, null);
    }

    public static <T> Response<T> success() {
        return new Response<>(ResponseCode.OK, ResponseCode.SUCCESS, null);
    }

    public static <T> Response<T> fail(String message) {
        return new Response<>(ResponseCode.INTERNAL_SERVER_ERROR, message, null);
    }

    public static <T> Response<T> fail() {
        return new Response<>(ResponseCode.INTERNAL_SERVER_ERROR, ResponseCode.FAIL, null);
    }
}
