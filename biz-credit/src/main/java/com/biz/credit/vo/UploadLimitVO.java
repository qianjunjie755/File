package com.biz.credit.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadLimitVO implements Serializable {
    private Integer interval;
    private Integer limitCount;
    private Integer userInterval;
    private Integer groupInterval;
    private Integer userLimitCount;
    private Integer limitType;
    private Integer day;
    private Integer month;
}
