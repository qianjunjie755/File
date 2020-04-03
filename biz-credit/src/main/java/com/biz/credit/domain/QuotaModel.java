package com.biz.credit.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("额度模型对象")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuotaModel {
    @ApiModelProperty(value="模型编号",required = true,example = "1")
    private Integer modelId;
    @ApiModelProperty(value="模型名称",required = true,example = "模型1")
    private String modelName;
    @ApiModelProperty(value="模型版本号",required = true,example = "1.0")
    private String modelVersion;
    private Integer calcType;
    @ApiModelProperty(value="模型脚本文件名",required = true,example = "xx.python")
    private String modelPath;
    @ApiModelProperty(value="模型状态",required = true,example = "1",notes = "1启用 0禁用")
    private Integer status;
    @ApiModelProperty(value="创建时间",required = true,example = "2020-01-01 00:00:00")
    private String createTime;

}
