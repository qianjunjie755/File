package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.*;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.DTableVO;

import java.util.List;

public interface ITableService {
    /**
     * 保存决策表
     * @param dTableVO
     * @return
     */
    RespEntity saveTable(DTableVO dTableVO);

    /**
     * 发布决策表
     * @param dTableVO
     * @return
     */
    RespEntity publishTable(DTableVO dTableVO);

    /**
     * 根据id获取决策表
     * @param tableId
     * @return
     */
    DTableVO getTableByTableId(Long tableId);
    /**
     * 获取决策表List
     * @param apiCode
     * @param projectId
     * @return
     */
    List<DTable> getTableList(Long projectId, String apiCode);

    /**
     * 决策表名称是否存在
     * @param tableName
     * @return
     */
    boolean existTableName(String tableName, Long projectId);

    /**
     * 根据决策表名称查询最大版本号
     * @param tableName
     * @return
     */
    String getMaxVersionByTableName(String tableName);

    /**
     * 根据决策表名称获取版本列表
     * @param tableName
     * @param projectId
     * @return
     */
    List<DTableVO> getVersionListByTableName(Long projectId, String tableName);
    /**
     *查询树的api 列表
     * @param tableId
     * @return
     */
    List<DNodeParam> queryTableApiList(Long tableId);

    /**
     *查询决策表详细信息list
     * @param nodeConfigVO
     * @return
     */
    List<JSONObject> queryTableConfig(DNodeConfigVO nodeConfigVO);

    /**
     *保存决策表详细信息配置
     * @param nodeId
     * @param nodeTableConfig
     * @return
     */
    RespEntity saveTableConfig(Long nodeId, DNodeConfig nodeTableConfig);

    /**
     * 删除决策表
     * @param tableId
     * @return
     */
    RespEntity deleteTableByTableId(Long tableId);

    List<DTable> getListByProjectId(Long projectId);
}
