package com.biz.credit.vo;

import com.biz.credit.domain.QuotaModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@ApiModel("额度模型对象")
@Getter
@Setter
@NoArgsConstructor
public class QuotaModelVO extends QuotaModel {
    @ApiModelProperty(value = "额度模型对象集合",required = true)
    private List<QuotaModelVarVO> vars;

   /* public QuotaModelVO(Integer modelId, String modelName, String modelVersion, Integer calcType, String modelPath, Integer status, String createTime) {
        super.(modelId,modelName,modelVersion,calcType,modelPath,status,createTime)
    }*/


    public QuotaModelVO(Integer modelId, String modelName, String modelVersion, Integer calcType, String modelPath, Integer status, String createTime) {
        super(modelId, modelName, modelVersion, calcType, modelPath, status, createTime);
    }
}
