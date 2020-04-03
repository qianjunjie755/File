package com.biz.credit;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Table {
    private String tableName;
    private String tableComment;
    private List<Column> columns;

    public void addColumn(Column column) {
        if (column == null) {
            return;
        }
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }
        this.columns.add(column);
    }
}
