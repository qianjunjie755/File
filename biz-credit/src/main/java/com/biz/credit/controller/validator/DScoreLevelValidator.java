package com.biz.credit.controller.validator;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.DScoreLevelChildrenVO;
import com.biz.credit.vo.DScoreLevelVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 评分等级参数校验器
 * 反欺诈模型 ANTI_FRAUD_SCORE=3
 * 信用评分模型 CREDIT_SCORE_MODEL=4
 * 评分卡模型 SCORE_CARD=5
 */
public class DScoreLevelValidator {

    private static final int[] modelTypes = new int[]{Constants.ANTI_FRAUD_SCORE,
                                                Constants.CREDIT_SCORE_MODEL,
                                                Constants.SCORE_CARD};

    public static RespEntity saveValidate(DScoreLevelVO scoreLevel) {
        RespEntity respEntity = RespEntity.error();
        if (StringUtils.isBlank(scoreLevel.getApiCode())){
            return respEntity.setMsg("客户代码不能为空");
        }
        if (scoreLevel.getModelCode() == null || scoreLevel.getModelCode() <= 0){
            return respEntity.setMsg("模型ID不能为空");
        }
        if (scoreLevel.getModelType() == null){
            return respEntity.setMsg("模型类型不能为空");
        }else {
            boolean flag = false;
            for (Integer modelType : modelTypes){
                if (modelType.equals(scoreLevel.getModelType())){
                    flag = true;
                }
            }
            if (!flag){
                return respEntity.setMsg("模型类型只能是3/4/5");
            }
        }
        if (CollectionUtils.isEmpty(scoreLevel.getModelLevels())){
            return respEntity.setMsg("模型等级列表不能为空");
        }else {
            for (DScoreLevelChildrenVO c : scoreLevel.getModelLevels()){
                if (StringUtils.isBlank(c.getScoreLevel())){
                    return respEntity.setMsg("评分等级不能为空");
                }
                if (StringUtils.isBlank(c.getScoreRange())){
                    return respEntity.setMsg("评分区间不能为空");
                }
            }
        }

        return RespEntity.success();
    }

    public static RespEntity queryValidate(String apiCode, Integer modelCode, Integer modelType) {
        RespEntity respEntity = RespEntity.error();
        if (StringUtils.isBlank(apiCode)){
            return respEntity.setMsg("客户代码不能为空");
        }
        if (modelCode == null || modelCode <= 0){
            return respEntity.setMsg("模型ID不能为空");
        }
        if (modelType == null){
            return respEntity.setMsg("模型类型不能为空");
        }

        return RespEntity.success();
    }
}
