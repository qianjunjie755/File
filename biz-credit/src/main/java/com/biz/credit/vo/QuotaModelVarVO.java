package com.biz.credit.vo;

import com.biz.credit.domain.QuotaModelVar;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("额度模型变量")
@Getter
@Setter
@NoArgsConstructor
public class QuotaModelVarVO extends QuotaModelVar {
    public QuotaModelVarVO(Integer modelId, String apiProdCode, String apiVersion, String varCode, String varName, String varVersion, String varInterval, String varJsonPath, String paramName, Integer status, String createTime) {
        super(modelId, apiProdCode, apiVersion, varCode, varName, varVersion, varInterval, varJsonPath, paramName, status, createTime);
    }
}
