package com.biz.credit.dao;

import com.biz.credit.vo.DFlowVO;
import com.biz.credit.vo.DNodeParamsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DNodeParamsDAO {
    List<DNodeParamsVO> findDNodeParamsVOListByDFlowVO(@Param("dFlowVO") DFlowVO dFlowVO);
}
