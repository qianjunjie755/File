package com.biz.credit.vo;

import com.biz.credit.domain.FixedPriceModelVar;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("定价模型变量")
@Getter
@Setter
@NoArgsConstructor
public class FixedPriceModelVarVO extends FixedPriceModelVar {
    public FixedPriceModelVarVO(Integer modelId, String apiProdCode, String apiVersion, String varCode, String varName, String varVersion, String varInterval, String varJsonPath, String paramName, Integer status, String createTime) {
        super(modelId, apiProdCode, apiVersion, varCode, varName, varVersion, varInterval, varJsonPath, paramName, status, createTime);
    }
}
