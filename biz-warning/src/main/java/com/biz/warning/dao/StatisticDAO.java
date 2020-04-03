package com.biz.warning.dao;

import com.biz.warning.domain.RiskSource;
import com.biz.warning.domain.TimeScope;
import com.biz.warning.domain.VariableDetail;
import com.biz.warning.vo.StatitscVO;
import com.biz.strategy.entity.EntityBasic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticDAO {
    List<TimeScope> queryTimeScope();

    void deleteVarDaily(@Param("date") String date);

    void statisticVarDaily(@Param("date") String date);

    void deleteVarNearly(@Param("type") String type);

    void statisticVarNearly(@Param("type") String type,
                            @Param("startTime") String startTime,
                            @Param("endTime") String endTime);

    List<StatitscVO> queryVarDailyByEntityId(@Param("date") String date, @Param("entityId") Integer entityId);

    Integer updateVarDailyByEntity(@Param("data") StatitscVO data);

    Integer insertVarDailyByEntity(@Param("data") List<StatitscVO> data);

    List<StatitscVO> queryVarNearlyByEntityId(@Param("type") String type,
                                              @Param("entityId") Integer entityId,
                                              @Param("startTime") String startTime,
                                              @Param("endTime") String endTime);

    Integer updateVarNearlyByEntity(@Param("data") StatitscVO data);

    Integer insertVarNearlyByEntity(@Param("data") List<StatitscVO> data);

    List<Integer> queryEntityIdByName(@Param("name") String companyName);

    List<RiskSource> queryRiskStatistic(@Param("apiCode") String apiCode,
                                        @Param("userIds") List<Integer> userIds,
                                        @Param("startTime") String startTime,
                                        @Param("endTime") String endTime);

    List<RiskSource> queryAllRiskByCompanyName(@Param("apiCode") String apiCode,
                                               @Param("userIds") List<Integer> userIds,
                                               @Param("entityIds") List<Integer> entityIds);

    Integer queryVariableType(@Param("warnId") long warnId);

    Integer countRiskDetails(@Param("warnId") long warnId);

    List<VariableDetail> queryRiskDetails(@Param("warnId") long warnId,
                                          @Param("pageNo") Integer pageNo,
                                          @Param("pageSize") Integer pageSize);

    EntityBasic queryEntityBasicInfoById(@Param("warnId") long warnId);

    EntityBasic queryEntityBasicInfoByName(@Param("warnId") long warnId);

    String queryAppId(@Param("warnId") Long warnId);

    String queryCaseDetail(@Param("warnId") Long warnId, @Param("caseId") String caseId);

    Integer insertCaseDetail(@Param("warnId") Long warnId, @Param("caseId") String caseId, @Param("content") String content);
}
