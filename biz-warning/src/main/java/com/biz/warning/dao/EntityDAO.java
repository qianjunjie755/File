package com.biz.warning.dao;

import com.biz.warning.domain.*;
import com.biz.warning.vo.EntityVO;
import com.biz.warning.vo.TaskVO;
import com.biz.strategy.entity.EntityBasic;
import com.biz.strategy.entity.TaskInput;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EntityDAO {
    int addEntities(List<Entity> entityList) throws Exception;
    int updateEntityStatus(@Param("entityVO") EntityVO entityVO) throws Exception;
    int updateEntityParentAppIdAndStatus(@Param("entityVO") EntityVO entityVO)throws Exception;
    List<EntityVO> findListByEntityVOForTask(@Param("entityVO") EntityVO entityVO) throws Exception;
    int findCountByTaskId(@Param("taskId") Integer taskId) throws Exception;
    List<TaskInput> findTaskInputListByTaskId(@Param("taskId") Integer taskId) throws Exception;
    List<Entity> findEntitiesByCompanyNames(@Param("list") List<String> entityNames, @Param("taskId") Integer taskId) throws Exception;
    int deleteEntityVOList(@Param("list") List<EntityVO> entityVOList, @Param("userId") Integer userId) throws Exception;
    Set<String> findEntityNamesByTask(@Param("task") Task task) throws Exception;
    List<EntityVO> findListByEntityVO(@Param("entityVO") EntityVO entityVO) throws Exception;
    List<Entity> findAllEntities(@Param("apiCode") String apiCode, @Param("userIdList") List<Integer> userIdList)throws Exception;
    List<Entity> findEntities(@Param("apiCode") String apiCode, @Param("userIdList") List<Integer> userIdList, @Param("nameOrCode") String nameOrCode,
                              @Param("uploadTime") String uploadTime, @Param("expireTime") String expireTime)throws Exception;
    EntityVO findEntityDetail(@Param("entityName") String entityName, @Param("userId") String userId)throws Exception;
    List<TaskVO> findTasksByEntity(@Param("entityName") String entityName, @Param("apiCode") String apiCode, @Param("userIdList") List<Integer> userIdList) throws Exception;
    int deleteEntity(@Param("entityName") String entityName, @Param("userId") String userId)throws Exception;
    int deleteEntityByTask(@Param("entityName") String entityName, @Param("taskId") Integer taskId, @Param("apiCode") String apiCode, @Param("userIdList") List<Integer> userIdList)throws Exception;

    int updateHitCountByTaskInput(@Param("entityId") Integer entityId, @Param("hitCount") Integer hitCount);

    List<EntityVO> findHitEntityMostList(@Param("apiCode") String apiCode);

    List<EntityVO> findHitEntityMostListByUserIds(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds);

    List<EntityCount> countEntityAmountByApiCode(@Param("apiCode") String apiCode);

    List<EntityCount> countEntityAmountByUserIds(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds);

    List<HitTrendCount> countHitTrendByApiCode(@Param("apiCode") String apiCode, @Param("n") int n, @Param("unit") int unit);

    List<HitTrendCount> countHitTrendByUserIds(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds, @Param("n") int n, @Param("unit") int unit);

    String getFirstDateByUserIds(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds);

    String getFirstDateByApiCode(@Param("apiCode") String apiCode);

    int updateEntityBasicInfo(@Param("entityBasic") EntityBasic entityBasic);

    int insertEntityBasicInfo(@Param("entityBasic") EntityBasic entityBasic);

    EntityVO findEntityBasicInfoByName(@Param("entityName") String entityName);

    EntityVO findHistoryParam(@Param("entityName") String entityName, @Param("taskId") Long taskId, @Param("apiCode") String apiCode, @Param("userIdList") List<Integer> userIdList);

    List<HitTrendCount> countMonitorAndHitTrend(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds, @Param("beginDay") String beginDay, @Param("toDay") String toDay);


    List<HitTrendCount> countHitRuleCompany(@Param("apiCode") String apiCode, @Param("list") List<Integer> userIds, @Param("beginDay") LocalDate beginDay, @Param("toDay") LocalDateTime toDay);
}
