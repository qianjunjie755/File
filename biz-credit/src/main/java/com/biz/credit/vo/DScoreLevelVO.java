package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Getter
@Setter
public class DScoreLevelVO implements Serializable{

    @ApiModelProperty(value = "客户代码", required = true)
    private String apiCode;

    @ApiModelProperty(value = "模型code", required = true)
    private Integer modelCode;

    @ApiModelProperty(value = "模型类型：3-反欺诈模型 4-信用评分模型 5-评分卡", required = true)
    private Integer modelType;

    @ApiModelProperty(value = "评分等级列表", required = true)
    private List<DScoreLevelChildrenVO> modelLevels;
}
