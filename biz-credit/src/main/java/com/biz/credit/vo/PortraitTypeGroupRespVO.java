package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业画像分类组查询参数
 */
@ApiModel
@Getter
@Setter
public class PortraitTypeGroupRespVO implements Serializable{
    @ApiModelProperty(value = "分类组ID")
    private Integer typeId;

    @ApiModelProperty(value = "分类组code")
    private String typeCode;

    @ApiModelProperty(value = "分类组名称")
    private String typeName;

    @ApiModelProperty(value = "系统模块ID")
    private Integer moduleId;

    @ApiModelProperty(value = "系统模块名称")
    private String moduleName;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "分类组状态：1-启用 0-停用")
    private Integer status;
}
