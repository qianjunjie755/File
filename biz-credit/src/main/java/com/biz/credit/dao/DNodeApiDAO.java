package com.biz.credit.dao;

import com.biz.credit.vo.DNodeApiVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DNodeApiDAO {


    void updateStatusByNodeId(@Param("nodeId") Long nodeId, @Param("status") Integer status);

    void insertList(@Param("nodeApiList") List<DNodeApiVO> nodeApiList);

    List<DNodeApiVO> queryList(@Param("dNodeApiVO") DNodeApiVO dNodeApiVO);
}
