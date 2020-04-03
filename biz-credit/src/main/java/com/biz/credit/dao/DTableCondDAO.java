package com.biz.credit.dao;

import com.biz.credit.domain.DTableCond;
import com.biz.credit.domain.DTableVar;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DTableCondDAO {
    int insertCond(@Param("dTableCond") DTableCond dTableCond);
    Long queryByParentId(@Param("dTableCond") DTableCond dTableCond);
    int updateCond(@Param("tableId") Long tableId);
    List<DTableCond>  queryCond(@Param("dTableVar") DTableVar dTableVar);
    List<DTableCond>  queryCondList(@Param("tableId") Long tableId);
    int queryParentCondOrder(@Param("dTableCond") DTableCond dTableCond);
    int queryChildrenCond(@Param("dTableCond") DTableCond dTableCond);
    int queryCondIsDefault(@Param("dTableCond") DTableCond dTableCond);
}
