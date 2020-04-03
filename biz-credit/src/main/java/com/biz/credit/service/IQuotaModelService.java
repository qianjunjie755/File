package com.biz.credit.service;

import com.biz.credit.vo.QuotaModelVO;
import com.biz.credit.vo.QuotaModelVarVO;

import java.util.List;

public interface IQuotaModelService {
    List<QuotaModelVO> findModelList(Integer modelId,String modelName);
    List<QuotaModelVarVO> findModelVarList(Integer modelId);
}
