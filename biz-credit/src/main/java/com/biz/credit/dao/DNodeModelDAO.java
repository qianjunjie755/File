package com.biz.credit.dao;

import com.biz.credit.domain.DNodeModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DNodeModelDAO {
    int insert(@Param("dNodeModel") DNodeModel dNodeModel);

    DNodeModel queryNodeModel(@Param("dNodeModel") DNodeModel dNodeModel);
    int queryNodeModelChoose(@Param("dNodeModel") DNodeModel dNodeModel);

    int updateStatusByNodeIdAndType(@Param("nodeId") Long nodeId, @Param("modelType") Integer modelType, @Param("status") Integer status);
}
