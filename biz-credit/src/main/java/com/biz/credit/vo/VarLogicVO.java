package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel("决策流逻辑变量对象")
@Getter
@Setter
public class VarLogicVO implements Serializable {
    @ApiModelProperty(value="逻辑变量阈值集合中，各个逻辑变量阈值之间的计算逻辑",example = "1",notes = "1 或 0 与",required = true)
    private Integer logic = 1;
    @ApiModelProperty(value="逻辑变量阈值集合",required = true)
    private List<VarThresholdVO> varThresholdVOList = new ArrayList<>();
}
