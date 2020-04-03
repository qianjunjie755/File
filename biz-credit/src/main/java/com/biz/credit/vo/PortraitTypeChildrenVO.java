package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel
@Getter
@Setter
public class PortraitTypeChildrenVO implements Serializable {
    @ApiModelProperty(value = "子分类code", required = true)
    private String typeCode;

    @ApiModelProperty(value = "子分类名称", required = true)
    private String typeName;
}
