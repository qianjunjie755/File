package com.biz.credit.service.impl;

import com.biz.credit.dao.VariablePeriodDAO;
import com.biz.credit.domain.VariablePeriod;
import com.biz.credit.service.IVariablePeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariablePeriodServiceImpl implements IVariablePeriodService{

    @Autowired
    private VariablePeriodDAO variablePeriodDAO;

    @Override
    public List<VariablePeriod> getVariablePeriodList(){
        return variablePeriodDAO.queryList();
    }
}
