package com.biz.credit.vo;

import com.biz.credit.domain.ModuleTypeTemplate;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModuleTypeTemplateVO extends ModuleTypeTemplate implements Serializable {
    private   List<ModuleTypeTemplate> childItems;
    private  String apiCode;

}
