package com.biz.warning.service;

import com.biz.warning.domain.VariablePool;
import com.biz.warning.vo.VariableVO;

import java.util.List;

/**
 *  变量维护接口
 */
public interface IVariableService {

    List<VariablePool> findVariableByType(Long variableTypeCode);

    List<VariablePool> findVersionByVariableName(String variableName);

    String findThresholdByVariableCode(long variableCode);
    long updateVariableThresholdForTaskUpdate(VariableVO variableVO);
}
