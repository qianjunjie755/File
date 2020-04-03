package com.biz.credit.dao;

import com.biz.credit.domain.DNodeParam;
import com.biz.credit.domain.DTreeVar;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DTreeVarDAO {
   int insertVar(@Param("dTreeVar") DTreeVar dTreeVar);
   int updateVar(@Param("treeId") Long treeId);
   DTreeVar queryById(@Param("varId") Long varId);
   DTreeVar findByTreeId(@Param("treeId") Long treeId);
   List<DNodeParam> queryByTreeId(@Param("treeId") Long treeId);
   int queryVarByTreeId(@Param("treeId") Long treeId);

}
