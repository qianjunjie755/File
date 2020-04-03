package com.biz.credit.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("返回对象内容")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespCommonData {
/*    @ApiModelProperty(value="消息",required = true,example = "success")
    private String msg;*/
    @ApiModelProperty(value="数量",required = true,example = "0")
    private Integer count;
}
