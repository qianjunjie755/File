package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.*;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.DTreeVO;

import java.util.List;

public interface ITreeService{

    /**
     * 保存决策树
     * @param dTreeVO
     * @return
     */
    RespEntity saveTree(DTreeVO dTreeVO);

    /**
     * 发布决策树
     * @param dTreeVO
     * @return
     */
    RespEntity publishTree(DTreeVO dTreeVO);

    /**
     * 根据id获取决策树
     * @param treeId
     * @return
     */
    DTreeVO getTreeById(Long treeId);
    /**
     * 获取决策树List
     * @param apiCode
     * @param projectId
     * @return
     */
    List<DTree> getTreeList(Long projectId, String apiCode);

    /**
     * 决策树名称是否存在
     * @param treeName
     * @param projectId
     * @return
     */
    boolean existTreeName(String treeName, Long projectId);

    /**
     * 根据决策树名称查询最大版本号
     * @param treeName
     * @param projectId
     * @return
     */
    Double getMaxVersionByTreeName(String treeName, Long projectId);

    /**
     * 根据决策树名称获取版本列表
     * @param treeName
     * @param projectId
     * @return
     */
    List<DTree> getVersionListByTreeName(Long projectId, String treeName);

    /**
     *查询树的api参数列表
     * @param treeId
     * @return
     */
    List<DNodeParam> queryTreeApiList(Long treeId);

    /**
     *查询决策树详细信息list
     * @param nodeConfigVO
     * @return
     */
    List<JSONObject> queryTreeConfig(DNodeConfigVO nodeConfigVO);


    /**
     *保存决策树详细信息配置
     * @param nodeId
     * @param nodeTreeConfig
     * @return
     */
    RespEntity saveTreeConfig(Long nodeId, DNodeConfig nodeTreeConfig);
    /**
     * 删除决策树
     * @param treeId
     * @return
     */
    RespEntity deleteTreeByTreeId(Long treeId);


    List<DTree> getListByProjectId(Long projectId);
}
