package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DNodeThreshold implements Serializable{
    private Integer modelCode;
    private Long modelId;
    private Integer type;
    private String judge;
    private String createTime;
}
