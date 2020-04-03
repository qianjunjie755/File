package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class BIDate implements Serializable {
    private Integer id;
    private String date;
    private Integer month;
    private Integer year;
}
