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
public class PortraitTypeReqVO implements Serializable{
    @ApiModelProperty(value = "分类组code", required = true)
    private String typeCode;

    @ApiModelProperty(value = "分类组名称", required = true)
    private String typeName;

    @ApiModelProperty(value = "系统模块ID", required = true)
    private Integer moduleId;

    @ApiModelProperty(value = "子分类列表", required = true)
    private List<PortraitTypeChildrenVO> children;
}
