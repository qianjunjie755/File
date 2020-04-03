package com.biz.warning.service;

import com.biz.warning.vo.HitedVariableOverviewVO;
import com.biz.warning.vo.WarnResultRuleCountVO;
import com.biz.warning.vo.WarnResultVariableVO;

import java.util.List;
import java.util.Map;

/**
 * 预警结果接口
 */
public interface IWarnResultService {
    List<WarnResultVariableVO> findWarnResultVariable(Long taskId, Long entityId, String period);

    List<WarnResultVariableVO> findWarnResultVariableByEntity(WarnResultVariableVO warnResultVariableVO, List<Integer> userIdList) throws Exception ;

    List<Map<String,Object>> findRiskSourceCount(WarnResultVariableVO warnResultVariableVO, List<Integer> userIdList) throws Exception ;

    List<WarnResultVariableVO> findWarnResultVariableByTask(WarnResultVariableVO warnResultVariableVO) throws Exception;

    List<WarnResultRuleCountVO> findWarnResultRule(Long taskId, Long entityId, String period);

    List<HitedVariableOverviewVO> findHitedVariableOverview(Long taskId, Long entityId, String period);

}
