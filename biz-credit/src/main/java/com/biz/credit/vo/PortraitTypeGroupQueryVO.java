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
public class PortraitTypeGroupQueryVO implements Serializable{
    @ApiModelProperty(value = "分类组code")
    private String typeCode;

    @ApiModelProperty(value = "分类组名称")
    private String typeName;

    @ApiModelProperty(value = "系统模块ID")
    private Integer moduleId;

    //根据更新日期查询：yyyy-mm-dd
    @ApiModelProperty(value = "根据更新日期查询：yyyy-mm-dd")
    private String updateTime;

    @ApiModelProperty(value = "分类组状态：1-启用 0-停用")
    private Integer status;
}
