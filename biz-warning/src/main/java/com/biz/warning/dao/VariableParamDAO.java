package com.biz.warning.dao;

import com.biz.warning.domain.VariableParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VariableParamDAO {
    long addVariableParam(@Param("variableParam") VariableParam variableParam);
    long updateVariableParam(@Param("variableParam") VariableParam variableParam);
    VariableParam findSingleVariableParam(@Param("variableParam") VariableParam variableParam);
    long deleteVariableParam(@Param("variableParam") VariableParam variableParam);
}
