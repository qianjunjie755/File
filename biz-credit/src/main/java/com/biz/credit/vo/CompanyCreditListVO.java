package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel
@Getter
@Setter
public class CompanyCreditListVO implements Serializable {
    @ApiModelProperty(value = "企业名称")
    private String companyName;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "模型code")
    private Integer modelCode;

    @ApiModelProperty(value = "模型类型：3-反欺诈模型 4-信用评分模型 5-评分卡")
    private String modelType;

    @ApiModelProperty(value = "评分等级")
    private String creditLevel;

    @ApiModelProperty(value = "评分结果")
    private String creditValue;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
