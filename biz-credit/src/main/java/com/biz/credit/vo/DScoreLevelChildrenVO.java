package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel
@Getter
@Setter
public class DScoreLevelChildrenVO implements Serializable {
    @ApiModelProperty(name = "评分等级", required = true)
    private String scoreLevel;

    @ApiModelProperty(name = "评分范围", required = true)
    private String scoreRange;
}
