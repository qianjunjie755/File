package com.biz.credit.service.impl;

import com.biz.credit.dao.DScoreLevelDAO;
import com.biz.credit.domain.DScoreLevel;
import com.biz.credit.service.IDScoreLevelService;
import com.biz.credit.vo.DScoreLevelVO;
import com.biz.credit.vo.DScoreLevelChildrenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DScoreLevelServiceImpl implements IDScoreLevelService {

    @Autowired
    private DScoreLevelDAO scoreLevelDAO;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${biz.decision.score-range-key:#{null}}")
    private String scoreRangeKey;

    @Override
    @Transactional
    public void saveScoreLevel(DScoreLevelVO scoreLevel) {
        //校验模型的有效性


        scoreLevelDAO.deleteByCondition(scoreLevel.getApiCode(), scoreLevel.getModelCode(), scoreLevel.getModelType());
        List<DScoreLevel> list = new ArrayList<>();
        for (DScoreLevelChildrenVO c : scoreLevel.getModelLevels()){
            DScoreLevel newScoreLevel = new DScoreLevel();
            newScoreLevel.setApiCode(scoreLevel.getApiCode());
            newScoreLevel.setModelCode(scoreLevel.getModelCode());
            newScoreLevel.setModelType(scoreLevel.getModelType());
            newScoreLevel.setScoreLevel(c.getScoreLevel().toUpperCase());
            newScoreLevel.setScoreRange(c.getScoreRange());
            list.add(newScoreLevel);
        }
        //按评分等级正序排列
        Collections.sort(list, Comparator.comparing(DScoreLevel::getScoreLevel));
        scoreLevelDAO.insertBatch(list);

        //清除redis缓存
        if (scoreRangeKey != null){
            String key = scoreLevel.getModelType() + "|" + scoreLevel.getApiCode() + "|" + scoreLevel.getModelCode();
            redisTemplate.opsForHash().delete(scoreRangeKey, key);
        }

    }

    @Override
    public DScoreLevelVO findByCondition(String apiCode, Integer modelCode, Integer modelType) {

        List<DScoreLevel> list = scoreLevelDAO.findByCondition(apiCode, modelCode, modelType);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        DScoreLevelVO scoreLevelVO = new DScoreLevelVO();
        scoreLevelVO.setApiCode(apiCode);
        scoreLevelVO.setModelCode(modelCode);
        scoreLevelVO.setModelType(modelType);
        List<DScoreLevelChildrenVO> childrenList = new ArrayList<>();
        for (DScoreLevel scoreLevel : list){
            DScoreLevelChildrenVO children = new DScoreLevelChildrenVO();
            children.setScoreLevel(scoreLevel.getScoreLevel());
            children.setScoreRange(scoreLevel.getScoreRange());
            childrenList.add(children);
        }
        scoreLevelVO.setModelLevels(childrenList);
        return scoreLevelVO;
    }
}
