package com.biz.credit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Column {
    private String columnName;
    private String columnComment;
    private String columnType;
    private String columnKey;
    private String columnDefault;
}
