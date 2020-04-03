package com.biz.credit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TableVO {
    private String tableName;
    private String tableComment;
    private String columnName;
    private String columnComment;
    private String columnType;
    private String columnKey;
    private String columnDefault;
}
