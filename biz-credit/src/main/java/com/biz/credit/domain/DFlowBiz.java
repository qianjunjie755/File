package com.biz.credit.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DFlowBiz {
    @ApiModelProperty(value="编号",position = 0,required = true,example = "1")
    private Integer id;
    @ApiModelProperty(value="业务名称",required = true,example = "通用")
    private String bizName;
    @ApiModelProperty(value="平台编号",required = true,example = "12")
    private Integer platformId;
    @ApiModelProperty(value="状态",required = true,example = "1",notes = "0禁用 1启用")
    private Integer status;
    @ApiModelProperty(value="创建日期",required = true,example = "2020-01-01 00:00:00")
    private String createTime;
    @ApiModelProperty(value="更新日期",required = true,example = "2020-01-01 00:00:00")
    private String updateTime;
}
