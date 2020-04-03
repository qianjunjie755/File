package com.biz.warning.dao;

import com.biz.warning.domain.VariableTypePool;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableTypeDAO {

    /**
     * 获取变量一级类型
     * @param
     * @return
     */
    List<VariableTypePool> findFirstVariableType();
    /**
     * 获取变量二级类型
     * @param firstTypeName 一级变量类型
     * @return
     */
    List<VariableTypePool> findSecondVariableType(@Param("firstTypeName") String firstTypeName);
}
