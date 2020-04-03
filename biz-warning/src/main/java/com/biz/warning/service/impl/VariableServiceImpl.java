package com.biz.warning.service.impl;

import com.biz.warning.dao.VariableDAO;
import com.biz.warning.domain.VariablePool;
import com.biz.warning.service.IVariableService;
import com.biz.warning.vo.VariableVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariableServiceImpl implements IVariableService {
    @Autowired
    private VariableDAO dao;


    @Override
    public List<VariablePool> findVariableByType(Long variableTypeCode) {
        return dao.findVariableByType(variableTypeCode);
    }

    @Override
    public List<VariablePool> findVersionByVariableName(String variableName) {
        return dao.findVersionByVariableName(variableName);
    }

    @Override
    public String findThresholdByVariableCode(long variableCode) {
        return dao.findThresholdByVariableCode(variableCode);
    }

    @Override
    public long updateVariableThresholdForTaskUpdate(VariableVO variableVO) {
        return dao.updateVariableThresholdForTaskUpdate(variableVO);
    }

}
