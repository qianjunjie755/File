package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@ApiModel("决策流对象")
@Getter
@Setter
public class VarSetVO implements Serializable {
    @ApiModelProperty(value="规则集合对象",required = true)
    private List<VarVO> varVOList = new ArrayList<>();
    @ApiModelProperty(value="决策流编号",example = "1")
    private Integer setId;
    @ApiModelProperty(value="决策流名称",example = "1")
    private String name;

}
