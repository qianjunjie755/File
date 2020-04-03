package com.biz.credit.dao;

import com.biz.credit.domain.DNode;
import com.biz.credit.domain.DNodeParam;
import com.biz.credit.vo.DNodeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DNodeDAO {
    int insertNodeBasic(@Param("dNode") DNode dNode);
    int updateNodeBasic(@Param("dNode") DNode dNode);

    int updateStatusByFlowId(@Param("flowId") Long flowId, @Param("status") Integer status);

    List<DNodeVO> queryListByFlowId(@Param("flowId") Long flowId);

    int updateStatusById(@Param("nodeId") Long nodeId, @Param("status") Integer status);
    List<DNodeParam> queryInPararms(@Param("nodeId") Long nodeId);
    int insertInParams(@Param("dNodeParam") DNodeParam dNodeParam);

    List<DNodeParam> queryParamsList(@Param("apiProdCode") String apiProdCode, @Param("apiVersion") String apiVersion);

    DNode queryById(@Param("nodeId") Long nodeId);
    int updateParam(@Param("nodeId") Long nodeId);

    int queryCount(@Param("nodeVO") DNodeVO nodeVO);
}
