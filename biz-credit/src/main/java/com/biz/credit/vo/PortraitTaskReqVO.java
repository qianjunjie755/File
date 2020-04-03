package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 新增画像任务入参
 */
@ApiModel
@Getter
@Setter
public class PortraitTaskReqVO implements Serializable{

    @ApiModelProperty(value = "任务名称", required = true)
    private String taskName;

    @ApiModelProperty(value = "任务类型：1-固定频率 2-有效期", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "系统模块ID", required = true)
    private Integer moduleId;

    @ApiModelProperty(value = "固定频率：taskType=1时必填-1D/1W/1M/1Y（每天/每周/每月/每年）")
    private String interval;

    @ApiModelProperty(value = "起始日期：taskType=2时必填-yyyy-MM-dd")
    private String startDate;

    @ApiModelProperty(value = "终止日期：taskType=2时必填-yyyy-MM-dd")
    private String endDate;

    @ApiModelProperty(value = "任务标签ID列表", required = true)
    private List<Integer> labelIds;

}