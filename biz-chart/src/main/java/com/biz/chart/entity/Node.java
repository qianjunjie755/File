package com.biz.chart.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString
public class Node {
    private Integer id;
    private String name;
    private String type;
    private String labels;
    private String properties;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
