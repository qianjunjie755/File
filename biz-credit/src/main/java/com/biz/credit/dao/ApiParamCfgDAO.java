package com.biz.credit.dao;

import com.biz.credit.vo.ApiParamCfgVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiParamCfgDAO {
    List<ApiParamCfgVO> findApiParamCfgList();
}
