package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.*;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.ScoreCardVO;

import java.util.List;

public interface IScoreCardService {

    /**
     * 保存评分卡
     * @param scoreCardVO
     * @return
     */
    RespEntity saveScoreCard(ScoreCardVO scoreCardVO);

    /**
     * 发布/取消发布评分卡
     * @return
     */
    RespEntity publishScoreCard(Long scoreCardId, Integer publish);

    /**
     * 根据id获取评分卡
     * @param scoreCardId
     * @return
     */
    ScoreCardVO getScoreCardById(Long scoreCardId);

    /**
     * 评分卡名称是否存在
     * @param scoreCardVO
     * @return
     */
    boolean existCardName(ScoreCardVO scoreCardVO);

    /**
     * 根据评分卡名称查询最大版本号
     * @param scoreCardVO
     * @return
     */
    String getMaxVersionByCardName(ScoreCardVO scoreCardVO);

    /**
     * 根据评分卡名称获取版本列表
     * @param scoreCardVO
     * @return
     */
    List<ScoreCardVO> getVersionListByCardName(ScoreCardVO scoreCardVO);

    /**
     * 获取最高版本评分卡列表
     * @return
     */
    List<ScoreCard> getAllMaxVersionList(Long projectId, String apiCode);


    List<ScoreCard> getListByProjectId(Long projectId);

    /**
     *查询树的api 列表
     * @param scoreCardId
     * @return
     */
    List<DNodeParam> queryScoreCardApiList(Long scoreCardId);
    /**
     *查询评分卡配置
     * @param nodeConfigVO
     * @return
     */
    List<JSONObject> queryScoreCardConfig(DNodeConfigVO nodeConfigVO);

    RespEntity saveScoreCardConfig(Long nodeId, DNodeConfig nodeScoreCardConfig);

    RespEntity deleteScoreCardById(Long scoreCardId);
}
