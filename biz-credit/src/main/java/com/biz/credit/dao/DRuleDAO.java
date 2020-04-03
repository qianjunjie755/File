package com.biz.credit.dao;

import com.biz.credit.domain.DNodeParam;
import com.biz.credit.vo.DNodeRuleVO;
import com.biz.credit.vo.DRuleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DRuleDAO {
    List<DRuleVO> queryListByKeyword(@Param("rule") DRuleVO dRuleVO);

    List<DRuleVO> selectDruleVarMap(@Param("ruleId") DRuleVO dRuleVO);

    List<DRuleVO> queryRuleList(@Param("rule") DRuleVO ruleVO);

    DRuleVO queryRuleById(@Param("ruleId") Long ruleId);

    List<DRuleVO> queryRuleVersionListByCode(String ruleCode);

    List<DNodeParam> queryByRuleId(@Param("nodeRuleIdList") List<DNodeRuleVO> nodeRuleIdList);

    List<DRuleVO> querySrcRuleList (@Param("rule")DRuleVO dRuleVO);
}
