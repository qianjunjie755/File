package com.biz.credit.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Objects;
@ApiModel("决策流逻辑规则阈值对象")
@Getter
@Setter
@NoArgsConstructor
public class VarThresholdVO implements Serializable {

    @ApiModelProperty(value="变量代码",required = true,example = "var.ic.biz.four")
    private String prodCode;
    @ApiModelProperty(value="变量名称",required = true,example = "企业四要素")
    private String varName;
    @ApiModelProperty(value="变量版本号",required = true,example = "1.0")
    private String version = "1.0";
    @ApiModelProperty(value="变量集代码",required = true,example = "varset.biz.detail")
    private String varset;
    @ApiModelProperty(value="变量集版本号",required = true,example = "1.0")
    private String varsetVersion;
    @ApiModelProperty(value="判断类型",required = true,example = "gt,lt",notes = "gt大于，ge大于等于，lt小于，le小于等于，eq等于，neq不等于")
    private String type;
    private String lt = StringUtils.EMPTY;
    private String gt = StringUtils.EMPTY;
    private String eq = StringUtils.EMPTY;
    @ApiModelProperty(value="阈值",required = true,example = "1,3")
    private String threshold;
    @ApiModelProperty(value="时间颗粒度",required = true,example = "-1",notes = "默认-1m 无限")
    private String interval = "-1m";
    @ApiModelProperty(value="变量匹配度",example = "0.2")
    private String match = StringUtils.EMPTY;
    private String logicSign = StringUtils.EMPTY;
    @ApiModelProperty(value="原始规则编号",required = true,example = "11")
    private Integer ruleId;
    @ApiModelProperty(value="原始规则名称",required = true,example = "规则1")
    private String ruleName;

    public VarThresholdVO(String prodCode, String version,String varName, String varset,String varsetVersion, String type, String lt, String gt, String eq, String threshold, String interval, String match,Integer ruleId, String ruleName) {
        setProdCode(prodCode);
        setVersion(version);
        setVarName(varName);
        setVarset(varset);
        setVarsetVersion(varsetVersion);
        setType(type);
        setLt(lt);
        setGt(gt);
        setEq(eq);
        setThreshold(threshold);
        setInterval(interval);
        setMatch(match);
        setRuleId(ruleId);
        setRuleName(ruleName);
    }

    public void setVersion(String version) {
        if(StringUtils.isNotEmpty(version))
        this.version = version;
    }

    public void setLt(String lt) {
        if(StringUtils.isNotEmpty(lt))
        this.lt = lt;
    }

    public void setGt(String gt) {
        if(StringUtils.isNotEmpty(gt))
        this.gt = gt;
    }

    public void setEq(String eq) {
        if(StringUtils.isNotEmpty(eq))
        this.eq = eq;
    }

    public void setInterval(String interval) {
        if(StringUtils.isNotEmpty(interval))
        this.interval = interval;
    }

    public void setMatch(String match) {
        if(StringUtils.isNotEmpty(match))
        this.match = match;
    }

    public  String makeLogicSign(){
        if(Objects.equals("compare",getType())){
            if(StringUtils.isNotEmpty(gt)){
                if(StringUtils.isNotEmpty(eq)){
                    logicSign = "ge";
                    return "ge";
                }
                logicSign = "gt";
                return "gt";
            }else if(StringUtils.isNotEmpty(lt)){
                if(StringUtils.isNotEmpty(eq)){
                    logicSign = "le";
                    return "le";
                }
                logicSign = "lt";
                return "lt";
            }else{
                logicSign = "eq";
                return "eq";
            }
        }else if(Objects.equals("equal",type)){
            logicSign = "eq";
            return "eq";
        }else if(Objects.equals("notEqual",type)){
            logicSign = "neq";
            return "neq";
        }
        return StringUtils.EMPTY;
    }
}
