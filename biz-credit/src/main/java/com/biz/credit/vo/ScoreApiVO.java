package com.biz.credit.vo;

import com.biz.credit.domain.ScoreApi;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScoreApiVO extends ScoreApi {

    private Integer ruleType;

    private List<ScoreBoundaryVO> scoreBoundaryVOList;
}
