package com.biz.credit.utils.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiDataVO {
    @ExcelProperty(index = 3)
    private String apiName;
    @ExcelProperty(index = 4)
    private String apiProdCode;
}
