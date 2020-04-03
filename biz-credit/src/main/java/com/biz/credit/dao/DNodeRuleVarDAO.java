package com.biz.credit.dao;

import com.biz.credit.vo.DNodeRuleVO;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.DNodeRuleVarVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DNodeRuleVarDAO  {

    List<DNodeRuleVarVO> queryNodeRuleVarList(@Param("ruleId") Long ruleId, @Param("srcRuleId") Long srcRuleId);

    List<DNodeRuleVarVO> queryAllSrcNodeRuleVarList();

    List<DNodeRuleVarVO> queryInstanceRuleVarList(@Param("nodeRule") DNodeRuleVO nodeRuleVO);

    void updateStatusByRuleId(@Param("ruleId") Long ruleId, @Param("status") Integer status);

    void insertList(List<DNodeRuleVarVO> dNodeRuleVarList);

    List<DNodeRuleVarVO> queryListByRuleIdNoStatus(@Param("ruleId") Long ruleId);

    List<DNodeRuleVarRefVO> queryRefVars(@Param("varId") Long varId, @Param("srcVarId") Long srcVarId);

    void updateList(List<DNodeRuleVarVO> dNodeRuleVarList);
}
