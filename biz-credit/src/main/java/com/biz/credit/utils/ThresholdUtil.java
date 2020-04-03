package com.biz.credit.utils;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.Expression;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class ThresholdUtil {

    public int checkRule(Object result,String threshold){
        try{
            if(StringUtils.isNotEmpty(threshold)&&null!=result&&StringUtils.isNotEmpty(result.toString())){
                JSONObject thresholdJson = JSONObject.parseObject(threshold);
                String type = thresholdJson.getString("type");
                if(StringUtils.equals("compare",type)){
                    Object eqValue = thresholdJson.get("eq");
                    Object gtValue = thresholdJson.get("gt");
                    Object ltValue = thresholdJson.get("lt");
                    if(null!=eqValue&&customCompare(eqValue,result)==0){
                        return 1;
                    }
                    if(null!=gtValue&&customCompare(gtValue,result)>0){
                        return 1;
                    }
                    if(null!=ltValue&&customCompare(result,ltValue)>0){
                        return 1;
                    }
                }else if(StringUtils.equals("equal",type)){
                    Object eqValue = thresholdJson.get("eq");
                    if(null!=eqValue&&customCompare(result,eqValue)==0){
                        return 1;
                    }
                }
            }else{
                return -1;
            }
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }

    public String getEnum(Object result,String threshold){
        String ret = StringUtils.EMPTY;
        if(StringUtils.isNotEmpty(threshold)&&null!=result&&StringUtils.isNotEmpty(result.toString())){
            JSONObject jsonObject = JSONObject.parseObject(threshold);
            if(StringUtils.equals("enum",jsonObject.getString("type"))){
                JSONObject enumJsonObject = jsonObject.getJSONObject("enum");
                return enumJsonObject.getString(result.toString());
            }
        }
        return ret;
    }
    /**
     * 表达式转换
     *
     * @param expr
     * @return
     */
    public static String convert(Expression expr) {
        if (expr == null || (expr.getLeft() == null && expr.getRight() == null)) {
            return null;
        }
        String expression;
        if (expr.getLeft() != null && expr.getRight() != null) {
            char s, e;
            if (ObjectUtils.equals(expr.getLeftExpr(), ">=") || ObjectUtils.equals(expr.getLeftExpr(), "<=")) {
                s = '[';
            } else if (ObjectUtils.equals(expr.getLeftExpr(), ">") || ObjectUtils.equals(expr.getLeftExpr(), "<")) {
                s = '(';
            } else {
                throw new RuntimeException("表达式'" + expr + "'不正确!!");
            }
            if (ObjectUtils.equals(expr.getRightExpr(), ">=") || ObjectUtils.equals(expr.getRightExpr(), "<=")) {
                e = ']';
            } else if (ObjectUtils.equals(expr.getRightExpr(), ">") || ObjectUtils.equals(expr.getRightExpr(), "<")) {
                e = ')';
            } else {
                throw new RuntimeException("表达式'" + expr + "'不正确!!");
            }
            expression = s + expr.getLeft() + "," + expr.getRight() + e;
            if (!NumberUtils.isNumber(expr.getLeft()) || !NumberUtils.isNumber(expr.getRight())) {
                throw new RuntimeException("表达式'" + expression + "'配置不正确!!");
            }
        } else {
            if (expr.getLeft() == null) {
                if (Objects.equals(expr.getRightExpr(), "=") || Objects.equals(expr.getRightExpr(), "==")) {
                    expression = "=" + expr.getRight();
                } else if (Objects.equals(expr.getRightExpr(), "!=") || Objects.equals(expr.getRightExpr(), "<>")) {
                    expression = "!=" + expr.getRight();
                } else {
                    expression = expr.getRightExpr() + expr.getRight();
                    if (!NumberUtils.isNumber(expr.getRight())) {
                        throw new RuntimeException("表达式'" + expression + "'配置不正确!!");
                    }
                }
            } else {
                if (Objects.equals(expr.getLeftExpr(), "=") || Objects.equals(expr.getLeftExpr(), "==")) {
                    expression = expr.getLeft() + "=";
                } else if (Objects.equals(expr.getLeftExpr(), "!=") || Objects.equals(expr.getLeftExpr(), "<>")) {
                    expression = expr.getLeft() + "!=";
                } else {
                    expression = expr.getLeft() + expr.getLeftExpr();
                    if (!NumberUtils.isNumber(expr.getLeft())) {
                        throw new RuntimeException("表达式'" + expression + "'配置不正确!!");
                    }
                }
            }
        }
        return expression;
    }

    /**
     * 表达式转换
     *
     * @param judge
     * @return
     */
    public static Expression convert(String judge) {
        if (StringUtils.isBlank(judge)) {
            return null;
        }
        judge = StringUtils.trim(judge);
        Expression expression = new Expression();
        String[] values = org.springframework.util.StringUtils.tokenizeToStringArray(judge, "[](), ><!=");
        if (values.length == 2) {
            BigDecimal a = new BigDecimal(values[0]);
            BigDecimal b = new BigDecimal(values[1]);
            String expr1, expr2;
            char s = judge.charAt(0);
            char e = judge.charAt(judge.length() - 1);
            if (s == '[') {
                expr1 = a.compareTo(b) < 0 ? "<=" : ">=";
            } else if (s == '(') {
                expr1 = a.compareTo(b) < 0 ? "<" : ">";
            } else {
                throw new RuntimeException("表达式'" + judge + "'不正确!!");
            }
            if (e == ']') {
                expr2 = a.compareTo(b) < 0 ? "<=" : ">=";
            } else if (e == ')') {
                expr2 = a.compareTo(b) < 0 ? "<" : ">";
            } else {
                throw new RuntimeException("表达式'" + judge + "'不正确!!");
            }
            expression.setLeft(values[0]);
            expression.setLeftExpr(expr1);
            expression.setRight(values[1]);
            expression.setRightExpr(expr2);
        } else if (values.length == 1) {
            if (judge.indexOf(values[0]) == 0) {
                String expr1 = judge.substring(values[0].length());
                if (Objects.equals(expr1, "=") || Objects.equals(expr1, "==")) {
                    expr1 = "=";
                } else if (Objects.equals(expr1, "!=") || Objects.equals(expr1, "<>")) {
                    expression.setLeftExpr("!=");
                    expr1 = "!=";
                } else {
                    if (!NumberUtils.isNumber(values[0])) {
                        throw new RuntimeException("表达式'" + judge + "'配置不正确!!");
                    }
                }
                expression.setLeft(values[0]);
                expression.setLeftExpr(expr1);
            } else {
                String expr1 = judge.substring(0, judge.indexOf(values[0]));
                if (Objects.equals(expr1, "=") || Objects.equals(expr1, "==")) {
                    expr1 = "=";
                } else if (Objects.equals(expr1, "!=") || Objects.equals(expr1, "<>")) {
                    expr1 = "!=";
                } else {
                    if (!NumberUtils.isNumber(values[0])) {
                        throw new RuntimeException("表达式'" + judge + "'配置不正确!!");
                    }
                }
                expression.setRight(values[0]);
                expression.setRightExpr(expr1);
            }
        } else {
            throw new RuntimeException("表达式'" + judge + "'配置不正确!!");
        }
        return expression;
    }
    public String getThresholdValue(JSONObject thresholdJsonObject){
        String threshold = null;
        if(null!=thresholdJsonObject){
            String type = thresholdJsonObject.getString("type");
            String lt = thresholdJsonObject.getString("lt");
            String gt = thresholdJsonObject.getString("gt");
            String eq = thresholdJsonObject.getString("eq");
            if("compare".equals(type)){
                if(StringUtils.isNotEmpty(lt)){
                    threshold = lt;
                }else if(StringUtils.isNotEmpty(gt)){
                    threshold = gt;
                }else if(StringUtils.isNotEmpty(eq)){
                    threshold = eq;
                }
            } else if("equal".equals(type)){
                threshold = eq;
            }
        }
        return threshold;
    }


    public int customCompare(Object low,Object high){
        if(NumberUtils.isNumber(low.toString())){
            Double lowDouble = Double.parseDouble(low.toString());
            Double highDouble = Double.parseDouble(high.toString());
            return highDouble.compareTo(lowDouble);
        }
        return high.toString().compareTo(low.toString());
    }

    public static void main(String[] args) {
        /*JSONObject jo =  new JSONObject();
        jo.put("dd","1.02");
        System.out.println("-333.33333".compareTo("-333.33332"));
        Object d = 1;
        System.out.println(jo.get("dd") instanceof Double);*/
/*        System.out.println(NumberUtils.isNumber("300"));
        System.out.println(new ThresholdUtil().checkRule("F","{type:\"compare\",\"lt\":null,\"gt\":\"C\",\"eq\":\"C\"}"));
        System.out.println(new ThresholdUtil().checkRule("49","{type:\"compare\",\"lt\":null,\"gt\":\"50\",\"eq\":\"50\"}"));
        System.out.println(new ThresholdUtil().checkRule("3000.00","{type:\"compare\",\"lt\":\"50\",\"gt\":null,\"eq\":null}"));*/
        System.out.println(new ThresholdUtil().checkRule(1,"{type:\"equal\",\"lt\":null,\"gt\":null,\"eq\":\"true\"}"));
    }
}
