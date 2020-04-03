package com.biz.chart.entity;

import com.biz.relation.entity.ChartNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class MyNode extends ChartNode {
    private String relationAttr;
    private Set<MyNode> subNodes;
}
