package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.BiRuleDataDAO;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.domain.responseData.BiRuleDataRes;
import com.biz.credit.domain.responseData.BiRuleIdDataRes;
import com.biz.credit.service.IRuleDataService;
import com.biz.credit.utils.DateUtil;
import com.biz.credit.vo.BiRuleDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RuleDataServiceImpl implements IRuleDataService {
    @Autowired
    private BiRuleDataDAO biRuleDataDAO;
    private Integer select_limit_time=100000;
    @Override
    public RespEntity findBiRuleDataByDayList(BiRuleDataVO biRuleDataVO) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = biRuleDataVO.getApiCode();
        if (StringUtils.isEmpty(apiCode)) {
            ret.setMsg("apiCode不能为空");
            return ret;
        }
        try {
            List<BiRuleDataRes> list = new ArrayList();
            if (StringUtils.isNotEmpty(biRuleDataVO.getInterval())) {
                String interval = biRuleDataVO.getInterval();//查询周期
                String intervalType = interval.substring(interval.length() - 1, interval.length());
                int size = Integer.parseInt(interval.substring(0, interval.length()-1));
                if ("D".equals(intervalType)) {//1-按天
                    if (size>select_limit_time){
                        ret.setMsg("查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    Date endDate = new Date();
                    Date beginDate = DateUtil.addDate(endDate, intervalType,size);
                    biRuleDataVO.setStartDate(DateUtil.parseDateToStr(beginDate, "yyyy-MM-dd"));
                    biRuleDataVO.setEndDate(DateUtil.parseDateToStr(endDate, "yyyy-MM-dd"));
                    //list = iRuleDataService.findBiRuleDataByDayList(biRuleDataVO);
                    list= biRuleDataDAO.findBiRuleDataByDayList(biRuleDataVO);
                } /*else if ("M".equals(intervalType)) {//2-按月
                    Date endMonth = new Date();
                    Date beginMonth = DateUtil.addDate(endMonth, intervalType,size);
                    biRuleDataVO.setStartMonth(DateUtil.parseDateToStr(beginMonth, "yyyyMM"));
                    biRuleDataVO.setEndMonth(DateUtil.parseDateToStr(endMonth, "yyyyMM"));
                    list = iRuleDataService.findBiRuleDataByMonthList(biRuleDataVO);
                } else if ("Y".equals(intervalType)) {//3-按年
                    Date endYear = new Date();
                    Date beginYear = DateUtil.addDate(endYear, intervalType,size);
                    biRuleDataVO.setStartYear(DateUtil.parseDateToStr(beginYear, "yyyy"));
                    biRuleDataVO.setEndYear(DateUtil.parseDateToStr(endYear, "yyyy"));
                    list = iRuleDataService.findBiRuleDataByYearList(biRuleDataVO);
                }*/ else {
                    ret.setMsg("查无数据");
                    ret.setCode("01");
                    return ret;
                }
            } else {//自定义时间
                String startDate_ = biRuleDataVO.getStartDate();
                String endDate_ = biRuleDataVO.getEndDate();
                if (StringUtils.isNotEmpty(startDate_) && StringUtils.isNotEmpty(endDate_)) {
                    List countDay=DateUtil.getDayListOfDate(startDate_,endDate_);
                    if(countDay.size()>select_limit_time){
                        ret.setMsg("自定义查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    biRuleDataVO.setStartYear(startDate_);
                    biRuleDataVO.setEndYear(endDate_);
                    list= biRuleDataDAO.findBiRuleDataByDayList(biRuleDataVO);
                } else {
                    ret.setMsg("查询开始时间或者结束时间不能为空");
                    ret.setCode("07");//参数错误
                    return ret;
                }
            }

            List data_x = new ArrayList();
            List data_y = new ArrayList();
            for (BiRuleDataRes vo : list) {
                data_x.add(vo.getDate_());//组织X轴数据 -数组
                data_y.add(vo.getCount_());//组织Y轴数据 -数组
            }
            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("data_x", data_x);
            jo.put("data_y", data_y);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("查询命中规则次数趋势失败");
            return ret;
        }
        return ret;
    }

    /*@Override
    public List<BiRuleDataRes> findBiRuleDataByMonthList(BiRuleDataVO biRuleDataVO) throws Exception {
        return biRuleDataDAO.findBiRuleDataByMonthList(biRuleDataVO);
    }*/

   /* @Override
    public List<BiRuleDataRes> findBiRuleDataByYearList(BiRuleDataVO biRuleDataVO) throws Exception {
        return biRuleDataDAO.findBiRuleDataByYearList(biRuleDataVO);
    }*/

    @Override
    public RespEntity findHitRuleCompanyByDayList(BiRuleDataVO biRuleDataVO) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = biRuleDataVO.getApiCode();
        if (StringUtils.isEmpty(apiCode)) {
            ret.setMsg("apiCode不能为空");
            return ret;
        }
        try {
            List<BiRuleDataRes> list = new ArrayList();
            if (StringUtils.isNotEmpty(biRuleDataVO.getInterval())) {
                String interval = biRuleDataVO.getInterval();//查询周期
                String intervalType = interval.substring(interval.length() - 1, interval.length());
                int size = Integer.parseInt(interval.substring(0, interval.length()-1));
                if ("D".equals(intervalType)) {//1-按天
                    if (size>select_limit_time){
                        ret.setMsg("查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    Date endDate = new Date();
                    Date beginDate = DateUtil.addDate(endDate, intervalType,size);
                    biRuleDataVO.setStartDate(DateUtil.parseDateToStr(beginDate, "yyyy-MM-dd"));
                    biRuleDataVO.setEndDate(DateUtil.parseDateToStr(endDate, "yyyy-MM-dd"));
                    // list = iRuleDataService.findHitRuleCompanyByDayList(biRuleDataVO);
                    list = biRuleDataDAO.findHitRuleCompanyByDayList(biRuleDataVO);
                } /*else if ("M".equals(intervalType)) {//2-按月
                    Date endMonth = new Date();
                    Date beginMonth = DateUtil.addDate(endMonth, intervalType,size);
                    biRuleDataVO.setStartMonth(DateUtil.parseDateToStr(beginMonth, "yyyyMM"));
                    biRuleDataVO.setEndMonth(DateUtil.parseDateToStr(endMonth, "yyyyMM"));
                    list = iRuleDataService.findHitRuleCompanyByMonthList(biRuleDataVO);
                } else if ("Y".equals(intervalType)) {//3-按年
                    Date endYear = new Date();
                    Date beginYear = DateUtil.addDate(endYear, intervalType,size);
                    biRuleDataVO.setStartYear(DateUtil.parseDateToStr(beginYear, "yyyy"));
                    biRuleDataVO.setEndYear(DateUtil.parseDateToStr(endYear, "yyyy"));
                    list = iRuleDataService.findHitRuleCompanyByYearList(biRuleDataVO);
                }*/ else {
                    ret.setMsg("查无数据");
                    ret.setCode("01");
                    return ret;
                }
            } else {//自定义时间
                String startDate_ = biRuleDataVO.getStartDate();
                String endDate_ = biRuleDataVO.getEndDate();
                if (StringUtils.isNotEmpty(startDate_) && StringUtils.isNotEmpty(endDate_)) {
                    List countDay=DateUtil.getDayListOfDate(startDate_,endDate_);
                    if(countDay.size()>select_limit_time){
                        ret.setMsg("自定义查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    biRuleDataVO.setStartYear(startDate_);
                    biRuleDataVO.setEndYear(endDate_);
                    list = biRuleDataDAO.findHitRuleCompanyByDayList(biRuleDataVO);
                } else {
                    ret.setMsg("查询开始时间或者结束时间不能为空");
                    ret.setCode("07");//参数错误
                    return ret;
                }
            }

            List data_x = new ArrayList();
            List data_y = new ArrayList();
            for (BiRuleDataRes vo : list) {
                data_x.add(vo.getDate_());//组织X轴数据 -数组
                data_y.add(vo.getCount_());//组织Y轴数据 -数组
            }
            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("data_x", data_x);
            jo.put("data_y", data_y);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("查询有命中规则进件趋势失败");
            return ret;
        }
        return ret;
    }

   /* @Override
    public List<BiRuleDataRes> findHitRuleCompanyByMonthList(BiRuleDataVO biRuleDataVO) throws Exception {
        return biRuleDataDAO.findHitRuleCompanyByMonthList(biRuleDataVO);
    }
*/
   /* @Override
    public List<BiRuleDataRes> findHitRuleCompanyByYearList(BiRuleDataVO biRuleDataVO) throws Exception {
        return biRuleDataDAO.findHitRuleCompanyByYearList(biRuleDataVO);
    }*/

    @Override
    public RespEntity findHitRuleMostByDayList(BiRuleDataVO biRuleDataVO) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = biRuleDataVO.getApiCode();
        if (StringUtils.isEmpty(apiCode)) {
            ret.setMsg("apiCode不能为空");
            return ret;
        }
        try {
            List<BiRuleIdDataRes> list = new ArrayList();
            if (StringUtils.isNotEmpty(biRuleDataVO.getInterval())) {
                String interval = biRuleDataVO.getInterval();//查询周期
                String intervalType = interval.substring(interval.length() - 1, interval.length());
                int size = Integer.parseInt(interval.substring(0, interval.length()-1));
                if ("D".equals(intervalType)) {//1-按天
                    if (size>select_limit_time){
                        ret.setMsg("查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    Date endDate = new Date();
                    Date beginDate = DateUtil.addDate(endDate, intervalType,size);
                    biRuleDataVO.setStartDate(DateUtil.parseDateToStr(beginDate, "yyyy-MM-dd"));
                    biRuleDataVO.setEndDate(DateUtil.parseDateToStr(endDate, "yyyy-MM-dd"));
                    // list = iRuleDataService.findHitRuleMostByDayList(biRuleDataVO);
                    list = biRuleDataDAO.findHitRuleMostByDayList(biRuleDataVO);
                } /*else if ("M".equals(intervalType)) {//2-按月
                    Date endMonth = new Date();
                    Date beginMonth = DateUtil.addDate(endMonth, intervalType,size);
                    biRuleDataVO.setStartMonth(DateUtil.parseDateToStr(beginMonth, "yyyyMM"));
                    biRuleDataVO.setEndMonth(DateUtil.parseDateToStr(endMonth, "yyyyMM"));
                    list = iRuleDataService.findHitRuleMostByMonthList(biRuleDataVO);
                } else if ("Y".equals(intervalType)) {//3-按年
                    Date endYear = new Date();
                    Date beginYear = DateUtil.addDate(endYear, intervalType,size);
                    biRuleDataVO.setStartYear(DateUtil.parseDateToStr(beginYear, "yyyy"));
                    biRuleDataVO.setEndYear(DateUtil.parseDateToStr(endYear, "yyyy"));
                    list = iRuleDataService.findHitRuleMostByYearList(biRuleDataVO);
                }*/ else {
                    ret.setMsg("查无数据");
                    ret.setCode("01");
                    return ret;
                }
            } else {//自定义时间
                String startDate_ = biRuleDataVO.getStartDate();
                String endDate_ = biRuleDataVO.getEndDate();
                if (StringUtils.isNotEmpty(startDate_) && StringUtils.isNotEmpty(endDate_)) {
                    List countDay=DateUtil.getDayListOfDate(startDate_,endDate_);
                    if(countDay.size()>select_limit_time){
                        ret.setMsg("自定义查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    biRuleDataVO.setStartYear(startDate_);
                    biRuleDataVO.setEndYear(endDate_);
                    //list = iRuleDataService.findHitRuleMostByDayList(biRuleDataVO);
                    list = biRuleDataDAO.findHitRuleMostByDayList(biRuleDataVO);
                } else {
                    ret.setMsg("查询开始时间或者结束时间不能为空");
                    ret.setCode("07");//参数错误
                    return ret;
                }
            }

            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("data", list);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("查询命中最多规则情况失败");
            return ret;
        }
        return ret;
    }
}
