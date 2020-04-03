package com.biz.warning.service.impl;

import com.biz.warning.dao.VariableSourceDAO;
import com.biz.warning.domain.VariableSource;
import com.biz.warning.service.IVariableSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VariableSourceServiceImpl implements IVariableSourceService {
    @Autowired
    private VariableSourceDAO variableSourceDAO;
    @Override
    public List<VariableSource> findAllVariableSource() throws Exception {
        return variableSourceDAO.findAllVariableSourceList();
    }
}
