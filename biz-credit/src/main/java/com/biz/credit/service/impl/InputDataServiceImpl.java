package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.BiInputDataDAO;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.domain.responseData.BiInputDataRes;
import com.biz.credit.domain.responseData.BiInputDataScoreRes;
import com.biz.credit.service.IInputDataService;
import com.biz.credit.utils.DateUtil;
import com.biz.credit.vo.BiInputDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class InputDataServiceImpl implements IInputDataService {
    @Autowired
    private BiInputDataDAO biInputDataDAO;
    @Value("${select_limit_time}")
    private Integer select_limit_time;
    @Override
    public List<BiInputDataRes> findBiInputDataByMonthList(BiInputDataVO biInputDataVO) throws  Exception{
        return biInputDataDAO.findBiInputDataByMonthList(biInputDataVO);
    }

    @Override
    public RespEntity findBiInputDataByDayList(BiInputDataVO biInputDataVO) throws  Exception{
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = biInputDataVO.getApiCode();
        if (StringUtils.isEmpty(apiCode)) {
            ret.setMsg("apiCode不能为空");
            return ret;
        }
        try {
            List<BiInputDataRes> list = new ArrayList();
            if (StringUtils.isNotEmpty(biInputDataVO.getInterval())) {
                String interval = biInputDataVO.getInterval();//查询周期
                String intervalType = interval.substring(interval.length() - 1, interval.length());
                int size = Integer.parseInt(interval.substring(0, interval.length()-1));
                if ("D".equals(intervalType)) {//1-按天
                    if (size>select_limit_time){
                        ret.setMsg("查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    Date endDate = new Date();
                    Date beginDate = DateUtil.addDate(endDate, intervalType,size);

                    biInputDataVO.setStartDate(DateUtil.parseDateToStr(beginDate, "yyyy-MM-dd"));
                    biInputDataVO.setEndDate(DateUtil.parseDateToStr(endDate, "yyyy-MM-dd"));
                    list = biInputDataDAO.findBiInputDataByDayList(biInputDataVO);
                } /*else if ("M".equals(intervalType)) {//2-按月
                    Date endMonth = new Date();
                    Date beginMonth = DateUtil.addDate(endMonth, intervalType,size);
                    biInputDataVO.setStartMonth(DateUtil.parseDateToStr(beginMonth, "yyyyMM"));
                    biInputDataVO.setEndMonth(DateUtil.parseDateToStr(endMonth, "yyyyMM"));
                    list = inputDataService.findBiInputDataByMonthList(biInputDataVO);
                } else if ("Y".equals(intervalType)) {//3-按年
                    Date endYear = new Date();
                    Date beginYear = DateUtil.addDate(endYear, intervalType,size);
                    biInputDataVO.setStartYear(DateUtil.parseDateToStr(beginYear, "yyyy"));
                    biInputDataVO.setEndYear(DateUtil.parseDateToStr(endYear, "yyyy"));
                    list = inputDataService.findBiInputDataByYearList(biInputDataVO);
                }*/ else {
                    ret.setMsg("查无数据");
                    ret.setCode("01");
                    return ret;
                }
            } else {//自定义查询时间
                String startDate_ = biInputDataVO.getStartDate();
                String endDate_ = biInputDataVO.getEndDate();
                if (StringUtils.isNotEmpty(startDate_) && StringUtils.isNotEmpty(endDate_)) {
                    List countDay=DateUtil.getDayListOfDate(startDate_,endDate_);
                    if(countDay.size()>select_limit_time){
                        ret.setMsg("自定义查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    biInputDataVO.setStartYear(startDate_);
                    biInputDataVO.setEndYear(endDate_);
                    list = biInputDataDAO.findBiInputDataByDayList(biInputDataVO);
                } else {
                    ret.setMsg("查询开始时间或者结束时间不能为空");
                    ret.setCode("07");//参数错误
                    return ret;
                }
            }

            List data_x = new ArrayList();
            List data_y = new ArrayList();
            for (BiInputDataRes vo : list) {
                data_x.add(vo.getDate_());//组织X轴数据 -数组
                data_y.add(vo.getCount_());//组织Y轴数据 -数组
            }
            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("data_x", data_x);
            jo.put("data_y", data_y);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("查询总体进件数量趋势失败");
            return ret;
        }
        return ret;
    }

    @Override
    public List<BiInputDataRes> findBiInputDataByYearList(BiInputDataVO biInputDataVO) throws Exception {
        return biInputDataDAO.findBiInputDataByYearList(biInputDataVO);
    }

    @Override
    public RespEntity findCompanyScoreByDayList(BiInputDataVO biInputDataVO) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = biInputDataVO.getApiCode();
        if (StringUtils.isEmpty(apiCode)) {
            ret.setMsg("apiCode不能为空");
            return ret;
        }
        try {
            List<BiInputDataRes> list = new ArrayList();
            if (StringUtils.isNotEmpty(biInputDataVO.getInterval())) {
                String interval = biInputDataVO.getInterval();//查询周期
                String intervalType = interval.substring(interval.length() - 1, interval.length());
                int size = Integer.parseInt(interval.substring(0, interval.length()-1));
                if ("D".equals(intervalType)) {//1-按天
                    if (size>select_limit_time){
                        ret.setMsg("查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    Date endDate = new Date();
                    Date beginDate = DateUtil.addDate(endDate, intervalType,size);

                    biInputDataVO.setStartDate(DateUtil.parseDateToStr(beginDate, "yyyy-MM-dd"));
                    biInputDataVO.setEndDate(DateUtil.parseDateToStr(endDate, "yyyy-MM-dd"));
                    // list = inputDataService.findCompanyScoreByDayList(biInputDataVO);
                    list = biInputDataDAO.findCompanyScoreByDayList(biInputDataVO);
                }/* else if ("M".equals(intervalType)) {//2-按月
                    Date endMonth = new Date();
                    Date beginMonth = DateUtil.addDate(endMonth, intervalType,size);
                    biInputDataVO.setStartMonth(DateUtil.parseDateToStr(beginMonth, "yyyyMM"));
                    biInputDataVO.setEndMonth(DateUtil.parseDateToStr(endMonth, "yyyyMM"));
                    list = inputDataService.findCompanyScoreByMonthList(biInputDataVO);
                } else if ("Y".equals(intervalType)) {//3-按年
                    Date endYear = new Date();
                    Date beginYear = DateUtil.addDate(endYear, intervalType,size);
                    biInputDataVO.setStartYear(DateUtil.parseDateToStr(beginYear, "yyyy"));
                    biInputDataVO.setEndYear(DateUtil.parseDateToStr(endYear, "yyyy"));
                    list = inputDataService.findCompanyScoreByYearList(biInputDataVO);
                } */else {
                    ret.setMsg("查无数据");
                    ret.setCode("01");
                    return ret;
                }
            } else {//自定义查询时间
                String startDate_ = biInputDataVO.getStartDate();
                String endDate_ = biInputDataVO.getEndDate();
                if (StringUtils.isNotEmpty(startDate_) && StringUtils.isNotEmpty(endDate_)) {
                    List countDay = DateUtil.getDayListOfDate(startDate_,endDate_);
                    if (countDay.size()>select_limit_time)
                    {
                        ret.setMsg("自定义查询时间不能超过"+select_limit_time+"天");
                        return ret;
                    }
                    biInputDataVO.setStartYear(startDate_);
                    biInputDataVO.setEndYear(endDate_);
                    // list = inputDataService.findCompanyScoreByDayList(biInputDataVO);
                    list = biInputDataDAO.findCompanyScoreByDayList(biInputDataVO);
                } else {
                    ret.setMsg("查询开始时间或者结束时间不能为空");
                    ret.setCode("07");//参数错误
                    return ret;
                }
            }

            List data_x = new ArrayList();
            List data_y = new ArrayList();
            for (BiInputDataRes vo : list) {
                data_x.add(vo.getDate_());//组织X轴数据 -数组
                data_y.add(vo.getCount_());//组织Y轴数据 -数组
            }
            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("data_x", data_x);
            jo.put("data_y", data_y);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("查询参与评分进件情况失败");
            return ret;
        }
        return ret;
    }

    @Override
    public RespEntity findCompanyScoreIntervalByDayList(BiInputDataVO biInputDataVO) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = biInputDataVO.getApiCode();
        if (StringUtils.isEmpty(apiCode)) {
            ret.setMsg("apiCode不能为空");
            return ret;
        }
        try {
            List<BiInputDataScoreRes> list = new ArrayList();
            List data_x = new ArrayList();
            List data_y = new ArrayList();
            int totalRefuse = 0;
            int totalReconside = 0;
            int totalAggree = 0;

            Integer moduleTypeId = biInputDataVO.getModuleTypeId();//模板ID
            List<BiInputDataVO> moduleTypeIdList = new ArrayList<>();
            if (moduleTypeId == null){
                //根据apicode查询模板列表
                moduleTypeIdList = biInputDataDAO.findModuleTypeIdListByApicode(apiCode);
            }else {
                moduleTypeIdList.add(biInputDataVO);
            }
            for(BiInputDataVO vo : moduleTypeIdList){
                biInputDataVO.setModuleTypeId(vo.getModuleTypeId());
                if (StringUtils.isNotEmpty(biInputDataVO.getInterval())) {
                    String interval = biInputDataVO.getInterval();//查询周期
                    String intervalType = interval.substring(interval.length() - 1, interval.length());
                    int size = Integer.parseInt(interval.substring(0, interval.length()-1));
                    if ("D".equals(intervalType)) {//1-按天
                        if (size>select_limit_time){
                            ret.setMsg("查询时间不能超过"+select_limit_time+"天");
                            return ret;
                        }
                        Date endDate = new Date();
                        Date beginDate = DateUtil.addDate(endDate, intervalType,size);

                        biInputDataVO.setStartDate(DateUtil.parseDateToStr(beginDate, "yyyy-MM-dd"));
                        biInputDataVO.setEndDate(DateUtil.parseDateToStr(endDate, "yyyy-MM-dd"));
                        // list = inputDataService.findCompanyScoreIntervalByDayList(biInputDataVO);
                        list = biInputDataDAO.findCompanyScoreIntervalByDayList(biInputDataVO);
                    }/* else if ("M".equals(intervalType)) {//2-按月
                    Date endMonth = new Date();
                    Date beginMonth = DateUtil.addDate(endMonth, intervalType,size);
                    biInputDataVO.setStartMonth(DateUtil.parseDateToStr(beginMonth, "yyyyMM"));
                    biInputDataVO.setEndMonth(DateUtil.parseDateToStr(endMonth, "yyyyMM"));
                    list = inputDataService.findCompanyScoreByMonthList(biInputDataVO);
                } else if ("Y".equals(intervalType)) {//3-按年
                    Date endYear = new Date();
                    Date beginYear = DateUtil.addDate(endYear, intervalType,size);
                    biInputDataVO.setStartYear(DateUtil.parseDateToStr(beginYear, "yyyy"));
                    biInputDataVO.setEndYear(DateUtil.parseDateToStr(endYear, "yyyy"));
                    list = inputDataService.findCompanyScoreByYearList(biInputDataVO);
                } */else {
                        ret.setMsg("查无数据");
                        ret.setCode("01");
                        return ret;
                    }
                } else {//自定义查询时间
                    String startDate_ = biInputDataVO.getStartDate();
                    String endDate_ = biInputDataVO.getEndDate();
                    if (StringUtils.isNotEmpty(startDate_) && StringUtils.isNotEmpty(endDate_)) {
                        biInputDataVO.setStartYear(startDate_);
                        biInputDataVO.setEndYear(endDate_);
                        // list = inputDataService.findCompanyScoreIntervalByDayList(biInputDataVO);
                        list =biInputDataDAO.findCompanyScoreIntervalByDayList(biInputDataVO);
                    } else {
                        ret.setMsg("查询开始时间或者结束时间不能为空");
                        ret.setCode("07");//参数错误
                        return ret;
                    }
                }

                for (BiInputDataScoreRes scoreRes : list) {
                    if("1".equals(scoreRes.getInterval_())){//通过
                        totalAggree+=Integer.parseInt(scoreRes.getCount_());
                    }else if("2".equals(scoreRes.getInterval_())){//拒绝
                        totalRefuse+=Integer.parseInt(scoreRes.getCount_());
                    }else if("3".equals(scoreRes.getInterval_())){//复议
                        totalReconside+=Integer.parseInt(scoreRes.getCount_());
                    }
                }
            }

            //按顺序add
            data_x.add("拒绝");//组织X轴数据 -数组
            data_y.add(totalRefuse);//组织Y轴数据 -数组
            data_x.add("复议");//组织X轴数据 -数组
            data_y.add(totalReconside);//组织Y轴数据 -数组
            data_x.add("通过");//组织X轴数据 -数组
            data_y.add(totalAggree);//组织Y轴数据 -数组

            JSONObject jo = new JSONObject();
            jo.put("total", data_x.size());
            jo.put("data_x", data_x);
            jo.put("data_y", data_y);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("查询参与评分进件情况失败");
            return ret;
        }
        return ret;
    }
}
