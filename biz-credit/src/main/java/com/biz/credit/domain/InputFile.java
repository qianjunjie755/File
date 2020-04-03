package com.biz.credit.domain;


import lombok.Data;

@Data
public class InputFile {

  private Integer inputFileId;
  private String inputFileName;
  private String inputFilePath;
  private Integer userId;
  private Integer taskId;
  private Integer successCount;
  private Integer failCount;
  private String fileErrorPath;
  private String fileErrorName;
  private String lastUpdateTime;
  private String createTime;

}
