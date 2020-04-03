package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Setter
@Getter
public class ScoreBoundary implements Serializable, Comparable<ScoreBoundary> {
    private Long scoreBoundaryId;
    private Long scoreApiId;
    private Integer status;
    private BigDecimal score;
    private BigDecimal bdLeft;
    private BigDecimal bdRight;
    private String hitValue;
    private String createTime;
    private String lastUpdateTime;


    @Override
    public int compareTo(ScoreBoundary o) {
        if(o.bdRight == null){
            return -1;
        }
        if(this.bdRight == null){
            return 1;
        }
        return this.bdRight.compareTo(o.bdRight);
    }
}
