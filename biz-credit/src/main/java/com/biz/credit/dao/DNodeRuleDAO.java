package com.biz.credit.dao;

import com.biz.credit.vo.DNodeRuleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DNodeRuleDAO {
    List<DNodeRuleVO> queryNodeRuleList(@Param("nodeRuleVO") DNodeRuleVO dNodeRuleVO);

    DNodeRuleVO queryNodeRuleById(@Param("nodeRuleId") Long nodeRuleId);

    void updateStatusByModelId(@Param("modelId") Long modelId, @Param("status") Integer status);

    List<DNodeRuleVO> queryListByModelId(@Param("modelId") Long modelId);

    void insertDNodeRule(@Param("nodeRule") DNodeRuleVO dNodeRuleVO);
}
