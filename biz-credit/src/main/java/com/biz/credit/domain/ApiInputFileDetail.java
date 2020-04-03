package com.biz.credit.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApiInputFileDetail implements Serializable {

  private Long inputFileDetailId;
  private String pdfFilePath;
  private String pdfFileName;
  private String apiCode;
  private Integer status;
  private String keyNo;
  private String creditCode;
  private String idNumber;
  private String cellPhone;
  private String name;
  private String bankId;
  private String homeAddr;
  private String bizAddr;
  private String createTime;
  private String lastUpdateTime;
  private String date;
  private Integer month;
  private Integer year;
  private Integer industryId;
  private Integer reportType;
  private String appId;
  private String industryType;
  private String apiTaskId;
  private List<Param> params;
}
