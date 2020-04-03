package com.biz.warning.dao;

import com.biz.warning.domain.VariableSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableSourceDAO {
    List<VariableSource> findAllVariableSourceList() throws Exception;
}
