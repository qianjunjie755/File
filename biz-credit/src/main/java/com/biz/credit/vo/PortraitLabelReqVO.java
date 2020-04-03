package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 画像标签新增/修改入参
 */
@ApiModel
@Getter
@Setter
public class PortraitLabelReqVO implements Serializable {
    //唯一ID，存在即修改，不存在新增
    @ApiModelProperty(value = "标签ID", required = true)
    private Integer labelId;

    //标签编码，必填
    @ApiModelProperty(value = "标签code", required = true)
    private String labelCode;

    //标签名称，必填
    @ApiModelProperty(value = "标签名称", required = true)
    private String labelName;

    //标签备注，非必填
    @ApiModelProperty(value = "标签备注")
    private String labelDesc;

    //分类ID，必填-子分类
    @ApiModelProperty(value = "二级分类ID", required = true)
    private Integer typeId;

    //对应表，非必填
    @ApiModelProperty(value = "对应表名")
    private String tableName;

    //计算逻辑，非必填
    @ApiModelProperty(value = "计算逻辑")
    private String calcLogic;
}
