package com.biz.chart.entity;

import com.biz.relation.entity.RelationChart;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MyChart extends RelationChart {
    private List<MyNode> inNodes;
    private List<MyNode> outNodes;

    public void setInNodes(List<MyNode> inNodes) {
        if (inNodes == null) {
            return;
        }
        if (this.inNodes == null) {
            this.inNodes = new ArrayList<>();
        }
        this.inNodes.addAll(inNodes);
    }

    public void addInNode(MyNode inNode) {
        if (inNode == null) {
            return;
        }
        if (this.inNodes == null) {
            this.inNodes = new ArrayList<>();
        }
        this.inNodes.add(inNode);
    }

    public void setOutNodes(List<MyNode> outNodes) {
        if (outNodes == null) {
            return;
        }
        if (this.outNodes == null) {
            this.outNodes = new ArrayList<>();
        }
        this.outNodes.addAll(outNodes);
    }

    public void addOutNode(MyNode outNode) {
        if (outNode == null) {
            return;
        }
        if (this.outNodes == null) {
            this.outNodes = new ArrayList<>();
        }
        this.outNodes.add(outNode);
    }
}
