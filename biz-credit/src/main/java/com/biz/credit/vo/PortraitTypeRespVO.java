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
public class PortraitTypeRespVO implements Serializable{
    @ApiModelProperty(value = "分类ID")
    private Integer typeId;

    @ApiModelProperty(value = "分类code")
    private String typeCode;

    @ApiModelProperty(value = "分类名称")
    private String typeName;

    @ApiModelProperty(value = "子分类列表")
    private List<PortraitTypeRespVO> children;
}
