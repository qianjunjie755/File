package com.biz.credit.dao;

import com.biz.credit.domain.DNodeModel;
import com.biz.credit.domain.DNodeThreshold;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DNodeThresholdDAO {

    int insertList(@Param("nodeThresholdList") List<DNodeThreshold> nodeThresholdList);
    int insert(@Param("dNodeThreshold") DNodeThreshold dNodeThreshold);
    List<DNodeThreshold> queryByNodeThreshold(@Param("dNodeModel") DNodeModel dNodeModel);
    //List<DNodeThreshold> findByNodeThreshold(@Param("dNodeModel")DNodeModel dNodeModel);
    List<DNodeThreshold> queryListByNodeIdAndType(@Param("nodeId") Long nodeId, @Param("modelType") int modelType);

    int deleteByModelId(@Param("modelId") Long modelId);
}
