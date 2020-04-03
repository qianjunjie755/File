package com.biz.credit.service.impl;

import com.biz.credit.dao.QuotaModelDAO;
import com.biz.credit.dao.QuotaModelVarDAO;
import com.biz.credit.service.IQuotaModelService;
import com.biz.credit.vo.QuotaModelVO;
import com.biz.credit.vo.QuotaModelVarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuotaModelServiceImpl implements IQuotaModelService {

    @Autowired
    private QuotaModelDAO quotaModelDAO;
    @Autowired
    private QuotaModelVarDAO quotaModelVarDAO;

    @Override
    public List<QuotaModelVO> findModelList(Integer modelId,String modelName) {
        return quotaModelDAO.findModelVOList(modelId,modelName);
    }

    @Override
    public List<QuotaModelVarVO> findModelVarList(Integer modelId) {
        return quotaModelVarDAO.findModelVarList(modelId);
    }
}
