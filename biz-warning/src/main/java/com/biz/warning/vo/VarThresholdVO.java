package com.biz.warning.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class VarThresholdVO implements Serializable {

    private String prodCode;
    private String version = "1.0";
    private String varset;
    private String varsetVersion;
    private String type;
    private String lt = StringUtils.EMPTY;
    private String gt = StringUtils.EMPTY;
    private String eq = StringUtils.EMPTY;
    private String threshold;
    private String interval = "-1";
    private String match = StringUtils.EMPTY;
    private String logicSign = StringUtils.EMPTY;
    private Integer ruleId;

    public VarThresholdVO(String prodCode, String version,String varset,String varsetVersion, String type, String lt, String gt, String eq, String threshold, String interval, String match,Integer ruleId) {
        setProdCode(prodCode);
        setVersion(version);
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
