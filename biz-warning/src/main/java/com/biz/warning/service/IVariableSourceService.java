package com.biz.warning.service;

import com.biz.warning.domain.VariableSource;

import java.util.List;

public interface IVariableSourceService {
    List<VariableSource> findAllVariableSource() throws Exception;
}
