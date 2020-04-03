package com.biz.credit.dao;

import com.biz.credit.domain.DNodeRuleVarRef;
import com.biz.credit.vo.DNodeRuleVO;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.NodeRuleConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DNodeRuleVarRefDAO {
    int updateStatusByNodeRuleVarId(@Param("varId") Long varId, @Param("status") Integer status);

    void insertList(@Param("refVarList") List<DNodeRuleVarRef> refVarList);

    //List<DNodeRuleVarRefVO> queryAllValidRefVars();

    List<DNodeRuleVarRefVO> queryAllSrcRefVars();

    List<DNodeRuleVarRefVO> queryInstanceRefVars(@Param("nodeRule") DNodeRuleVO dNodeRuleVO);


    void updateList(List<DNodeRuleVarRef> refVarList);

    List<DNodeRuleVarRefVO> querySrcRefVars(@Param("refPId")String varId);

}
