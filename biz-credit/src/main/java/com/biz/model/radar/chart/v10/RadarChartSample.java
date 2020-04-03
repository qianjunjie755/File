package com.biz.model.radar.chart.v10;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadarChartSample {

    public static void main(String[] args) {


        String str="{\"JudgmentDoc_1\":\"1\",\"SharesImpawnTimes\":\"0\",\"CaseTimes\":\"0\",\"JudgmentDocTimes\":\"3\",\"FrInvRegCapMax\":\"\",\"AlterTimes\":\"15\",\"FrInvEsDateMin\":\"2.71\",\"Alter_6_12\":\"0\",\"AlsM12IdRelAllnum\":\"0\",\"FrInvSubConAmtMin\":\"50.0\",\"RegCap\":\"1000.0\",\"EsDateDetailYear\":\"7.81\",\"AlsLstIdBankInteday\":\"0\",\"PersonNum\":\"3\",\"FrInvFundedRatioMax\":\"100.0\",\"FrInvFundedRatioAll\":\"240.0\",\"JudgmentDoc_12\":\"3\",\"Alter_1_12\":\"0\",\"EntStatus\":\"false\",\"FrInvRegCapMin\":\"\",\"AlterDetalDaysMin\":\"1792\",\"AlsM3IdCaonOrgnum\":\"0\",\"FrInvFundedRatioAvg\":\"60.0\",\"AlsM1IdNbankP2pAllnum\":\"0\",\"AlsM12IdNbankP2pAllnum\":\"0\",\"Alter_3\":\"1\",\"Alter_4\":\"0\",\"Alter_1\":\"3\",\"Alter_8\":\"0\",\"FrInvEsDateMax\":\"7.81\",\"FiliationNum\":\"0\",\"FrInvTimes\":\"4\",\"FrInvSubConAmtAll\":\"1450.0\",\"AlsLstIdNbankInteday\":\"0\"}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        Map<String,String> map = new HashMap<>();
        ParamConst.PARAM_ITEM_GROUP.forEach((item,group)->{
            map.put(item,jsonObject.getString(item));
        });
        System.out.println(new RadarChartCore().score(map));

    }

    public static void main1(String[] args) throws IOException {
        List<String> strings = FileUtils.readLines(new File("D:\\biz_credit\\model\\target\\system218.out"),"UTF-8");
        List<String> result = new ArrayList<>();
        strings.forEach(s->{
            String str = s.length()>=46?s.substring(46):s;
            if(str.startsWith("jsonData")||str.startsWith("item")||str.startsWith("group")||str.startsWith("result")){
                //System.out.println(str.substring(0,str.lastIndexOf("[")));
                if(result.size()==0||(result.size()>0&&!result.get(result.size()-1).equals(str.substring(0,str.lastIndexOf("[")))))
                    result.add(str.substring(0,str.lastIndexOf("[")).concat("\n"));
            }
        });

        List<String> strings2 = FileUtils.readLines(new File("D:\\biz_credit\\model\\target\\system219.out"),"UTF-8");

        strings2.forEach(s->{
            String str = s.length()>=46?s.substring(46):s;
            if(str.startsWith("jsonData")||str.startsWith("item")||str.startsWith("group")||str.startsWith("result")){
                //System.out.println(str.substring(0,str.lastIndexOf("[")));
                if(result.size()==0||(result.size()>0&&!result.get(result.size()-1).equals(str.substring(0,str.lastIndexOf("[")))))
                    result.add(str.substring(0,str.lastIndexOf("[")).concat("\n"));
            }
        });
        result.forEach(s -> {
            System.out.println(s);
        });
        FileUtils.writeLines(new File("D:\\biz_credit\\model\\target\\result.txt"),result);
        //System.out.println("[2019-04-12 15:05:17.020][INFO ][Thread-56] - ".length());//46
        //System.out.println("[RadarChartCore:48]".length()); //19
    }
}
