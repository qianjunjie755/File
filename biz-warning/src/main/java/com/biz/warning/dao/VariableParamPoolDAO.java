package com.biz.warning.dao;


import com.biz.warning.domain.VariableParamPool;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableParamPoolDAO {
    List<VariableParamPool> findListByVariableCode(@Param("variableCode") Long variableCode)throws Exception;
    List<VariableParamPool> findListByVariableCodes(List<Long> variableCodes) throws Exception;
    List<VariableParamPool> findListByNameList(List<String> nameList) throws Exception;
}
