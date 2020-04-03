package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.BiInputDataDAO;
import com.biz.credit.dao.UserGroupDAO;
import com.biz.credit.domain.UserGroup;
import com.biz.credit.service.IInputGroupTendencyService;
import com.biz.credit.vo.BiInputByGroupVO;
import com.biz.credit.vo.BiJsonResultVO;
import com.biz.credit.vo.BiReportQueryCriteriaVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InputGroupTendencyServiceImpl implements IInputGroupTendencyService {
    @Autowired
    private UserGroupDAO userGroupDAO;
    @Autowired
    private BiInputDataDAO biInputDataDAO;
    @Override
    public BiJsonResultVO findBiJsonResultByBiReportQueryCriteriaVOFromMysSql(BiReportQueryCriteriaVO criteriaVO) throws Exception {
        BiJsonResultVO resultVO = new BiJsonResultVO();
        Map<Integer, UserGroup> groupNameMapAll = userGroupDAO.findUserGroupNameMapByApiCode(criteriaVO.getApiCode());
        if(0==criteriaVO.getGroupId()){
            UserGroup userGroup = new UserGroup();
            userGroup.setId(-1);
            userGroup.setName("超级管理员");
            groupNameMapAll.put(userGroup.getId(),userGroup);
        }else if(-1==criteriaVO.getGroupId()){
            if(MapUtils.isEmpty(groupNameMapAll)){
                groupNameMapAll = new HashMap<>();
            }
            UserGroup userGroup = new UserGroup();
            userGroup.setId(-1);
            userGroup.setName("超级管理员");
            groupNameMapAll.put(userGroup.getId(),userGroup);
        }
        String startDateStr = criteriaVO.getStartDate();
        String endDateStr = criteriaVO.getEndDate();
        if(StringUtils.isNotEmpty(criteriaVO.getInterval())){
            DateTime now = DateTime.now();
            Integer days = Integer.parseInt(criteriaVO.getInterval().substring(0,criteriaVO.getInterval().length()-1));
            endDateStr = now.toString("yyyy-MM-dd");
            startDateStr = now.minusDays(days).toString("yyyy-MM-dd");
        }
        criteriaVO.setStartDate(startDateStr);
        criteriaVO.setEndDate(endDateStr);
        DateTime startDate = DateTime.parse(startDateStr);
        DateTime endDate = DateTime.parse(endDateStr);
        JSONArray xDataList = new JSONArray();
        JSONObject xData = new JSONObject();
        xData.put("id",1);
        JSONArray xAxis = new JSONArray();
        while(endDate.getMillis()>=startDate.getMillis()){
            xAxis.add(startDate.toString("yyyy-MM-dd"));
            startDate = startDate.plusDays(1);
        }
        xData.put("xAxis",xAxis);
        xDataList.add(xData);
        resultVO.setXAxisData(xDataList);
        Map<Integer,JSONObject> groupDataMap = new HashMap<>();
        JSONArray groupDataArray = new JSONArray();
        if(null!=criteriaVO.getGroupId()&&criteriaVO.getGroupId()>0){
            UserGroup userGroup =  groupNameMapAll.get(criteriaVO.getGroupId());
            JSONObject groupData = new JSONObject();
            groupData.put("id",userGroup.getId());
            groupData.put("groupId",userGroup.getId());
            groupData.put("groupName",userGroup.getName());
            JSONArray yAxis = new JSONArray();
            xAxis.forEach(date->{
                yAxis.add(0);
            });
            groupData.put("groupData",yAxis);
            groupDataMap.put(userGroup.getId(),groupData);
        }else{
            groupNameMapAll.forEach((groupId,userGroup)->{
                JSONObject groupData = new JSONObject();
                groupData.put("id",userGroup.getId());
                groupData.put("groupId",userGroup.getId());
                groupData.put("groupName",userGroup.getName());
                JSONArray yAxis = new JSONArray();
                xAxis.forEach(date->{
                    yAxis.add(0);
                });
                groupData.put("groupData",yAxis);
                groupDataMap.put(userGroup.getId(),groupData);
                log.info("groupNameMapAll["+userGroup.getId()+"]:"+groupData);
            });


        }
        List<BiInputByGroupVO> list = biInputDataDAO.findBiInputDataListByGroupCondition(criteriaVO);

        list.forEach(biInputByGroupVO -> {
            log.info("biInputByGroupVO:"+biInputByGroupVO);
            if(null!=biInputByGroupVO.getGroupId()&&StringUtils.isNotEmpty(biInputByGroupVO.getApiCode())){
                int index = xAxis.indexOf(biInputByGroupVO.getDate());
                JSONObject groupData = groupDataMap.get(biInputByGroupVO.getGroupId());
                if (groupData != null) {
                    JSONArray yAxis = groupData.getJSONArray("groupData");
                    yAxis.remove(index);
                    yAxis.add(index, biInputByGroupVO.getCount());
                    groupData.put("groupData", yAxis);
                }

            }
        });
        groupDataMap.values().forEach(groupData->{
            groupDataArray.add(groupData);
        });
        resultVO.setYAxisData(groupDataArray);
        //list.forEach();
        /*BiJsonResultVO resultVO = new BiJsonResultVO();
        Set<String> yAxisDataSet = new TreeSet<>();
        JSONArray xAxisDataArray = new JSONArray();
        for(int i = 0 ;i<list.size(); i++){
            yAxisDataSet.add(biInputByGroupVO.getDate());
            xAxisDataArray.
        }
        list.forEach(biInputByGroupVO -> {
            yAxisDataSet.add(biInputByGroupVO.getDate());
            xAxisDataArray.add()
        });*/
        log.info("inputGroupTendencyResultVO:"+resultVO.toString());
        return resultVO;
    }
}
