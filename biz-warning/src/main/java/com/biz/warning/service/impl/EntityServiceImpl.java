package com.biz.warning.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.dao.EntityDAO;
import com.biz.warning.dao.RuleDAO;
import com.biz.warning.dao.TaskDAO;
import com.biz.warning.domain.Entity;
import com.biz.warning.domain.EntityCount;
import com.biz.warning.domain.HitTrendCount;
import com.biz.warning.domain.Task;
import com.biz.warning.service.IEntityService;
import com.biz.warning.util.ExcelUtil;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.EntityVO;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.TaskVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EntityServiceImpl implements IEntityService {
    @Resource
    private TaskDAO taskDAO;
    @Resource
    private EntityDAO entityDAO;
    @Resource
    private RuleDAO ruleDAO;

    @Override
    public Workbook getWorkbookByTask(TaskVO task) throws Exception {
        TaskVO taskRet = taskDAO.findByTask(task);
        Workbook workbook = ExcelUtil.createWorkBookByHeaders(taskRet.getEntityTemplateName().split("_"));
        return workbook;
    }

    @Override
    public List<Entity> saveEntityList(List<Entity> entityList) throws Exception {
/*        Map<String,Entity> entityMap = new HashMap<>();
        entityList.forEach(entity -> {
            entityMap.put(entity.getEntityName(),entity);
        });
        List<String> entityNames = new ArrayList<>();
        entityMap.keySet().forEach(entityName->{
            entityNames.add(entityName);
        });
        List<Entity> retList = entityDAO.findEntitiesByCompanyNames(entityNames,taskId);
        retList.forEach(entity -> {
            entityMap.remove(entity.getEntityName());
        });
        entityMap.values().forEach(entity->{
            retEntityList.add(entity);
        });*/
        if(!CollectionUtils.isEmpty(entityList)){
            Integer taskId = entityList.get(0).getTaskId().intValue();
            Map<String,Entity> entityMap = new HashMap<>();
            entityList.forEach(entity -> {
                entityMap.put(entity.getEntityName(),entity);
            });
            List<String> entityNames = new ArrayList<>();
            entityMap.keySet().forEach(entityName->{
                entityNames.add(entityName);
            });
            List<Entity> retList = entityDAO.findEntitiesByCompanyNames(entityNames,taskId);
            retList.forEach(entity -> {
                if(entityMap.containsKey(entity.getCompanyName())
                        &&StringUtils.isEmpty(entityMap.get(entity.getCompanyName()).getParentAppId())
                        &&StringUtils.isNotEmpty(entity.getParentAppId())){
                    entityMap.get(entity.getCompanyName()).setParentAppId(entity.getParentAppId());
                }
            });
            entityDAO.addEntities(entityList);
        }
        return entityList;
    }

    @Override
    public int deleteEntityVOList(List<EntityVO> entityVOList,Integer userId) throws Exception {
/*        StringBuilder stringBuilder = new StringBuilder();
        entityVOList.forEach(entityVO -> {
            stringBuilder.append(entityVO.getEntityId()).append(",");
        });*/

        return entityDAO.deleteEntityVOList(entityVOList,userId);
    }


    @Override
    public Set<String> findEntityNamesByTask(Task task) throws Exception {
        return  entityDAO.findEntityNamesByTask(task);
    }

    @Override
    public List<EntityVO> findList(EntityVO entityVO) throws Exception {
        return entityDAO.findListByEntityVO(entityVO);
    }

    @Override
    public List<Entity> findAllEntities(String apiCode,List<Integer> userIdList)throws Exception{
        return entityDAO.findAllEntities(apiCode,userIdList);
    }

    @Override
    public List<Entity> findEntities(String apiCode, List<Integer> userIdList, String nameOrCode, String uploadTime, String expireTime) throws Exception {
        return entityDAO.findEntities(apiCode,userIdList,nameOrCode,uploadTime,expireTime);
    }

    @Override
    public EntityVO findEntityDetail(String entityName,HttpServletRequest request,String userId) throws Exception {
        /*EntityVO vo = entityDAO.findEntityDetail(entityName,userId);
        if(vo != null){
            vo.setRealName(authService.getUserName(request.getHeader("cookie"), vo.getUserId().intValue()));
        }*/
        EntityVO vo = entityDAO.findEntityBasicInfoByName(entityName);
        return vo;
    }

    @Override
    public List<TaskVO> findTasksByEntity(String entityName,String apiCode,List<Integer> userIdList) throws Exception {
        return entityDAO.findTasksByEntity(entityName,apiCode,userIdList);
    }

    @Override
    public int deleteEntity(String entityName,String userId) throws Exception {
        return entityDAO.deleteEntity(entityName,userId);
    }

    @Override
    public int deleteEntityByTask(String entityName, Integer taskId,String apiCode,List<Integer> userIdList) throws Exception {
        return entityDAO.deleteEntityByTask(entityName,taskId,apiCode,userIdList);
    }


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
                beginDate = endDate.minusDays(n);
            } else if (u == 'w' || u == 'W') {
                unit = 2; //周
                beginDate = endDate.minusWeeks(n);
            } else if (u == 'm' || u == 'M') {
                unit = 3; //月
                beginDate = endDate.minusMonths(n);
            } else if (u == 'y' || u == 'Y') {
                unit = 4; //年
                beginDate = endDate.minusYears(n);
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
    public Map<String, Object> findHistoryParam(String entityName, Long taskId, String apiCode,List<Integer> userIdList) {
        EntityVO entityVO =  entityDAO.findHistoryParam(entityName, taskId, apiCode,userIdList);
        TaskVO taskVO = new TaskVO(taskId);
        Map<String,Object> result = new LinkedHashMap<>();
        taskVO.setApiCode(apiCode);
        try {
            TaskVO task = taskDAO.findByTask(taskVO);
            String entityTemplateName = task.getEntityTemplateName();
            String[] entityTemplateNameArr = entityTemplateName.split("_");
            for (String paramName : entityTemplateNameArr) {
                if(SysDict.companyName.equals(paramName)){
                    result.put(SysDict.companyName,entityVO.getCompanyName());
                }else if(SysDict.idNumber.equals(paramName)){
                    result.put(SysDict.idNumber,entityVO.getPersonId());
                }else if(SysDict.legalPerson.equals(paramName)){
                    result.put(SysDict.legalPerson,entityVO.getLegalPerson());
                }else if(SysDict.cellPhone.equals(paramName)){
                    result.put(SysDict.cellPhone,entityVO.getCell());
                }else if(SysDict.creditCode.equals(paramName)){
                    result.put(SysDict.creditCode,entityVO.getCreditCode());
                }else if(SysDict.bankId.equals(paramName)){
                    result.put(SysDict.bankId,entityVO.getBankId());
                }else if(SysDict.ENTITY_APPLICATION_DATE.equals(paramName)){
                    result.put(SysDict.ENTITY_APPLICATION_DATE,entityVO.getApplicationDate());
                }else if(SysDict.ENTITY_EXPIRE_TIME_NAME.equals(paramName)){
                    result.put(SysDict.ENTITY_EXPIRE_TIME_NAME,entityVO.getExpireTime());
                }else if (SysDict.homeAddress.equals(paramName)){
                    result.put(SysDict.homeAddress,entityVO.getHomeAddr());
                }else if (SysDict.bizAddress.equals(paramName)){
                    result.put(SysDict.bizAddress,entityVO.getBizAddr());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
