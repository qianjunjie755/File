package com.biz.warning.service.impl;

import com.biz.warning.dao.VariablePoolDAO;
import com.biz.warning.domain.VariablePool;
import com.biz.warning.service.IVariablePoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariablePoolServiceImpl implements IVariablePoolService {

    @Autowired
    private VariablePoolDAO variablePoolDAO;

    @Override
    public List<VariablePool> findByApiProdCode(String apiProdCode) {
        return variablePoolDAO.findByApiProdCode(apiProdCode);
    }
}
