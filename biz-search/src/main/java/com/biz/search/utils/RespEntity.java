package com.biz.search.utils;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel("返回对象")
@Getter
@NoArgsConstructor
public class RespEntity<T> implements Serializable {
    @ApiModelProperty(value = "返回状态码", required = true)
    private String code;
    @ApiModelProperty(value = "返回状态描述", required = true)
    private String msg;
    @ApiModelProperty(value = "返回数据内容")
    private T data;

    public RespEntity(RespCode respCode) {
        this.code = respCode.getCode();
        this.msg = respCode.getMsg();
    }

    public RespEntity(RespCode respCode, T data) {
        this(respCode);
        this.data = data;
    }

    public void changeRespEntity(RespCode respCode, T data) {
        this.code = respCode.getCode();
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

    public static RespEntity success() {
        return new RespEntity(RespCode.SUCCESS);
    }

    public static RespEntity noResult() {
        return new RespEntity(RespCode.NO_RESULT);
    }

    public static RespEntity error() {
        return new RespEntity(RespCode.WARN);
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess() {
        return RespCode.SUCCESS.getCode().equals(this.getCode());
    }
}
