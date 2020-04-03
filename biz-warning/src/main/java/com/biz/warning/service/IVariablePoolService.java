package com.biz.warning.service;

import com.biz.warning.domain.VariablePool;

import java.util.List;

public interface IVariablePoolService {

    List<VariablePool> findByApiProdCode(String apiProdCode);
}
