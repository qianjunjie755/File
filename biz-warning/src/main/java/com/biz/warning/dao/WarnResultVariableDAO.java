package com.biz.warning.dao;

import com.biz.warning.domain.VariableDetail;
import com.biz.warning.domain.WarnResultVariable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarnResultVariableDAO {
    int addWarnResultVariableList(List<WarnResultVariable> warnResultVariableList) throws Exception;
    int insertVariableDetail(List<VariableDetail> variableDetails);
}
