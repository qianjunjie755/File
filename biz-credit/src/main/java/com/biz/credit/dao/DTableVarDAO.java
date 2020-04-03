package com.biz.credit.dao;

import com.biz.credit.domain.DNodeParam;
import com.biz.credit.domain.DTableVar;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DTableVarDAO {
    int insert(@Param("dTableVar") DTableVar dTableVar);
    List<DTableVar> queryVar(@Param("tableId") Long tableId);
    int updateVar(@Param("tableId") Long tableId);
    Long queryParentVarId(@Param("dTableVar") DTableVar dTableVar);
    List<DNodeParam> queryByTableId(@Param("tableId") Long tableId);

}
