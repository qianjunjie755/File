package com.biz.credit.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface VariableParamPoolDAO {
    String findDescriptionListByParamNames(@Param("list") Set<String> paramNameList) throws Exception;
}
