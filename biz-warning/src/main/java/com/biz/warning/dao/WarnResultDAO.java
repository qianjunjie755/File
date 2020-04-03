package com.biz.warning.dao;

import com.biz.warning.domain.Rule;
import com.biz.warning.vo.WarnResultRuleCountVO;
import com.biz.warning.vo.WarnResultVariableCountVO;
import com.biz.warning.vo.WarnResultVariableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WarnResultDAO {
    List<WarnResultVariableVO> findWarnResultVariable(@Param("taskId") Long taskId, @Param("entityId") Long entityId, @Param("period") String period);
    List<WarnResultVariableVO> findWarnResultVariableByTask(@Param("wrv") WarnResultVariableVO warnResultVariableVO) throws Exception;

    List<WarnResultRuleCountVO>  findWarnResultRule(@Param("taskId") Long taskId, @Param("entityId") Long entityId, @Param("period") String period);

    Long  findRuleExecCount(@Param("taskId") Long taskId, @Param("entityId") Long entityId, @Param("period") String period, @Param("srcRuleId") Long srcRuleId);

    List<Rule>  findWarnResultVariableRule(@Param("taskId") Long taskId, @Param("entityId") Long entityId, @Param("period") String period);

    List<WarnResultVariableCountVO>  findWarnResultVariableCount(@Param("taskId") Long taskId, @Param("entityId") Long entityId, @Param("period") String period, @Param("srcRuleId") Long srcRuleId);

    List<WarnResultVariableVO> findWarnResultVariableByEntity(@Param("warnResultVariableVO") WarnResultVariableVO warnResultVariableVO, @Param("userIdList") List<Integer> userIdList) throws Exception;

    List<Map<String,Object>> findRiskSourceCount(@Param("warnResultVariableVO") WarnResultVariableVO warnResultVariableVO, @Param("userIdList") List<Integer> userIdList) throws Exception;
}
