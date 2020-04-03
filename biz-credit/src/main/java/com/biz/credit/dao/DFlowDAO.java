package com.biz.credit.dao;

import com.biz.credit.vo.DFlowVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DFlowDAO {
    List<DFlowVO> findDFlowVOList(@Param("dFlowVO") DFlowVO dFlowVO);

    int insert(@Param("flow") DFlowVO flowVO);

    int update(@Param("flow") DFlowVO flowVO);

    List<DFlowVO> queryList(@Param("flow") DFlowVO flowVO);

    DFlowVO getById(@Param("flowId") Long flowId);

    int queryCountByName(@Param("flowName") String flowName, @Param("apiCode") String apiCode);
}
