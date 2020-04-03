package com.biz.warning.service.impl;

import com.biz.warning.dao.VariableTypeDAO;
import com.biz.warning.domain.VariableTypePool;
import com.biz.warning.service.IVariableTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariableTypeServiceImpl implements IVariableTypeService {
    @Autowired
    private VariableTypeDAO dao;


    @Override
    public List<VariableTypePool> findFirstVariableType() {
        return dao.findFirstVariableType();
    }

    @Override
    public List<VariableTypePool> findSecondVariableType(String firstTypeName) {
        return dao.findSecondVariableType(firstTypeName);
    }
}
