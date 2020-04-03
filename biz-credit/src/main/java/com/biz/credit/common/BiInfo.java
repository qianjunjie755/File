package com.biz.credit.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BiInfo {
    String AppId = "";//請求唯一id
    String isInvalidStrongRule = "false";//入参是否完完整
    LinkedList countApiList = new LinkedList();//记录有返回值的接口数量 size
    LinkedList hitCountList = new LinkedList();//命中且权重为100 的个数 size
    LinkedList hitCount2List = new LinkedList();//命中个数 size（包含各种权重值）
    LinkedList unKnownHitCountList = new LinkedList();//无法判断命中且权重为100 的个数 size
    LinkedList ruleCountList = new LinkedList();//总共几个强规则
    //命中的强规则 LinkedList roleids
    LinkedList hitRoleids = new LinkedList();
    //未命中的强规则 LinkedList noHitRoleids
    LinkedList noHitRoleids = new LinkedList();
    //无法判断命中的强规则 LinkedList unknownRoleids
    LinkedList unknownRoleids = new LinkedList();

    //命中的强规则阈值map<roleid,hitValue>  hitValue=result.hit
    Map<String, String> hitValueRoleids = new HashMap<>();
    //未命中的强规则阈值map<roleid,hitValue>
    Map<String, String> noHitValueRoleids = new HashMap<>();
    //企业强规则 法人强规则map<api#type,roleid>
    Map<String, String> roleids = new HashMap<>();
    //基础数据部分 返回无结果的个数
    LinkedList resultNullList = new LinkedList();
    //基础数据部分模块总个数
    LinkedList detailModelTotalList = new LinkedList();

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getIsInvalidStrongRule() {
        return isInvalidStrongRule;
    }

    public void setIsInvalidStrongRule(String isInvalidStrongRule) {
        this.isInvalidStrongRule = isInvalidStrongRule;
    }

    public LinkedList getCountApiList() {
        return countApiList;
    }

    public void addCountApiList(String rule) {
        this.countApiList.add(rule);
    }

    public LinkedList getHitCountList() {
        return hitCountList;
    }

    public void addHitCountList(String rule) {
        this.hitCountList.add(rule);
    }

    public LinkedList getHitCount2List() {
        return hitCount2List;
    }

    public void addHitCount2List(String rule) {
        this.hitCount2List.add(rule);
    }

    public LinkedList getUnKnownHitCountList() {
        return unKnownHitCountList;
    }

    public void addUnKnownHitCountList(String rule) {
        this.unKnownHitCountList.add(rule);
    }

    public LinkedList getRuleCountList() {
        return ruleCountList;
    }

    public void addRuleCountList(String rule) {
        this.ruleCountList.add(rule);
    }

    public LinkedList getHitRoleids() {
        return hitRoleids;
    }

    public void addHitRoleids(String hitRoleids) {
        this.hitRoleids.add(hitRoleids);
    }

    public LinkedList getNoHitRoleids() {
        return noHitRoleids;
    }

    public void addNoHitRoleids(String noHitRoleids) {
        this.noHitRoleids.add(noHitRoleids);
    }

    public LinkedList getUnknownRoleids() {
        return unknownRoleids;
    }

    public void addUnknownRoleids(String unknownRoleids) {
        this.unknownRoleids.add(unknownRoleids);
    }

    public Map<String, String> getHitValueRoleids() {
        return hitValueRoleids;
    }

    public void addHitValueRoleids(String roleid,String hitValue ) {
        this.hitValueRoleids.put(roleid,hitValue);
    }

    public Map<String, String> getNoHitValueRoleids() {
        return noHitValueRoleids;
    }

    public void addNoHitValueRoleids( String roleid,String hitValue ) {
        this.noHitValueRoleids.put(roleid,hitValue);
    }

    public Map<String, String> getRoleids() {
        return roleids;
    }

    public void addRoleids(String api,String roleid) {
        this.roleids.put(api,roleid);
    }

    public LinkedList getResultNullList() {
        return resultNullList;
    }

    public void addResultNullList(String model) {
        this.resultNullList.add(model);
    }

    public LinkedList getDetailModelTotalList() {
        return detailModelTotalList;
    }

    public void addDetailModelTotalList(String model) {
        this.detailModelTotalList.add(model);
    }
}
