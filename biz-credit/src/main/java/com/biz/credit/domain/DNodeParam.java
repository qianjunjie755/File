package com.biz.credit.domain;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class DNodeParam implements Serializable {
    private Long id;
    private String code;
    private String name;
    private String fields;
    private Boolean choose;
    private Integer required;
    private Integer isChoose;
    private Boolean isRequired;
    private Long nodeId;
    private Integer status;
    private Integer updateTime;
    private Integer createTime;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DNodeParam)) return false;
        DNodeParam that = (DNodeParam) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, name);
    }
}
