package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel
@Getter
@Setter
public class PortraitTaskQueryVO implements Serializable{
    @ApiModelProperty(value = "系统模块ID")
    private Integer moduleId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务状态：1-启用 0-停用")
    private Integer status;
}
