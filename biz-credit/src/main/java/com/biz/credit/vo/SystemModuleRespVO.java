package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 系统模块列表返参
 */
@ApiModel
@Getter
@Setter
public class SystemModuleRespVO implements Serializable{
    @ApiModelProperty(value = "模块ID")
    private Integer moduleId;

    @ApiModelProperty(value = "模块名称")
    private String moduleName;
}
