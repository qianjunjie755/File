package com.biz.chart.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString
public class Relation {
    private Integer id;
    private String name;
    private Integer startId;
    private String startName;
    private Integer endId;
    private String endName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Relation)) {
            return false;
        }
        Relation relation = (Relation) o;
        return Objects.equals(id, relation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
