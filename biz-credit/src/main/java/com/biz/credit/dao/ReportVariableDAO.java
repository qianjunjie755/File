package com.biz.credit.dao;

import com.biz.credit.vo.ReportVariableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportVariableDAO {
    List<ReportVariableVO> findListByVariableVO(@Param("variableVO") ReportVariableVO reportVariableVO) throws Exception;
    List<ReportVariableVO> findListByVariableVOForApiTask(@Param("variableVO") ReportVariableVO reportVariableVO) throws Exception;
    List<ReportVariableVO> findStrongRuleVariableList() throws Exception;
    int updateVariable(@Param("variableVO") ReportVariableVO reportVariableVO) throws Exception;
}
