package com.biz.credit.dao;

import com.biz.credit.domain.VariablePeriod;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariablePeriodDAO {

    List<VariablePeriod> queryList();
    List<VariablePeriod> queryListByVarPeriodAndPeriodUnit(@Param("varPeriod") Integer varPeriod, @Param("periodUnit") String periodUnit);
    List<VariablePeriod> queryListByPeriodId(@Param("periodId") Integer periodId);
}
