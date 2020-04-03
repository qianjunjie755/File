package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 画像标签查询返参
 */
@ApiModel
@Getter
@Setter
public class PortraitLabelRespVO implements Serializable {
    @ApiModelProperty(value = "标签ID")
    private Integer labelId;

    @ApiModelProperty(value = "标签code")
    private String labelCode;

    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @ApiModelProperty(value = "标签备注")
    private String labelDesc;

    @ApiModelProperty(value = "分类组名称")
    private String typeName;

    @ApiModelProperty(value = "二级分类名称")
    private String subTypeName;

    @ApiModelProperty(value = "标签状态")
    private Integer status;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;
}
