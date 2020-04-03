package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业信用评分结果
 */
@Getter
@Setter
public class CompanyCredit implements Serializable{
    private String inputId;

    private Integer modelCode;

    private Integer modelType;

    private String creditLevel;

    private String creditValue;

    private String createT;

    private String updateT;
}
