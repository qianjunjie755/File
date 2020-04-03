package com.biz.warning.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.dao.EntityDAO;
import com.biz.warning.dao.RuleDAO;
import com.biz.warning.domain.EntityCount;
import com.biz.warning.domain.HitProcessr;
import com.biz.warning.domain.HitTrendCount;
import com.biz.warning.service.IRiskOverviewService;
import com.biz.warning.util.LocalDateTimeUtil;
import com.biz.warning.vo.EntityVO;
import com.biz.warning.vo.RuleVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RiskOverviewServiceImpl implements IRiskOverviewService {
    @Resource
    private EntityDAO entityDAO;
    @Resource
    private RuleDAO ruleDAO;


    @Override
    public Map findHitEntityPointMostList(String apiCode, List<Integer> userIds) {
        List<EntityVO> entityResult;
        List<RuleVO> ruleResult;
        Map map=new HashMap<String,List>();
        if (userIds != null && !userIds.isEmpty()) {
            entityResult = entityDAO.findHitEntityMostListByUserIds(apiCode, userIds);
            ruleResult= ruleDAO.findHitRuleMostListByUserIds(apiCode, userIds);
        } else {
            entityResult = entityDAO.findHitEntityMostList(apiCode);
            ruleResult= ruleDAO.findHitRuleMostList(apiCode);
        }
        map.put("entity",entityResult);
        map.put("point",ruleResult);
        return map;
    }

    @Override
    public List<EntityCount> countEntityAmount(String apiCode, List<Integer> userIds) {
        List<EntityCount> entityCount;
        if (userIds != null && !userIds.isEmpty()) {
            entityCount = entityDAO.countEntityAmountByUserIds(apiCode, userIds);
        } else {
            entityCount = entityDAO.countEntityAmountByApiCode(apiCode);
        }
        return entityCount;
    }

    @Override
    public JSONObject countHitTrend(String apiCode, List<Integer> userIds, String type) {
        JSONObject jsonObject = new JSONObject();
        List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        jsonObject.put("dateList", dateList);
        jsonObject.put("countList", countList);

        int n = 0;
        int unit = 0;
        LocalDate beginDate = null;
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        //获取第一次进件上传日期
        String firstDateString;
        if (userIds != null && !userIds.isEmpty()) {
            firstDateString = entityDAO.getFirstDateByUserIds(apiCode, userIds);
        } else {
            firstDateString = entityDAO.getFirstDateByApiCode(apiCode);
        }
        if (StringUtils.isBlank(firstDateString)) {
            return jsonObject;
        }
        LocalDate firstDate = LocalDate.parse(firstDateString);
        // 全部
        if (!"0".equals(type)) {
            n = Integer.valueOf(type.substring(0, type.length() - 1));
            char u = type.charAt(type.length() - 1);
            if (u == 'd' || u == 'D') {
                unit = 1; //天
                beginDate = endDate.minusDays(n).plusDays(1);
            } else if (u == 'w' || u == 'W') {
                unit = 2; //周
                beginDate = endDate.minusWeeks(n).plusDays(1);
            } else if (u == 'm' || u == 'M') {
                unit = 3; //月
                beginDate = endDate.minusMonths(n).plusDays(1);
            } else if (u == 'y' || u == 'Y') {
                unit = 4; //年
                beginDate = endDate.minusYears(n).plusDays(1);
            } else {
                log.error("未知统计类型单位[{}]", type);
                return jsonObject;
            }
            //如果beginDate<=firstDate则用firstDate
            if (beginDate.isBefore(firstDate)) {
                beginDate = firstDate;
            }
        } else {
            beginDate = firstDate;
        }
        //
        List<HitTrendCount> hitTrendCount;
        if (userIds != null && !userIds.isEmpty()) {
            hitTrendCount = entityDAO.countHitTrendByUserIds(apiCode, userIds, n, unit);
        } else {
            hitTrendCount = entityDAO.countHitTrendByApiCode(apiCode, n, unit);
        }

        Map<String, Integer> datas = null;
        if (hitTrendCount != null && !hitTrendCount.isEmpty()) {
            datas = hitTrendCount.stream().collect(Collectors.toMap(HitTrendCount::getHitDate, HitTrendCount::getHitCount));
        } else {
            datas = new HashMap<>();
        }

        //处理数据
        LocalDate date = beginDate;
        long days = date.toEpochDay();
        long endDays = endDate.toEpochDay();
        String hitDate;
        Integer hitCount;
        while (days <= endDays) {
            hitDate = date.format(formatter);
            hitCount = datas.get(hitDate);
            dateList.add(hitDate);
            countList.add(hitCount == null ? 0 : hitCount);
            days++;
            date = LocalDate.ofEpochDay(days);
        }
        return jsonObject;
    }

    @Override
    public JSONObject countMonitorAndHitTrend(String apiCode, List<Integer> userIds, String type) {
        JSONObject jsonObject = new JSONObject();
        List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        List<Integer> hitRuleList = new ArrayList<>();

        //计算出起始日期
        LocalDateTime toDay=LocalDateTime.now();
        LocalDate beginDay=null;
        if (!"0".equals(type)) {
            int n = Integer.valueOf(type.substring(0, type.length() - 1));
            char u = type.charAt(type.length() - 1);
            if (u == 'd' || u == 'D') {
                beginDay = toDay.minusDays(n-1).toLocalDate();
            } else if (u == 'w' || u == 'W') {
                beginDay = toDay.minusWeeks(n).plusDays(1).toLocalDate();
            } else if (u == 'm' || u == 'M') {
                beginDay = toDay.minusMonths(n).plusDays(1).toLocalDate();
            } else if (u == 'y' || u == 'Y') {
                beginDay = toDay.minusYears(n).plusDays(1).toLocalDate();
            } else {
                log.error("未知统计类型单位[{}]", type);
                return jsonObject;
            }
        }
        //查出指定日期区间内的记录
        List<HitTrendCount> monitorAndHitTrend=null;
        List<HitTrendCount> hitRuleCompanyTrend=null;
        //查找该用户第一天上传
        String firstDateString;
        if (userIds != null && !userIds.isEmpty()) {
            firstDateString = entityDAO.getFirstDateByUserIds(apiCode, userIds);
            monitorAndHitTrend=entityDAO.countMonitorAndHitTrend(apiCode,userIds, null,
                    LocalDateTimeUtil.localDateToString(toDay.toLocalDate()));
            hitRuleCompanyTrend=entityDAO.countHitRuleCompany(apiCode,userIds,null,toDay);
        } else {
            firstDateString = entityDAO.getFirstDateByApiCode(apiCode);
            monitorAndHitTrend=entityDAO.countMonitorAndHitTrend(apiCode,null, null,
                    LocalDateTimeUtil.localDateToString(toDay.toLocalDate()));
            hitRuleCompanyTrend=entityDAO.countHitRuleCompany(apiCode,null,null,toDay);
        }
        if (StringUtils.isBlank(firstDateString)) {
            return jsonObject;
        }
        if(monitorAndHitTrend==null || monitorAndHitTrend.isEmpty()) {
            return jsonObject;
        }
        if(hitRuleCompanyTrend == null ||hitRuleCompanyTrend.isEmpty()){
            HitTrendCount hitTrendCount=new HitTrendCount();
            hitTrendCount.setHitDate(monitorAndHitTrend.get(0).getHitDate());
            hitTrendCount.setHitCount(0);
            hitRuleCompanyTrend.add(hitTrendCount);
        }

        LocalDate firstDate = LocalDate.parse(firstDateString);
        //调整起始日期
        LocalDate tmepDay = beginDay;
        if("0".equals(type) || !tmepDay.isAfter(firstDate)){
            tmepDay=firstDate;
        }

        //填充为空的日期对应的类型,list
        int j=0,i;
        //确定i 起始值 当天或者前一个
        for (i=0;i<monitorAndHitTrend.size();i++){
            if(!LocalDateTimeUtil.stringToLocalDate(monitorAndHitTrend.get(i).getHitDate()).isAfter(tmepDay)){
                continue;
            }
            break;
        }
        //确定i 起始值 当天或者前一个
        for (j=0;j<hitRuleCompanyTrend.size();j++){
            if(!LocalDateTimeUtil.stringToLocalDate(hitRuleCompanyTrend.get(j).getHitDate()).isAfter(tmepDay)){
                continue;
            }
            break;
        }
        LocalDate lastMOnitorDate =LocalDateTimeUtil.stringToLocalDate( monitorAndHitTrend.get(monitorAndHitTrend.size() - 1).getHitDate());
        LocalDate lashRuleCpnyDate = LocalDateTimeUtil.stringToLocalDate(hitRuleCompanyTrend.get(hitRuleCompanyTrend.size() - 1).getHitDate());
        if(lastMOnitorDate.isBefore(toDay.toLocalDate())){
            HitTrendCount hc=new HitTrendCount();
            hc.setHitDate(LocalDateTimeUtil.localDateTimeToString(toDay));
            hc.setHitCount(monitorAndHitTrend.get(monitorAndHitTrend.size() - 1).getHitCount());
            monitorAndHitTrend.add(hc);
        }
        if(lashRuleCpnyDate.isBefore(toDay.toLocalDate())){
            HitTrendCount hc=new HitTrendCount();
            hc.setHitDate(LocalDateTimeUtil.localDateTimeToString(toDay));
            hc.setHitCount(hitRuleCompanyTrend.get(hitRuleCompanyTrend.size() - 1).getHitCount());
            hitRuleCompanyTrend.add(hc);
        }
        
        
        while(i < monitorAndHitTrend.size() &&!tmepDay.isAfter(toDay.toLocalDate())){
            HitTrendCount hitTrendCount = monitorAndHitTrend.get(i);
            int hitCount = hitTrendCount.getHitCount();
            String hitDate = hitTrendCount.getHitDate();

            HitTrendCount hitRuleCompanyCount = hitRuleCompanyTrend.get(j);
            int hitRuleCount = hitRuleCompanyCount.getHitCount();
            String hitRuleDate = hitRuleCompanyCount.getHitDate();

            dateList.add(LocalDateTimeUtil.localDateToString(tmepDay));
            LocalDate tempHitDate =  LocalDateTimeUtil.stringToLocalDate(hitDate);
            LocalDate tempHitRuleDate =  LocalDateTimeUtil.stringToLocalDate(hitRuleDate);

            if(tmepDay.isEqual(tempHitDate)){
                countList.add(hitCount);
                i++;
            }else if(tmepDay.isBefore(LocalDateTimeUtil.stringToLocalDate(monitorAndHitTrend.get(0).getHitDate()))){
                countList.add(0);
            }
            else{
                //优化
                //累计监控企业，填充开始缺失及中间缺失
                countList.add(monitorAndHitTrend.get(i-1).getHitCount());
            }
            if(tmepDay.isEqual(tempHitRuleDate)){
                hitRuleList.add(hitRuleCount);
                j++;
            }else if(tmepDay.isBefore(LocalDateTimeUtil.stringToLocalDate(hitRuleCompanyTrend.get(0).getHitDate()))){
                hitRuleList.add(0);
            } else{
                //优化
                //累计监控企业，填充开始缺失及中间缺失
                hitRuleList.add(hitRuleCompanyTrend.get(j-1).getHitCount());
            }
            tmepDay=tmepDay.plusDays(1);
        }
        jsonObject.put("dateList", dateList);
        jsonObject.put("monitorList",countList );
        jsonObject.put("hitRuleList",hitRuleList );
        return jsonObject;
    }

    @Override
    public JSONObject findRiskHitSituaion(String apiCode, List<Integer> userIds, String type,
                                          String period, String[] sourceIds) {
        List<HitProcessr> result=null;
        JSONObject jsonObject = new JSONObject();
        Map map=new HashMap<String,List>();
        LocalDateTime toDay=LocalDateTime.now();
        LocalDateTime beginDay=null;
        if (!"0".equals(period)) {
            int n = Integer.valueOf(period.substring(0, period.length() - 1));
            char u = period.charAt(period.length() - 1);
            if (u == 'd' || u == 'D') {
                beginDay = toDay.minusDays(n).plusDays(1);
            } else if (u == 'w' || u == 'W') {
                beginDay = toDay.minusWeeks(n).plusDays(1);
            } else if (u == 'm' || u == 'M') {
                beginDay = toDay.minusMonths(n).plusDays(1);
            } else if (u == 'y' || u == 'Y') {
                beginDay = toDay.minusYears(n).plusDays(1);
            } else {
                log.error("未知统计类型单位[{}]", period);
                return jsonObject;
            }
        }
        if("variable".equals(type)){
            result=ruleDAO.findRiskHitSituaion(apiCode,userIds,sourceIds,beginDay.toLocalDate(),toDay.plusDays(1).minusSeconds(1));
        }else if("entity".equals(type)){
            result=ruleDAO.findRiskCompany(apiCode,userIds,sourceIds,beginDay.toLocalDate(),toDay.plusDays(1).minusSeconds(1));
        }else if("ruleSet".equals(type)){
            result=ruleDAO.findRiskRuleSet(apiCode,userIds,sourceIds,beginDay.toLocalDate(),toDay.plusDays(1).minusSeconds(1));
        }else if("rule".equals(type)){
            result=ruleDAO.findRiskRule(apiCode,userIds,sourceIds,beginDay.toLocalDate(),toDay.plusDays(1).minusSeconds(1));
        }else {
            return jsonObject;
        }
        jsonObject.put("point",result);
        return jsonObject;
    }
    //待优化
    public JSONObject monitorAndHitTrend(String apiCode, List<Integer> userIds, String type) {
        JSONObject jsonObject = new JSONObject();
        List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        List<Integer> hitRuleList = new ArrayList<>();

        //计算出起始日期
        LocalDateTime toDay=LocalDateTime.now();
        LocalDate beginDay=null;
        if (!"0".equals(type)) {
            int n = Integer.valueOf(type.substring(0, type.length() - 1));
            char u = type.charAt(type.length() - 1);
            if (u == 'd' || u == 'D') {
                beginDay = toDay.minusDays(n).toLocalDate();
            } else if (u == 'w' || u == 'W') {
                beginDay = toDay.minusWeeks(n).toLocalDate();
            } else if (u == 'm' || u == 'M') {
                beginDay = toDay.minusMonths(n).toLocalDate();
            } else if (u == 'y' || u == 'Y') {
                beginDay = toDay.minusYears(n).toLocalDate();
            } else {
                log.error("未知统计类型单位[{}]", type);
                return jsonObject;
            }
        }
        //查出指定日期区间内的记录
        List<HitTrendCount> monitorAndHitTrend=null;
        List<HitTrendCount> hitRuleCompanyTrend=null;
        //查找该用户第一天上传
        String firstDateString;
        if (userIds != null && !userIds.isEmpty()) {
            firstDateString = entityDAO.getFirstDateByUserIds(apiCode, userIds);
            monitorAndHitTrend=entityDAO.countMonitorAndHitTrend(apiCode,userIds, null,
                    LocalDateTimeUtil.localDateToString(toDay.toLocalDate()));
            hitRuleCompanyTrend=entityDAO.countHitRuleCompany(apiCode,userIds,beginDay,toDay);
        } else {
            firstDateString = entityDAO.getFirstDateByApiCode(apiCode);
            monitorAndHitTrend=entityDAO.countMonitorAndHitTrend(apiCode,null, null,
                    LocalDateTimeUtil.localDateToString(toDay.toLocalDate()));
            hitRuleCompanyTrend=entityDAO.countHitRuleCompany(apiCode,null,beginDay,toDay);
        }
        if(monitorAndHitTrend==null && hitRuleCompanyTrend == null){
            return jsonObject;
        }
        if (StringUtils.isBlank(firstDateString)) {
            return jsonObject;
        }
        LocalDate firstDate = LocalDate.parse(firstDateString);

        //调整起始日期
        LocalDate tmepDay = beginDay;
        if("0".equals(type) || !tmepDay.isAfter(firstDate)){
            tmepDay=firstDate;
        }

        Map<String, Integer> datas = null;
        if (monitorAndHitTrend != null && !monitorAndHitTrend.isEmpty()) {
            datas = monitorAndHitTrend.stream().collect(Collectors.toMap(HitTrendCount::getHitDate, HitTrendCount::getHitCount));
        } else {
            datas = new HashMap<>();
        }
        //处理数据
        LocalDate date = tmepDay;
        long days = date.toEpochDay();
        long endDays = toDay.toLocalDate().toEpochDay();
        String hitDate;
        Integer hitCount;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        while (days <= endDays) {
            hitDate = date.format(formatter);
            hitCount = datas.get(hitDate);
            dateList.add(hitDate);
            countList.add(hitCount == null ? countList.get(0): hitCount);
            days++;
            date = LocalDate.ofEpochDay(days);
        }

        jsonObject.put("dateList", dateList);
        jsonObject.put("monitorList",countList );
        jsonObject.put("hitRuleList",hitRuleList );
        return jsonObject;
    }

    //优化
    @Override
    public JSONObject monitorAndHitCompTrend(String apiCode, List<Integer> userIds, String type) {

        JSONObject jsonObject = new JSONObject();
        List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        List<Integer> hitRuleList = new ArrayList<>();

        //计算出起始日期
        LocalDateTime toDay=LocalDateTime.now();
        LocalDate beginDay=null;
        if (!"0".equals(type)) {
            int n = Integer.valueOf(type.substring(0, type.length() - 1));
            char u = type.charAt(type.length() - 1);
            if (u == 'd' || u == 'D') {
                beginDay = toDay.minusDays(n-1).toLocalDate();
            } else if (u == 'w' || u == 'W') {
                beginDay = toDay.minusWeeks(n).plusDays(1).toLocalDate();
            } else if (u == 'm' || u == 'M') {
                beginDay = toDay.minusMonths(n).plusDays(1).toLocalDate();
            } else if (u == 'y' || u == 'Y') {
                beginDay = toDay.minusYears(n).plusDays(1).toLocalDate();
            } else {
                log.error("未知统计类型单位[{}]", type);
                return jsonObject;
            }
        }
        //查出指定日期区间内的记录
        List<HitTrendCount> monitorAndHitTrend=null;
        List<HitTrendCount> hitRuleCompanyTrend=null;
        //查找该用户第一天上传
        String firstDateString;
        if (userIds != null && !userIds.isEmpty()) {
            firstDateString = entityDAO.getFirstDateByUserIds(apiCode, userIds);
            monitorAndHitTrend=entityDAO.countMonitorAndHitTrend(apiCode,userIds, null,
                    LocalDateTimeUtil.localDateToString(toDay.toLocalDate()));
            hitRuleCompanyTrend=entityDAO.countHitRuleCompany(apiCode,userIds,null,toDay);
        } else {
            firstDateString = entityDAO.getFirstDateByApiCode(apiCode);
            monitorAndHitTrend=entityDAO.countMonitorAndHitTrend(apiCode,null, null,
                    LocalDateTimeUtil.localDateToString(toDay.toLocalDate()));
            hitRuleCompanyTrend=entityDAO.countHitRuleCompany(apiCode,null,null,toDay);
        }
        if (StringUtils.isBlank(firstDateString)) {
            return jsonObject;
        }
        if(monitorAndHitTrend==null || monitorAndHitTrend.isEmpty()) {
            return jsonObject;
        }
        if(hitRuleCompanyTrend == null ||hitRuleCompanyTrend.isEmpty()){
            HitTrendCount hitTrendCount=new HitTrendCount();
            hitTrendCount.setHitDate(monitorAndHitTrend.get(0).getHitDate());
            hitTrendCount.setHitCount(0);
            hitRuleCompanyTrend.add(hitTrendCount);
        }
        LocalDate firstDate = LocalDate.parse(firstDateString);

        //转换
        Map<String, Integer> monitorDatas = null;
        Map<String, Integer> hitCompDatas = null;
        if (monitorAndHitTrend != null && !monitorAndHitTrend.isEmpty()) {
            monitorDatas = monitorAndHitTrend.stream().collect(Collectors.toMap(HitTrendCount::getHitDate, HitTrendCount::getHitCount));
        } else {
            monitorDatas = new HashMap<>();
        }
        if (hitRuleCompanyTrend != null && !hitRuleCompanyTrend.isEmpty()) {
            hitCompDatas = hitRuleCompanyTrend.stream().collect(Collectors.toMap(HitTrendCount::getHitDate, HitTrendCount::getHitCount));
        } else {
            hitCompDatas = new HashMap<>();
        }

        //补全
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String hitDate;
        Integer historyMonitorCount=0;
        Integer historyhitCompCount=0;
        LocalDate beginTemp=firstDate;
        while(!beginTemp.isAfter(toDay.toLocalDate())){
            hitDate = beginTemp.format(formatter);
            Integer monitorCount = monitorDatas.get(hitDate);
            Integer hitCompCount = hitCompDatas.get(hitDate);
            dateList.add(hitDate);
            Integer mcount = monitorCount == null ? historyMonitorCount : monitorCount;
            countList.add(mcount);
            historyMonitorCount = mcount;
            Integer hcount = hitCompCount == null ? historyhitCompCount: hitCompCount;
            hitRuleList.add(hcount);
            historyhitCompCount = hcount;
            beginTemp= beginTemp.plusDays(1);
        }

        //调整起始日期
        LocalDate tmepDay = beginDay;
        if("0".equals(type) || tmepDay.isBefore(firstDate)){
            tmepDay=firstDate;
        }
        Integer tmepDayIndex=dateList.indexOf(tmepDay.format(formatter));
        List subDateList=dateList.subList(tmepDayIndex,dateList.size());
        List subCountList=countList.subList(tmepDayIndex,dateList.size());
        List subHitRuleList=hitRuleList.subList(tmepDayIndex,dateList.size());
        jsonObject.put("dateList", subDateList);
        jsonObject.put("monitorList",subCountList );
        jsonObject.put("hitRuleList",subHitRuleList );

        return jsonObject;
    }

}
