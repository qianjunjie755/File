package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.*;
import com.biz.credit.domain.*;
import com.biz.credit.service.IProjectService;
import com.biz.credit.service.IScoreCardService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ScoreCardServiceImpl implements IScoreCardService {

    @Autowired
    private ScoreCardDAO scoreCardDAO;

    @Autowired
    private DRuleVarDAO dRuleVarDAO;

    @Autowired
    private ScoreApiDAO scoreApiDAO;

    @Autowired
    private ScoreBoundaryDAO scoreBoundaryDAO;

    @Autowired
    private DNodeModelDAO dNodeModelDAO;
    @Autowired
    private DNodeThresholdDAO dNodeThresholdDAO;

    @Autowired
    private IProjectService projectService;

    @Override
    @Transactional
    public RespEntity saveScoreCard(ScoreCardVO scoreCardVO) {
        if (scoreCardVO.getProjectId() == null) {
            if (StringUtils.isEmpty(scoreCardVO.getUserId())) {
                scoreCardVO.setUserId(Constants.BAIRONG_SUPER_ADMIN);
            }
            Project project = projectService.getFirstProject(scoreCardVO.getApiCode(), Long.valueOf(scoreCardVO.getUserId()));
            scoreCardVO.setProjectId(project.getId());
        }
        Integer operationType = scoreCardVO.getOperationType();
        if(operationType == null){
            if(scoreCardVO.getScoreCardId() == null){
                operationType = 1;
            }else{
                operationType = 2;
            }
        }
        //参数校验
        RespEntity checkResp = checkParams(scoreCardVO,operationType);
        if(!checkResp.isSuccess()){
            return checkResp;
        }
        //保存评分卡
        if(operationType == 0 || operationType == 1){
            if(operationType == 0){
                scoreCardVO.setCardVersion(Constants.SCORE_CARD_DEFAULT_VALUE);
                scoreCardVO.setSettingWeight(Constants.COMMON_STATUS_INVALID);
                scoreCardVO.setCalculateType(Constants.COMMON_STATUS_VALID);
                scoreCardVO.setScoreBase(BigDecimal.ZERO);
            }
            scoreCardVO.setStatus(Constants.SCORE_CARD_STATUS_VALID);
            scoreCardVO.setPublish(Constants.SCORE_CARD_PUBLISH_NO);
            scoreCardVO.setScoreBoundaryType(Constants.SCORE_BOUNDARY_TYPE_LEFT_CONTAIN);
            scoreCardVO.setScoreCardType(Constants.SCORE_CARD_TYPE_DEFAULT);
            scoreCardDAO.insert(scoreCardVO);
            if(operationType == 0){
                return RespEntity.success().setData(scoreCardVO.getScoreCardId());
            }
            return insertRelation(scoreCardVO);
        }else{
            return updateScoreCard(scoreCardVO);
        }
    }


    private RespEntity insertRelation(ScoreCardVO scoreCardVO) {
        int index = 1;
        List<ScoreBoundaryVO> scoreBoundaryVOList = new ArrayList<>();
        for (ScoreApiVO scoreApiVO : scoreCardVO.getScoreApiVOList()) {
            DRuleVarVO dRuleVarVO = dRuleVarDAO.queryById(scoreApiVO.getVarId());
            if(dRuleVarVO != null){
                scoreApiVO.setApiProdCode(dRuleVarVO.getApiProdCode());
                scoreApiVO.setApiVersion(dRuleVarVO.getApiVersion());
                scoreApiVO.setVarVersion(dRuleVarVO.getVersion());
                scoreApiVO.setVarName(dRuleVarVO.getVariableName());
                //scoreApiVO.setFeq(dRuleVarVO.getVarPeriod());
                //scoreApiVO.setFeqUnit(dRuleVarVO.getPeriodUnit());
                scoreApiVO.setVarCode(dRuleVarVO.getProdCode());
            }
            scoreApiVO.setModelParamName("param" + index ++);
            scoreApiVO.setStatus(Constants.SCORE_CARD_STATUS_VALID);
            scoreApiVO.setScoreCardId(scoreCardVO.getScoreCardId());
            scoreApiDAO.insert(scoreApiVO);
            for (ScoreBoundaryVO scoreBoundaryVO : scoreApiVO.getScoreBoundaryVOList()) {
                scoreBoundaryVO.setStatus(Constants.SCORE_CARD_STATUS_VALID);
                scoreBoundaryVO.setScoreApiId(scoreApiVO.getScoreApiId());
                scoreBoundaryVOList.add(scoreBoundaryVO);
            }
        }
        scoreBoundaryDAO.insertList(scoreBoundaryVOList);
        return RespEntity.success();
    }

    @Override
    @Transactional
    public RespEntity publishScoreCard(Long scoreCardId, Integer publish) {
        //查询评分卡是否已保存
        ScoreCardVO exist = scoreCardDAO.queryById(scoreCardId);
        if (exist == null){
            return RespEntity.error().setMsg("发布前请先保存评分卡");
        }

        ScoreCardVO scoreCardVO = new ScoreCardVO();
        scoreCardVO.setScoreCardId(scoreCardId);
        scoreCardVO.setPublish(publish);
        scoreCardDAO.update(scoreCardVO);
        return RespEntity.success();
    }

    @Override
    public ScoreCardVO getScoreCardById(Long scoreCardId) {
        ScoreCardVO scoreCardVO = scoreCardDAO.queryById(scoreCardId);
        List<ScoreCardVO> versionList = scoreCardDAO.queryVersionListByCardName(scoreCardVO);
        boolean canNewVersion = true;
        for (ScoreCardVO card : versionList) {
            if(Constants.SCORE_CARD_PUBLISH_NO.equals(card.getPublish())){
                canNewVersion = false;
                break;
            }
        }
        scoreCardVO.setCanNewVersion(canNewVersion);
        return scoreCardVO;
    }

    @Override
    public boolean existCardName(ScoreCardVO scoreCard) {
        int count = scoreCardDAO.queryCountByCardName(scoreCard);
        return count != 0;
    }

    @Override
    public String getMaxVersionByCardName(ScoreCardVO scoreCardVO) {
        return scoreCardDAO.queryMaxVersionByCardName(scoreCardVO);
    }

    @Override
    public List<ScoreCardVO> getVersionListByCardName(ScoreCardVO scoreCardVO) {
        return scoreCardDAO.queryVersionListByCardName(scoreCardVO);
    }

    @Override
    public List<ScoreCard> getAllMaxVersionList(Long projectId, String apiCode) {
        return scoreCardDAO.queryAllMaxVersionList(apiCode, projectId);
    }

    @Override
    public List<DNodeParam> queryScoreCardApiList(Long scoreCardId) {
        return scoreApiDAO.queryByScoreCardId(scoreCardId);
    }

    @Override
    public List<ScoreCard> getListByProjectId(Long projectId) {
        return scoreCardDAO.queryListByProjectId(projectId);
    }

    @Override
    @Transactional
    public RespEntity saveScoreCardConfig(Long nodeId,DNodeConfig nodeScoreCardConfig) {
        if(nodeScoreCardConfig== null){
            return RespEntity.error().setMsg("nodeScoreCardConfig为空");
        }else{
                checkNodeConfigParams(nodeScoreCardConfig);
                DNodeModel dNodeModel=new DNodeModel();
                dNodeModel.setModelType(Constants.SCORE_CARD);
                dNodeModel.setNodeId(nodeId);
                dNodeModel.setModelCode(nodeScoreCardConfig.getScoreCardId());
                dNodeModel.setStatus(Constants.COMMON_STATUS_VALID);
                //查询
                DNodeModel nodeModel = dNodeModelDAO.queryNodeModel(dNodeModel);
                if(nodeModel==null){
                    dNodeModelDAO.insert(dNodeModel);
                    DNodeThreshold dNodeThreshold=new DNodeThreshold();
                    dNodeThreshold.setModelId(dNodeModel.getModelId());
                    dNodeThreshold.setJudge(nodeScoreCardConfig.getJudge());
                    dNodeThreshold.setType(nodeScoreCardConfig.getType());
                    dNodeThresholdDAO.insert(dNodeThreshold);
                }else{
                    //失效
                    dNodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.SCORE_CARD,Constants.COMMON_STATUS_INVALID);
                    dNodeModelDAO.insert(dNodeModel);
                    DNodeThreshold dNodeThreshold=new DNodeThreshold();
                    dNodeThreshold.setModelId(dNodeModel.getModelId());
                    dNodeThreshold.setJudge(nodeScoreCardConfig.getJudge());
                    dNodeThreshold.setType(nodeScoreCardConfig.getType());
                    dNodeThresholdDAO.insert(dNodeThreshold);
                }

            return RespEntity.success();
        }

    }

    @Override
    public RespEntity deleteScoreCardById(Long scoreCardId) {
        ScoreCardVO scoreCardVO = new ScoreCardVO();
        scoreCardVO.setScoreCardId(scoreCardId);
        scoreCardVO.setStatus(Constants.COMMON_STATUS_INVALID);
        scoreCardDAO.update(scoreCardVO);
        return RespEntity.success();
    }

    @Override
    public List<JSONObject> queryScoreCardConfig(DNodeConfigVO nodeConfigVO){
        List<JSONObject> tList = new ArrayList<>();
        DNodeModel dNodeModel=new DNodeModel();
        dNodeModel.setNodeId(nodeConfigVO.getNodeId());
        dNodeModel.setApiCode(Long.parseLong(nodeConfigVO.getApiCode()));
        dNodeModel.setModelType(Constants.SCORE_CARD);
        List<com.biz.credit.vo.ScoreCardVO> scoreCardList = scoreCardDAO.findScoreCardList(dNodeModel);
        for(ScoreCardVO scoreCardVO :scoreCardList ){
            JSONObject jsonObject = new JSONObject();
            if(!StringUtils.isEmpty(scoreCardVO.getJudge())){
                List<DNodeThreshold> nodeThresholds=new ArrayList<>();
                DNodeThreshold dNodeThreshold=new DNodeThreshold();
                String Judge=scoreCardVO.getJudge().substring(2);
                dNodeThreshold.setJudge(Judge);
                dNodeThreshold.setType(scoreCardVO.getType());
                nodeThresholds.add(dNodeThreshold);
                jsonObject.put("nodeThresholdList",nodeThresholds);
            }else{
                List<DNodeThreshold> list =new ArrayList<>();
                DNodeThreshold dNodeThreshold=new DNodeThreshold();
                dNodeThreshold.setJudge("");
                list.add(dNodeThreshold);
                jsonObject.put("nodeThresholdList",list);
            }
            jsonObject.put("scoreCardId",scoreCardVO.getScoreCardId());
            jsonObject.put("name", scoreCardVO.getProjectName());
            jsonObject.put("cardName",scoreCardVO.getCardName());
            jsonObject.put("cardVersion", scoreCardVO.getCardVersion());
            jsonObject.put("returnField", Constants.SCORE_CARD_RETURN_FIELD);
            jsonObject.put("cardDescription", scoreCardVO.getCardDescription());
            jsonObject.put("choose",scoreCardVO.isChoose());
            tList.add(jsonObject);
        }
        return tList;
    }
    private RespEntity updateScoreCard(ScoreCardVO scoreCardVO) {
        //1.查询源关联api和边界
        //2.更新评分卡
        //3.失效关联的api和边界
        //4.保存api和边界
        ScoreCardVO existScoreCardVO = scoreCardDAO.queryById(scoreCardVO.getScoreCardId());
        if(existScoreCardVO == null){
            return RespEntity.error().setMsg("评分卡不存在");
        }
        scoreCardDAO.update(scoreCardVO);
        List<ScoreApiVO> scoreApiVOList = existScoreCardVO.getScoreApiVOList();
        List<Long> scoreBoundaryIdList = new ArrayList<>();
        for (ScoreApiVO scoreApiVO : scoreApiVOList) {
            for (ScoreBoundaryVO scoreBoundaryVO : scoreApiVO.getScoreBoundaryVOList()) {
                scoreBoundaryIdList.add(scoreBoundaryVO.getScoreBoundaryId());
            }
        }
        scoreApiDAO.updateStatusByScoreCardId(scoreCardVO.getScoreCardId(),Constants.SCORE_CARD_STATUS_UNVALID);
        if(!CollectionUtils.isEmpty(scoreBoundaryIdList)){
            scoreBoundaryDAO.batchUpdateStatus(scoreBoundaryIdList,Constants.SCORE_CARD_STATUS_UNVALID);
        }
        return insertRelation(scoreCardVO);
    }

    private RespEntity checkParams(ScoreCardVO scoreCardVO,int operationType) {
        if(scoreCardVO.getProjectId() == null){
            return RespEntity.error().setMsg("所属项目不能为空");
        }
        if(StringUtils.isEmpty(scoreCardVO.getCardName())){
            return RespEntity.error().setMsg("评分卡名称不能为空");
        }
        if(operationType == 0){
            //初始化
            boolean existCardName = existCardName(scoreCardVO);
            if(existCardName){
                return RespEntity.error().setMsg("该评分卡名称已存在，请更换");
            }
           return  RespEntity.success();
        }
        //清理数据
        scoreCardVO.cleanData();
        if (StringUtils.isEmpty(scoreCardVO.getCardVersion())) {
            return RespEntity.error().setMsg("评分卡版本不能为空");
        }
        if (scoreCardVO.getScoreBase() == null) {
            return RespEntity.error().setMsg("基础分值不能为空");
        }
        if(scoreCardVO.getCalculateType() == null){
            return RespEntity.error().setMsg("计算类型不能为空");
        }
        if(scoreCardVO.getSettingWeight() == null){
            return RespEntity.error().setMsg("是否设置权重不能为空");
        }
        List<ScoreApiVO> scoreApiVOList = scoreCardVO.getScoreApiVOList();
        if (CollectionUtils.isEmpty(scoreApiVOList)) {
            return RespEntity.error().setMsg("至少选择一个变量");
        }
        for (ScoreApiVO scoreApiVO : scoreApiVOList) {
            if (StringUtils.isEmpty(scoreApiVO.getVarId())) {
                return RespEntity.error().setMsg("评分卡apiId不能为空");
            }
            if (scoreApiVO.getConditionType() == null) {
                return RespEntity.error().setMsg("条件类型不能为空");
            }
            List<ScoreBoundaryVO> scoreBoundaryVOList = scoreApiVO.getScoreBoundaryVOList();
            if (CollectionUtils.isEmpty(scoreBoundaryVOList)) {
                return RespEntity.error().setMsg("每个变量至少需要一个判断条件");
            }
            RespEntity checkBoundaryResp = null;
            if (Constants.API_CONDITION_TYPE_CONTINUITY.equals(scoreApiVO.getConditionType())) {
                checkBoundaryResp = checkContinuityBoundary(scoreBoundaryVOList);
            } else if (Constants.API_CONDITION_TYPE_DISCRETE.equals(scoreApiVO.getConditionType())) {
                checkBoundaryResp = checkDiscreteBoundary(scoreBoundaryVOList);
            }
            if (checkBoundaryResp != null && !checkBoundaryResp.isSuccess()) {
                return checkBoundaryResp;
            }
            for (ScoreBoundaryVO scoreBoundaryVO : scoreBoundaryVOList) {
                if (scoreBoundaryVO.isDefauleValue()) {
                    continue;
                }
                if (scoreBoundaryVO.getScore() == null) {
                    return RespEntity.error().setMsg("分值不能为空");
                }
            }
        }
        return RespEntity.success();
    }

    private RespEntity checkDiscreteBoundary(List<ScoreBoundaryVO> scoreBoundaryVOList) {
        return RespEntity.success();
    }

    private RespEntity checkContinuityBoundary(List<ScoreBoundaryVO> scoreBoundaryVOList) {
        Collections.sort(scoreBoundaryVOList);
        RespEntity errResp = RespEntity.error().setMsg("边界条件不正确");
        BigDecimal left = scoreBoundaryVOList.get(0).getBdLeft();
        BigDecimal right = scoreBoundaryVOList.get(0).getBdRight();
        for (int i = 1;i < scoreBoundaryVOList.size(); i ++){
            if (scoreBoundaryVOList.get(i).getBdLeft() == null){
                return errResp;
            }
            if(! scoreBoundaryVOList.get(i).getBdLeft().equals(right)){
                return errResp;
            }
            right =  scoreBoundaryVOList.get(i).getBdRight();
        }
        return RespEntity.success();
    }
    private RespEntity checkNodeConfigParams(DNodeConfig dNodeConfig){
        if (StringUtils.isEmpty(dNodeConfig.getJudge())) {
            return RespEntity.error().setMsg("阈值配置区间不能为空");
        }
        if (StringUtils.isEmpty(dNodeConfig.getJudge())) {
            return RespEntity.error().setMsg("阈值配置类型不能为空");
        }
        return RespEntity.success();
    }
}
