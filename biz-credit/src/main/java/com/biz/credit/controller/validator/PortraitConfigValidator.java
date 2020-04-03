package com.biz.credit.controller.validator;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.PortraitLabelReqVO;
import com.biz.credit.vo.PortraitTypeChildrenVO;
import com.biz.credit.vo.PortraitTypeGroupUpdateVO;
import com.biz.credit.vo.PortraitTypeReqVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 企业画像配置相关校验器
 */
public class PortraitConfigValidator {

    public static RespEntity addTypeValidate(PortraitTypeReqVO portraitType) {
        RespEntity respEntity = RespEntity.error();
        if (portraitType.getModuleId() == null || portraitType.getModuleId() <= 0){
            return respEntity.setMsg("模块ID不能为空");
        }
        if (StringUtils.isBlank(portraitType.getTypeName())){
            return respEntity.setMsg("分类组名称不能为空");
        }
        if (StringUtils.isBlank(portraitType.getTypeCode())){
            return respEntity.setMsg("分类组编码不能为空");
        }
        return RespEntity.success();
    }

    public static RespEntity updateTypeGroupValidate(PortraitTypeGroupUpdateVO typeGroup) {
        RespEntity respEntity = RespEntity.error();
        if (typeGroup.getModuleId() == null || typeGroup.getModuleId() <= 0){
            return respEntity.setMsg("模块ID不能为空");
        }
        if (typeGroup.getTypeId() == null || typeGroup.getTypeId() <= 0){
            return respEntity.setMsg("分类组ID不能为空");
        }
        if (CollectionUtils.isEmpty(typeGroup.getChildren())){
            return respEntity.setMsg("子分类ID列表不能为空");
        }
        return RespEntity.success();
    }

    public static RespEntity saveLabelValidate(PortraitLabelReqVO label) {
        RespEntity respEntity = RespEntity.error();
        if (label.getLabelId() != null){
            if (label.getLabelId() <= 0){
                return respEntity.setMsg("标签ID不合法");
            }
        }
        if (StringUtils.isBlank(label.getLabelCode())){
            return respEntity.setMsg("标签编码不能为空");
        }
        if (StringUtils.isBlank(label.getLabelName())){
            return respEntity.setMsg("标签名称不能为空");
        }
        if (label.getTypeId() == null || label.getTypeId() <= 0){
            return respEntity.setMsg("分类ID不能为空");
        }
        return RespEntity.success();
    }
}
