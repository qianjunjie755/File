package com.biz.credit.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DFlowLink {
    @ApiModelProperty(value="编号",position = 0,required = true,example = "1")
    private Integer id;
    @ApiModelProperty(value="业务环节名称",required = true,example = "通用")
    private String LinkName;
    @ApiModelProperty(value="业务编号",required = true,example = "12")
    private Integer bizId;
    @ApiModelProperty(value="状态",required = true,example = "1",notes = "0禁用 1启用")
    private Integer status;
    @ApiModelProperty(value="创建日期",required = true,example = "2020-01-01 00:00:00")
    private String createTime;
    @ApiModelProperty(value="更新日期",required = true,example = "2020-01-01 00:00:00")
    private String updateTime;
}
