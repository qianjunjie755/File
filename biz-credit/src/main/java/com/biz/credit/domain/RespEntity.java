package com.biz.credit.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@ApiModel("返回对象")
@Data
@NoArgsConstructor
public class RespEntity<T> implements Serializable {
    @ApiModelProperty(value="返回状态码",required = true)
    private String code;
    @ApiModelProperty(value="返回信息",required = true)
    private String msg;
    @ApiModelProperty(value="返回体",required = true)
    private T data;

    public RespEntity(RespCode respCode) {
        this.code = respCode.getCode();
        this.msg = respCode.getMsg();
    }

    public RespEntity(RespCode respCode, T data) {
        this(respCode);
        this.data = data;
    }
    public void changeRespEntity(RespCode respCode, T data){
        this.code=respCode.getCode();
        this.msg = respCode.getMsg();
        this.data = data;
    }

    public RespEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public RespEntity setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public RespEntity setData(T data) {
        this.data = data;
        return this;
    }

    public static RespEntity success(){
        return new RespEntity(RespCode.SUCCESS);
    }

    public static RespEntity error(){
        return new RespEntity(RespCode.WARN);
    }

    @JsonIgnore
    @JSONField(serialize=false)
    public boolean isSuccess(){
        return RespCode.SUCCESS.getCode().equals(this.getCode());
    }
}
