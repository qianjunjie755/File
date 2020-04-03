package com.biz.credit.dao;

import com.biz.credit.domain.DRuleVar;
import com.biz.credit.vo.DRuleVarVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DRuleVarDAO {

    DRuleVarVO queryById(@Param("varId") Long varId);

    List<DRuleVarVO> queryListByRuleId(@Param("ruleId") Long ruleId);

    List<DRuleVarVO> queryVersionListByProdCode(@Param("prodCode") String prodCode);

    Integer updateRuleVar(@Param("dDRuleVar") DRuleVar dDRuleVar);

    Integer updateRefVar(@Param("dDRuleVar")DRuleVar dDRuleVar);

    Integer updateRuleVarList(@Param("list")List<DRuleVar> dDRuleVar);

    Integer updateRefVarList(@Param("list")List<DRuleVar> dDRuleVar);
}
