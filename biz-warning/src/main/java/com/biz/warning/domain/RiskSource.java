package com.biz.warning.domain;

import lombok.Data;

@Data
public class RiskSource {
    private int sourceId;
    private String sourceName;
    private long hitCount;
}
