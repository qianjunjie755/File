package com.biz.model.radar.chart.v10;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RadarChartCore {
    private static final String RESULT = "result";
    private static final String SCORE = "score";
    private static final String MIN = "min";
    private static final String MAX = "max";

    public Map<String,Double> score(Map<String,String> params){
        Map<String,Double> result = new HashMap<>();

        Map<String,Map<String,String>> paramValueMap = new HashMap<>();
        paramValueMap.put(ParamConst.COMPANY_QUALIFICATION,new HashMap<>());
        paramValueMap.put(ParamConst.COMPANY_CAPACITY,new HashMap<>());
        paramValueMap.put(ParamConst.COMPANY_STABILITY,new HashMap<>());
        paramValueMap.put(ParamConst.COMPANY_HONESTY,new HashMap<>());
        paramValueMap.put(ParamConst.COMPANY_PERSON,new HashMap<>());

        params.forEach((k,v)->{
            paramValueMap.get(ParamConst.PARAM_ITEM_GROUP.get(k)).put(k,v);
        });
        getGroupScore(paramValueMap,result);


        return result;
    }


    public void getGroupScore(Map<String,Map<String,String>> paramValueMap,Map<String,Double> result){
        paramValueMap.forEach((groupName,paramValues)->{
            Map<String,BigDecimal> groupResult = new HashMap<>();
            groupResult.put(SCORE,new BigDecimal(0));
            BigDecimal min = ParamConst.RADAR_EXTREME.get(groupName).get(ParamConst.MIN);
            //Double max = ParamConst.RADAR_EXTREME.get(groupName).get(ParamConst.MAX);
            BigDecimal span = ParamConst.RADAR_EXTREME.get(groupName).get(ParamConst.SPAN);
            paramValues.forEach((itemName,value)->{
                if(StringUtils.equals("true",value)||StringUtils.equals("false",value)){
                    value = String.valueOf(BooleanUtils.toInteger(!Boolean.parseBoolean(value)));
                }
                log.info("item socre start[{}]:{}",itemName,value);
                BigDecimal val = StringUtils.isNotEmpty(value)?new BigDecimal(value):new BigDecimal(-1*Double.MAX_VALUE).add(new BigDecimal(1));
                for(int i=0;i<ParamConst.RADAR_PARAM_RANGE.get(groupName).get(itemName).size()-1;i++){
                    BigDecimal left = ParamConst.RADAR_PARAM_RANGE.get(groupName).get(itemName).get(i);
                    BigDecimal right = ParamConst.RADAR_PARAM_RANGE.get(groupName).get(itemName).get(i+1);
                    /*if(ParamConst.COMPANY_PERSON.equals(groupName)){
                        if(left.compareTo(val)<0&&val.compareTo(right)<=0){
                            groupResult.put(SCORE,groupResult.get(SCORE).add(ParamConst.RADAR_PARAM_SCORE.get(groupName).get(itemName).get(i)));
                            log.info("item:{};score:{}",itemName,ParamConst.RADAR_PARAM_SCORE.get(groupName).get(itemName).get(i));
                        }
                    }else{
                        if(left.compareTo(val)<=0&&val.compareTo(right)<0){
                            groupResult.put(SCORE,groupResult.get(SCORE).add(ParamConst.RADAR_PARAM_SCORE.get(groupName).get(itemName).get(i)));
                            log.info("item:{};score:{}",itemName,ParamConst.RADAR_PARAM_SCORE.get(groupName).get(itemName).get(i));
                        }
                    }*/

                    if(left.compareTo(val)<=0&&val.compareTo(right)<0){
                        groupResult.put(SCORE,groupResult.get(SCORE).add(ParamConst.RADAR_PARAM_SCORE.get(groupName).get(itemName).get(i)));
                        log.info("item:{};score:{}",itemName,ParamConst.RADAR_PARAM_SCORE.get(groupName).get(itemName).get(i));
                    }
                }
            });
            log.info("group:{};score:{}",groupName,groupResult.get(SCORE));
            result.put(groupName,((((groupResult.get(SCORE).subtract(min)).divide(span,30, BigDecimal.ROUND_HALF_EVEN)).add(new BigDecimal(0.25))).divide(new BigDecimal(1.25),30, BigDecimal.ROUND_HALF_EVEN)).doubleValue());
            //result.put(groupName,(((groupResult.get(SCORE)-min)/span)+0.25)/1.25);
        });
    }

}
