package com.biz.warning.service;

import com.biz.warning.domain.VariableTypePool;

import java.util.List;

/**
 * 变量类型接口
 */
public interface IVariableTypeService {
    List<VariableTypePool> findFirstVariableType();
    List<VariableTypePool> findSecondVariableType(String firstTypeName);
}
