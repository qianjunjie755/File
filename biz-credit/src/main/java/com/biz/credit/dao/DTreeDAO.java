package com.biz.credit.dao;

import com.biz.credit.domain.*;
import com.biz.credit.vo.DTreeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DTreeDAO {

    int insert(@Param("dTreeVO") DTreeVO dTreeVO);
    int update(@Param("dTreeVO") DTreeVO dTreeVO);
    DTreeVO queryById(@Param("treeId") Long treeId);
    List<DTree> queryVersionListByTreeName(@Param("dTreeVO") DTreeVO dTreeVO);
    int existTreeName(@Param("treeName") String treeName, @Param("projectId") Long projectId);
    Double queryMaxVersionByTreeName(@Param("treeName") String treeName, @Param("projectId") Long projectId);
    List<DTree> queryTreeList(@Param("dTreeVO") DTreeVO dTreeVO);
    List<DTreeVO> queryTreeConfig(@Param("apiCode") String apiCode);
    List<DTreeVO> findTreeConfig(@Param("dNodeModel") DNodeModel dNodeModel);
    int deleteTreeByTreeId(@Param("treeId") Long treeId);

    List<DTree> queryListByProjectId(@Param("projectId") Long projectId);
}
