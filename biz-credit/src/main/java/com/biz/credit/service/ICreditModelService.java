package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.ApiVO;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.NodeCreditModelConfig;

import java.util.List;

public interface ICreditModelService {
    /**
     *查询信用模型api
     * @param nodeConfigVO
     * @return
     */
    List<JSONObject> queryCreditModelConfig(DNodeConfigVO nodeConfigVO);
    /**
     *保存信用模型
     * @param nodeId
     * @param nodeCreditModelConfig
     * @param isNew
     * @return
     */
    RespEntity saveCreditModelConfig(Long nodeId, NodeCreditModelConfig nodeCreditModelConfig, boolean isNew);
    List<ApiVO> queryCreditModelApiList(Integer creditModelId);
}
