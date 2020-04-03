package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.DNodeParam;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.DNodeVO;
import com.biz.credit.vo.FlowParamConfigVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface IDNodeService {

    RespEntity saveNode(DNodeVO nodeVO);

    void invalidByFlowId(Long flowId);

    List<DNodeVO> getListByFlowId(@Param("flowId") Long flowId);

    void invalidById(Long nodeId);


    /**
     * 根据节点id 查入参
     * @param nodeId
     * @return
     */
    List<DNodeParam> queryInPararms(Long nodeId);
    /**
     * 根据节点id 更新入参
     * @param dNodeParamList
     * @return
     */
    RespEntity saveInPrarams(Long nodeId, List<DNodeParam> dNodeParamList);
    /**
     * 切换tab 查入参
     * @param flowParamConfigVO
     * @return
     */
    Set<DNodeParam> queryNodePrarams(FlowParamConfigVO flowParamConfigVO);
    /**
     *  入参
     * @param flowParamConfigVO
     * @return
     */
    Set<DNodeParam> getPrarams(FlowParamConfigVO flowParamConfigVO);

    /**
     * 删除节点
     * @param nodeId
     * @return
     */
    RespEntity deleteNode(Long nodeId);

    JSONObject getNodeDetail(Long nodeId, String userId, String apiCode);
}
