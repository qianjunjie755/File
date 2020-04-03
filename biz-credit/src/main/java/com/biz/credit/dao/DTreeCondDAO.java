package com.biz.credit.dao;

import com.biz.credit.domain.DTreeCond;
import com.biz.credit.domain.DTreeVar;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DTreeCondDAO {
    int insertCond(@Param("dTreeCond") DTreeCond dTreeCond);
    int updateCond(@Param("treeId") Long treeId);
    int update(@Param("dTreeCond") DTreeCond dTreeCond);
    List<DTreeCond>  queryCond(@Param("dTreeVar") DTreeVar dTreeVar);
    List<DTreeCond> queryListByCond(@Param("dtreeCond") DTreeCond dtreeCond);
    List<DTreeVar> queryCondOut(@Param("dTreeCond") DTreeCond dTreeCond);
    int updateNextId(@Param("dTreeCond") DTreeCond dTreeCond);
}
