package com.biz.warning.dao;

import com.biz.warning.domain.VariableLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableLogDAO {
    int addVariableLogList(List<VariableLog> variableLogList) throws Exception;
}
