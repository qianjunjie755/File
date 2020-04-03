package com.biz.warning.dao;

import com.biz.warning.domain.Period;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodDAO {
    /**
     * 查询全部时间范围
     * @param
     * @return
     */
    List<Period>  findAllPeriod();
}
