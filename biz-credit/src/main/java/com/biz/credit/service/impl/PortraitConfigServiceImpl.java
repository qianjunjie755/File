package com.biz.credit.service.impl;

import com.biz.credit.dao.PortraitConfigDAO;
import com.biz.credit.dao.PortraitTaskDAO;
import com.biz.credit.domain.PortraitLabel;
import com.biz.credit.domain.PortraitType;
import com.biz.credit.service.IPortraitConfigService;
import com.biz.credit.vo.*;
import com.biz.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class PortraitConfigServiceImpl implements IPortraitConfigService {
    @Autowired
    private PortraitConfigDAO portraitConfigDao;

    @Autowired
    private PortraitTaskDAO portraitTaskDAO;

    @Override
    public List<SystemModuleRespVO> querySystemModules() {
        return portraitConfigDao.findSystemModules();
    }

    @Override
    public SystemModuleRespVO querySingleModule(Integer moduleId) {
        return portraitConfigDao.findSingleModule(moduleId);
    }

    @Override
    @Transactional
    public void addPortraitType(Integer userId, PortraitTypeReqVO portraitType) {
        String now = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
        PortraitType parent = convertPortraitType(userId, now);
        parent.setModuleId(portraitType.getModuleId());
        parent.setTypeName(portraitType.getTypeName());
        parent.setTypeCode(portraitType.getTypeCode());
        parent.setParentId(-1);
        parent.setCreateUser(userId);
        parent.setCreateTime(now);
        portraitConfigDao.insertType(parent);

        if (!CollectionUtils.isEmpty(portraitType.getChildren())){
            for (PortraitTypeChildrenVO children : portraitType.getChildren()){
                PortraitType childrenType = convertPortraitType(userId, now);
                childrenType.setModuleId(portraitType.getModuleId());
                childrenType.setTypeName(children.getTypeName());
                childrenType.setTypeCode(children.getTypeCode());
                childrenType.setParentId(parent.getTypeId());
                childrenType.setCreateUser(userId);
                childrenType.setCreateTime(now);
                portraitConfigDao.insertType(childrenType);
            }
        }
    }

    private PortraitType convertPortraitType(Integer userId, String now){
        PortraitType type = new PortraitType();
        type.setStatus(1);
        type.setUpdateUser(userId);
        type.setUpdateTime(now);
        return type;
    }

    @Override
    public List<PortraitTypeGroupRespVO> queryPortraitTypeGroup(PortraitTypeGroupQueryVO param) {
        return portraitConfigDao.findPortraitTypeGroup(param);
    }

    @Override
    public List<PortraitTypeRespVO> queryPortraitTypes(Integer moduleId) {
        return portraitConfigDao.findPortraitTypes(moduleId);
    }

    @Override
    public void updatePortraitTypeGroup(Integer userId, PortraitTypeGroupUpdateVO typeGroup) {
        portraitConfigDao.updateParentIdByTypeId(userId, typeGroup.getTypeId(), typeGroup.getChildren());
    }

    @Override
    public PortraitTypeVO querySingleType(Integer typeId) {
        return portraitConfigDao.findSingleType(typeId);
    }

    @Override
    public List<PortraitTypeVO> queryTypeByBatch(List<Integer> typeIds) {
        return portraitConfigDao.findTypeByBatch(typeIds);
    }

    @Override
    public Integer saveLabel(Integer userId, PortraitLabelReqVO portraitLabel) {
        if (portraitLabel.getLabelId() == null){
            PortraitLabel label = new PortraitLabel();
            convertPortraitLabel(label, portraitLabel, userId);
            portraitConfigDao.insertLabel(label);
            return label.getLabelId();
        }else {
            PortraitLabel label = portraitConfigDao.findSingleLabel(portraitLabel.getLabelId());
            if (label == null){
                return null;
            }
            convertPortraitLabel(label, portraitLabel, userId);
            portraitConfigDao.updateLabel(label);
            return null;
        }
    }

    /**
     * 为即将入库的Label对象设置属性
     * @param label
     * @param portraitLabel
     * @param userId
     */
    private void convertPortraitLabel(PortraitLabel label, PortraitLabelReqVO portraitLabel, Integer userId) {
        BeanUtils.copyProperties(portraitLabel, label);
        String now = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
        if (label.getLabelId() == null){
            label.setCreateUser(userId);
            label.setCreateTime(now);
        }
        label.setUpdateUser(userId);
        label.setUpdateTime(now);
    }

    @Override
    @Transactional
    public void updateLabelStatus(Integer labelId, Integer status, Integer userId) {
        portraitConfigDao.updateLabelStatus(labelId, status, userId);
        //更新任务标签表状态
        portraitTaskDAO.updateTaskLabelStatus(labelId, status, userId);
    }

    @Override
    public PortraitLabel querySingleLabel(Integer labelId) {
        return portraitConfigDao.findSingleLabel(labelId);
    }

    @Override
    public List<PortraitLabelRespVO> queryPortraitLabels(String labelCode, String labelName, Integer status) {
        return portraitConfigDao.findPortraitLabels(labelCode, labelName, status);
    }

    @Override
    public List<PortraitLabelRespVO> queryLabelsByBatch(List<Integer> labelIds) {
        return portraitConfigDao.findLabelsByBatch(labelIds);
    }

}
