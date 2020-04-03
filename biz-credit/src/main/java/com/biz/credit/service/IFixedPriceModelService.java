package com.biz.credit.service;

import com.biz.credit.vo.FixedPriceModelVO;
import com.biz.credit.vo.FixedPriceModelVarVO;

import java.util.List;

public interface IFixedPriceModelService {
    List<FixedPriceModelVO> findModelList(Integer modelId,String modelName);
    List<FixedPriceModelVarVO> findModelVarList(Integer modelId);
    int addModel(FixedPriceModelVO fixedPriceModelVO);
}
