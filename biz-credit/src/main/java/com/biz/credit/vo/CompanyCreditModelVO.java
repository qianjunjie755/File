package com.biz.credit.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 企业评分结果中模型code与模型名称bean
 */
@Getter
@Setter
public class CompanyCreditModelVO {

    private Integer modelCode;

    private Integer modelType;

    private String modelName;
}
