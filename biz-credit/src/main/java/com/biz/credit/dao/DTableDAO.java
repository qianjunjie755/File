package com.biz.credit.dao;

import com.biz.credit.domain.DNodeModel;
import com.biz.credit.domain.DTable;
import com.biz.credit.vo.DTableVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DTableDAO {
    int insert(@Param("dTableVO") DTableVO dTableVO);
    int update(@Param("dTableVO") DTableVO dTableVO);
    DTableVO queryById(@Param("tableId") Long tableId);
    List<DTable> queryTableList(@Param("dTableVO") DTableVO dTableVO);
    List<DTableVO> queryVersionListByTableName(@Param("dTableVO") DTableVO dTableVO);
    int existTableName(@Param("tableName") String tableName, @Param("projectId") Long projectId);
    String queryMaxVersionByTableName(@Param("tableName") String tableName);
    List<DTableVO> queryTableConfig(@Param("apiCode") String apiCode);
    List<DTableVO> findTableConfig(@Param("dNodeModel") DNodeModel dNodeModel);
    int queryTableVersion(@Param("dTableVO") DTableVO dTableVO);
    int deleteTableByTableId(@Param("tableId") Long tableId);

    List<DTable> queryListByProjectId(@Param("projectId") Long projectId);
}
