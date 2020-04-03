package com.biz.credit.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@ApiModel("分页结果")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {
    @ApiModelProperty(value = "总记录数", required = true)
    private long total;
    @ApiModelProperty(value = "当前页内容", required = true)
    private List<T> rows;

}
