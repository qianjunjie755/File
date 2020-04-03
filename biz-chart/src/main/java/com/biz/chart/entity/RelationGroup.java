package com.biz.chart.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class RelationGroup {
    private Integer id;
    private String name;
    private List<RelationType> types;

    public void setTypes(List<RelationType> types) {
        if (types == null) {
            return;
        }
        if (this.types == null) {
            this.types = new ArrayList<>();
        }
        this.types.addAll(types);
    }

    public void addRelationType(RelationType type) {
        if (type == null) {
            return;
        }
        if (this.types == null) {
            this.types = new ArrayList<>();
        }
        this.types.add(type);
    }
}
