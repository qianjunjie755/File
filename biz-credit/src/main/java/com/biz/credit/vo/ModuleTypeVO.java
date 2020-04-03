package com.biz.credit.vo;

import com.biz.credit.domain.ModuleType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ModuleTypeVO extends ModuleType implements Serializable {
   private String apiCode;
   private String columnHead;
   private Integer industryId;
   private Integer userId;
   private Integer groupId;

   private String status;//0-无效  1-有效
   private Long newStrategyId;

   public ModuleTypeVO (){
      super();
   }
   public ModuleTypeVO (String apiCode){
      this.apiCode = apiCode;
   }
   public ModuleTypeVO(String apiCode,Integer moduleTypeId){
      this.apiCode = apiCode; this.setModuleTypeId(moduleTypeId);
   }
   public ModuleTypeVO(Integer moduleTypeId){
      this.setModuleTypeId(moduleTypeId);
   }

   public ModuleTypeVO (Integer reportType,Integer isTemplate){
      setReportType(reportType); setIsTemplate(isTemplate);
   }
}
