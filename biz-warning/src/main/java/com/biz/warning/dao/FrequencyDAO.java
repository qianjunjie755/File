package com.biz.warning.dao;

import com.biz.warning.domain.FrequencyPool;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrequencyDAO {
    /**
     * 查询全部频率
     * @param
     * @return
     */
    List<FrequencyPool>  findAllFrequency();
}
