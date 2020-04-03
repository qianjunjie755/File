package com.biz.warning.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.Entity;
import com.biz.warning.domain.EntityCount;
import com.biz.warning.domain.Task;
import com.biz.warning.vo.EntityVO;
import com.biz.warning.vo.TaskVO;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IEntityService {
    Workbook getWorkbookByTask(TaskVO task) throws Exception;
    List<Entity> saveEntityList(List<Entity> entityList) throws Exception;
    int deleteEntityVOList(List<EntityVO> entityVOList, Integer userId) throws Exception;
    Set<String> findEntityNamesByTask(Task task) throws Exception;
    List<EntityVO> findList(EntityVO entityVO) throws Exception;
    List<Entity> findAllEntities(String apiCode, List<Integer> userIdList)throws Exception;
    List<Entity> findEntities(String apiCode, List<Integer> userIdList, String nameOrCode, String uploadTime, String expireTime)throws Exception;
    EntityVO findEntityDetail(String entityName, HttpServletRequest request, String userId)throws Exception;
    List<TaskVO> findTasksByEntity(String entityName, String apiCode, List<Integer> userIdList)throws Exception;
    int deleteEntity(String entityName, String userId)throws Exception;
    int deleteEntityByTask(String entityName, Integer taskId, String apiCode, List<Integer> userIdList)throws Exception;

    Map findHitEntityPointMostList(String apiCode, List<Integer> userIds);

    List<EntityCount> countEntityAmount(String apiCode, List<Integer> userIds);

    JSONObject countHitTrend(String apiCode, List<Integer> userIds, String type);

    Map<String,Object> findHistoryParam(String entityName, Long taskId, String apiCode, List<Integer> userIdList);
}
