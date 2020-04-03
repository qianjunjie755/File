package com.biz.warning.dao;

import com.biz.warning.domain.VariablePool;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariablePoolDAO {
    /**
     * 按数据源查找
     * @param
     * @return
     */
    List<VariablePool> findByApiProdCode(@Param("apiProdCode") String apiProdCode);
}
