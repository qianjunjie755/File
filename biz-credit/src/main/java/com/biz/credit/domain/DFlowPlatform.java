package com.biz.credit.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DFlowPlatform {
    @ApiModelProperty(value="编号",position = 0,required = true,example = "1")
    private Integer id;
    @ApiModelProperty(value="平台名称",required = true,example = "e派克")
    private String platFormName;
    @ApiModelProperty(value="状态",required = true,example = "1",notes = "0禁用 1启用")
    private Integer status;
    @ApiModelProperty(value="创建日期",required = true,example = "2020-01-01 00:00:00")
    private String createTime;
    @ApiModelProperty(value="更新日期",required = true,example = "2020-01-01 00:00:00")
    private String updateTime;
}
