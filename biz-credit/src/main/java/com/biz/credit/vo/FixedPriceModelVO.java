package com.biz.credit.vo;

import com.biz.credit.domain.FixedPriceModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@ApiModel("定价模型对象")
@Getter
@Setter
@NoArgsConstructor
public class FixedPriceModelVO extends FixedPriceModel {
    @ApiModelProperty(value = "定价模型变量集合",required = true)
    private List<FixedPriceModelVarVO> vars;



    public FixedPriceModelVO(Integer modelId, String modelName, String modelVersion, Integer calcType, String modelPath, Integer status, String createTime) {
        super(modelId, modelName, modelVersion, calcType, modelPath, status, createTime);
    }
}
