package com.biz.credit.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@ApiModel("额度模型变量")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuotaModelVar {
    @ApiModelProperty(value="模型编号",required = true,example = "1")
    private Integer modelId;
    @ApiModelProperty(value="数据api代码",required = true,example = "BizDetail")
    private String apiProdCode;
    @ApiModelProperty(value="数据api版本号",required = true,example = "1.0")
    private String apiVersion;
    @ApiModelProperty(value="变量代码",required = true,example = "var.ic.biz_four")
    private String varCode;
    @ApiModelProperty(value="变量名称",required = true,example = "企业四要素验证")
    private String varName;
    @ApiModelProperty(value="变量版本号",required = true,example = "1.0")
    private String varVersion;
    @ApiModelProperty(value="变量时间颗粒度",required = true,example = "-1m",notes = "m-月 d-天 y-年 -1m-无限制")
    private String varInterval;
    @ApiModelProperty(value="获取变量值的路径",required = true,example = "$.Result.Hit")
    private String varJsonPath;
    @ApiModelProperty(value = "模型脚本入参名称",required = true,example = "param1")
    private String paramName;
    @ApiModelProperty(value="模型状态",required = true,example = "1",notes = "1启用 0禁用")
    private Integer status;
    @ApiModelProperty(value="创建时间",required = true,example = "2020-01-01 00:00:00")
    private String createTime;
}
