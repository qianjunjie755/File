package com.biz.search.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel("企业工商基本信息")
public class BasicInfoHighLight extends BasicInfo {
    @ApiModelProperty(value = "高亮内容")
    private String highLightText;
}
