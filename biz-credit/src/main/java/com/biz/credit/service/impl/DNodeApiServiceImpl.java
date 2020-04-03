package com.biz.credit.service.impl;

import com.biz.credit.dao.ApiDAO;
import com.biz.credit.dao.DNodeApiDAO;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDNodeApiService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.DNodeApiVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DNodeApiServiceImpl implements IDNodeApiService {

    @Autowired
    private DNodeApiDAO nodeApiDAO;

    @Autowired
    private ApiDAO apiDAO;

    @Override
    @Transactional
    public RespEntity updateNodeApi(Long nodeId,List<DNodeApiVO> nodeApiVOList){
        if(nodeId == null){
            return RespEntity.error().setMsg("所选节点不能为空");
        }
        //失效关联数据
        nodeApiDAO.updateStatusByNodeId(nodeId, Constants.COMMON_STATUS_INVALID);
        //保存新数据
        List<DNodeApiVO> nodeApiList = new ArrayList<>();
        for (DNodeApiVO nodeApiVO : nodeApiVOList) {
            DNodeApiVO nodeApi = new DNodeApiVO();
            nodeApi.setApiProdCode(nodeApiVO.getApiProdCode());
            nodeApi.setApiVersion(nodeApiVO.getApiVersion());
            nodeApi.setNodeId(nodeId);
            nodeApi.setStatus(Constants.COMMON_STATUS_VALID);
            nodeApiList.add(nodeApi);
        }
        if(!CollectionUtils.isEmpty(nodeApiList)){
            nodeApiDAO.insertList(nodeApiList);
        }
        return RespEntity.success();
    }

    @Override
    public List<DNodeApiVO> getDNodeApiList(DNodeApiVO dNodeApiVO) {
        return nodeApiDAO.queryList(dNodeApiVO);
    }
}
