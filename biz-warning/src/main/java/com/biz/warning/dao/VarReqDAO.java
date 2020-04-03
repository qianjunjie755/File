package com.biz.warning.dao;

import com.biz.warning.vo.VarReqVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VarReqDAO {
    List<VarReqVO> findListForTaskSchedule(List<Long> taskIdList) throws Exception;
    List<VarReqVO> findListForTaskScheduleByTaskId(@Param("taskId") Long taskId) throws Exception;
    List<VarReqVO> findListForTemplateNameBuild(Long taskId) throws Exception;
}
