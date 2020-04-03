package com.biz.credit.service;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.DNodeApiVO;

import java.util.List;

/**
 * 决策流api
 * @author yangjun
 */
public interface IDNodeApiService {

    RespEntity updateNodeApi(Long nodeId, List<DNodeApiVO> nodeApiList);
    List<DNodeApiVO> getDNodeApiList(DNodeApiVO dNodeApiVO);
}
