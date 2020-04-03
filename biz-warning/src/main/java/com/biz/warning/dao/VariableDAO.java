package com.biz.warning.dao;

import com.biz.warning.domain.VariablePool;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.VariableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableDAO {

    long updateVariable(@Param("variable") VariableVO variable);

    long updateVariableValue(@Param("variable") VariableVO variable);

    long updateVariableThresholdForTaskUpdate(@Param("variable") VariableVO variable);

    VariableVO findSingleVariable(@Param("variable") VariableVO variable);
    /**
     * 删除变量
     * @param  variable
     * @return
     */
    long deleteVariable(@Param("variable") VariableVO variable);

    /**
     * 按规则编号删除变量
     * @param  rule
     * @return
     */
    long deleteVariableByRuleId(@Param("rule") RuleVO rule);



    /**
     * 新增规则变量关联关系
     * @param  variable
     * @return
     */
    long addVariable(@Param("variable") VariableVO variable);

    /**
     * 新增规则变量关联关系
     * @param  variableList
     * @return
     */
    long addVariableList(List<VariableVO> variableList);

    /**
     * 查找规则变量关联关系
     * @param  variable
     * @return
     */
    List<VariableVO> findVariable(@Param("variable") VariableVO variable);


    /**
     * 按照类型获取变量
     * @param
     * @return
     */
    List<VariablePool> findVariableByType(@Param("variableTypeCode") Long variableTypeCode);

    /**
     * 按照变量名获取版本
     * @param
     * @return
     */
    List<VariablePool> findVersionByVariableName(@Param("variableName") String variableName);

    /**
     * 按照规则获取变量
     * @param
     * @return
     */
    List<VariableVO> findVariableByRule(@Param("rule") RuleVO rule);

    /**
     * 按照变量编码获取默认阈值
     * @param
     * @return
     */
    String findThresholdByVariableCode(@Param("variableCode") long variableCode);

}
