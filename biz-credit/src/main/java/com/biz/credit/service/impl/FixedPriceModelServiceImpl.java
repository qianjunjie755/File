package com.biz.credit.service.impl;

import com.biz.credit.dao.FixedPriceModelDAO;
import com.biz.credit.dao.FixedPriceModelVarDAO;
import com.biz.credit.service.IFixedPriceModelService;
import com.biz.credit.vo.FixedPriceModelVO;
import com.biz.credit.vo.FixedPriceModelVarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixedPriceModelServiceImpl implements IFixedPriceModelService {
    @Autowired
    private FixedPriceModelVarDAO fixedPriceModelVarDAO;
    @Autowired
    private FixedPriceModelDAO fixedPriceModelDAO;
    @Override
    public List<FixedPriceModelVO> findModelList(Integer modelId,String modelName) {
        return fixedPriceModelDAO.findModelVOList(modelId,modelName);
    }

    @Override
    public List<FixedPriceModelVarVO> findModelVarList(Integer modelId) {
        return fixedPriceModelVarDAO.findModelVarList(modelId);
    }

    @Override
    public int addModel(FixedPriceModelVO fixedPriceModelVO) {

        return 0;
    }
}
