package com.biz.warning.service.impl;

import com.biz.warning.dao.WarnResultDAO;
import com.biz.warning.domain.Rule;
import com.biz.warning.service.IWarnResultService;
import com.biz.warning.util.DateUtil;
import com.biz.warning.util.StringUtil;
import com.biz.warning.vo.HitedVariableOverviewVO;
import com.biz.warning.vo.WarnResultRuleCountVO;
import com.biz.warning.vo.WarnResultVariableCountVO;
import com.biz.warning.vo.WarnResultVariableVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WarnResultServiceImpl implements IWarnResultService {

    @Autowired
    private WarnResultDAO dao;

    @Override
    public List<WarnResultVariableVO> findWarnResultVariable(Long taskId,Long entityId,String period) {
        period = DateUtil.parseDateToStr(DateUtil.addDate(new Date(),period),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
        return dao.findWarnResultVariable(taskId, entityId,period);
    }

    @Override
    public List<WarnResultVariableVO> findWarnResultVariableByEntity(WarnResultVariableVO warnResultVariableVO,List<Integer> userIdList)  throws Exception {
        if(!StringUtils.isEmpty(warnResultVariableVO.getHitTypeStr()) && !"-1".equals(warnResultVariableVO.getHitTypeStr())){
            warnResultVariableVO.setHitTypeList(StringUtil.changeStr2IntList(warnResultVariableVO.getHitTypeStr(),","));
        }
        if(!StringUtils.isEmpty(warnResultVariableVO.getTaskIdListStr())){
            warnResultVariableVO.setTaskIdList(StringUtil.changeStr2IntList(warnResultVariableVO.getTaskIdListStr(),","));
        }
        return dao.findWarnResultVariableByEntity(warnResultVariableVO,userIdList);
    }

    @Override
    public List<Map<String, Object>> findRiskSourceCount(WarnResultVariableVO warnResultVariableVO, List<Integer> userIdList) throws Exception {
        if(!StringUtils.isEmpty(warnResultVariableVO.getHitTypeStr()) && !"-1".equals(warnResultVariableVO.getHitTypeStr())){
            warnResultVariableVO.setHitTypeList(StringUtil.changeStr2IntList(warnResultVariableVO.getHitTypeStr(),","));
        }
        if(!StringUtils.isEmpty(warnResultVariableVO.getTaskIdListStr())){
            warnResultVariableVO.setTaskIdList(StringUtil.changeStr2IntList(warnResultVariableVO.getTaskIdListStr(),","));
        }
        return dao.findRiskSourceCount(warnResultVariableVO,userIdList);
    }

    @Override
    public List<WarnResultVariableVO> findWarnResultVariableByTask(WarnResultVariableVO warnResultVariableVO) throws Exception {
        return dao.findWarnResultVariableByTask(warnResultVariableVO);
    }

    @Override
    public List<WarnResultRuleCountVO> findWarnResultRule(Long taskId,Long entityId,String period) {
        period = DateUtil.parseDateToStr(DateUtil.addDate(new Date(),period),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
        List<WarnResultRuleCountVO> list = dao.findWarnResultRule(taskId,entityId,period);

        for(WarnResultRuleCountVO vo : list){
            vo.setExecCount(dao.findRuleExecCount(taskId,entityId,period,vo.getSrcRuleId()));
        }
        return list;
    }

    @Override
    public List<HitedVariableOverviewVO> findHitedVariableOverview(Long taskId,Long entityId,String period) {
        period = DateUtil.parseDateToStr(DateUtil.addDate(new Date(),period),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
        List<HitedVariableOverviewVO> hList = new ArrayList<>();
        List<Rule> rList = dao.findWarnResultVariableRule(taskId,entityId,period);
        for(Rule rule : rList){
            HitedVariableOverviewVO vo = new HitedVariableOverviewVO();
            List<WarnResultVariableCountVO> warnResultVariableCountVOList = dao.findWarnResultVariableCount(taskId,entityId,period,rule.getSrcRuleId());
            vo.setRule(rule);
            vo.setWarnResultVariableCountVOList(warnResultVariableCountVOList);
            hList.add(vo);
        }
        return hList;
    }
}
