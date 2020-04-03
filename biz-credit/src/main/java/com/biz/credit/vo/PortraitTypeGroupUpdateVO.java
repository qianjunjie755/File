package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 企业画像分类组查询参数
 */
@ApiModel
@Getter
@Setter
public class PortraitTypeGroupUpdateVO implements Serializable{

    //更换的分类组ID
    @ApiModelProperty(value = "分类组ID", required = true)
    private Integer typeId;

    @ApiModelProperty(value = "模块ID", required = true)
    private Integer moduleId;

    //需要更换分组的子分类ID列表
    @ApiModelProperty(value = "子分类ID列表", required = true)
    private List<Integer> children;
}
