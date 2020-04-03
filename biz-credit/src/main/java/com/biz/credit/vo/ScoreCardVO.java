package com.biz.credit.vo;

import com.biz.credit.domain.ScoreCard;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class ScoreCardVO extends ScoreCard {

    private String apiCode;

    private String userId;

    private String projectName;

    private boolean canNewVersion;

    private String judge;

    private boolean choose;

    private Integer type;

    private Integer operationType;

    private List<ScoreApiVO> scoreApiVOList;

    public void cleanData() {
        List<ScoreApiVO> scoreApiVOList = getScoreApiVOList();
        if(!CollectionUtils.isEmpty(scoreApiVOList)){
            for(int i = 0 ; i < scoreApiVOList.size(); i ++){
                ScoreApiVO scoreApiVO = scoreApiVOList.get(i);
                List<ScoreBoundaryVO> scoreBoundaryVOList = scoreApiVO.getScoreBoundaryVOList();
                if(!CollectionUtils.isEmpty(scoreBoundaryVOList)){
                    for(int j = 0 ; j < scoreBoundaryVOList.size(); j ++){
                        ScoreBoundaryVO scoreBoundaryVO = scoreBoundaryVOList.get(j);
                        if(scoreBoundaryVO.isDefauleValue()){
                            scoreBoundaryVOList.remove(j);
                            break;
                        }
                    }
                }
            }
        }
    }
}
