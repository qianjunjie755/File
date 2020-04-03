package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DScoreLevel implements Serializable{
    private String apiCode;

    private Integer modelCode;

    private Integer modelType;

    private String scoreLevel;

    private String scoreRange;

    private String createTime;
}
