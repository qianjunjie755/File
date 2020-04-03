package com.biz.chart.utils;


import java.io.Serializable;

public class RespEntity implements Serializable {
    private String code;
    private String message;
    private Object data;

    public RespEntity(RespCode respCode) {
        this.code = respCode.getCode();
        this.message = respCode.getMessage();
    }

    public RespEntity(RespCode respCode, Object result) {
        this(respCode);
        this.data = result;
    }
    public void changeRespEntity(RespCode respCode, Object result){
        this.code=respCode.getCode();
        this.message = respCode.getMessage();
        this.data = result;
    }

    public static RespEntity checkFail(){
        return new RespEntity(RespCode.INVALID);
    }
    public static RespEntity checkFail(String message){
        return checkFail().setMessage(message);
    }

    public RespEntity() {
        super();
    }

    public String getCode() {
        return code;
    }

    public RespEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RespEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public RespEntity setData(Object data) {
        this.data = data;
        return this;
    }

    public static RespEntity success() {
        return new RespEntity(RespCode.SUCCESS);
    }

    public static RespEntity error() {
        return new RespEntity(RespCode.ERROR);
    }

    public boolean ok() {
        return RespCode.SUCCESS.getCode().equals(this.getCode());
    }
}
