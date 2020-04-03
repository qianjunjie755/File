package com.biz.credit.service;

import com.biz.credit.domain.PortraitLabel;
import com.biz.credit.vo.*;

import java.util.List;

public interface IPortraitConfigService {
    /**
     * 查询可用系统模块列表
     * @return
     */
    List<SystemModuleRespVO> querySystemModules();

    /**
     * 查询单个系统模块
     * @param moduleId
     * @return
     */
    SystemModuleRespVO querySingleModule(Integer moduleId);

    /**
     * 添加分类组，包含二级分类
     * @param userId
     * @param portraitType
     */
    void addPortraitType(Integer userId, PortraitTypeReqVO portraitType);

    /**
     * 查询分类组列表
     * @param param
     * @return
     */
    List<PortraitTypeGroupRespVO> queryPortraitTypeGroup(PortraitTypeGroupQueryVO param);

    /**
     * 根据模块ID查询所有分类，包含分类组以及组下面的子分类
     * @param moduleId
     * @return
     */
    List<PortraitTypeRespVO> queryPortraitTypes(Integer moduleId);

    /**
     * 将某些子分类更换分类组
     * @param userId
     * @param typeGroup
     */
    void updatePortraitTypeGroup(Integer userId, PortraitTypeGroupUpdateVO typeGroup);

    /**
     * 查询单个分类
     * @param typeId
     * @return
     */
    PortraitTypeVO querySingleType(Integer typeId);

    /**
     * 批量查询分类
     * @param typeIds
     * @return
     */
    List<PortraitTypeVO> queryTypeByBatch(List<Integer> typeIds);

    /**
     * 新增或更新标签
     * @param userId
     * @param portraitLabel
     */
    Integer saveLabel(Integer userId, PortraitLabelReqVO portraitLabel);

    /**
     * 更新标签状态
     * @param labelId
     * @param status
     * @param userId
     */
    void updateLabelStatus(Integer labelId, Integer status, Integer userId);

    /**
     * 查询单个标签
     * @param labelId
     * @return
     */
    PortraitLabel querySingleLabel(Integer labelId);

    /**
     * 查询标签列表
     * @param labelCode
     * @param labelName
     * @param status
     * @return
     */
    List<PortraitLabelRespVO> queryPortraitLabels(String labelCode, String labelName, Integer status);

    /**
     * 批量查询标签
     * @param labelIds
     * @return
     */
    List<PortraitLabelRespVO> queryLabelsByBatch(List<Integer> labelIds);
}
