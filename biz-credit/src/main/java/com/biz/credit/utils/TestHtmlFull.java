package com.biz.credit.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.UUID;

public class TestHtmlFull {
    /**
     * 测试生成html
     *
     * data--html数据源
     * htmlTemplateName--模板名称
     */
    public void test_htmlFull(JSONObject data, String tempalteFullPath) throws Exception{
        //读取模板被渲染后的html完整文件内容
        String htmlContext = getHtmlContentFull(data, tempalteFullPath);

        //将html文件内容 生成html文件
        String uuid = UUID.randomUUID().toString();
        String htmlPath ="D:/workspace/htmltopdf_files/report/html/"+uuid +".html";
        FileUtils.write(new File(htmlPath), htmlContext, "UTF-8");

        System.out.println(htmlPath);
    }

    /**
     * 读取渲染后的html文件内容
     *
     * @param data
     * @return
     */
    public static String getHtmlContentFull(JSONObject data, String tempalteFullPath) throws Exception {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        String str = "";
        StringBuffer templateStr = null;
        try {
            //读取html模板
            FileInputStream fileInputStream = new FileInputStream(new File(tempalteFullPath));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            templateStr = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                templateStr.append(str);
            }
        } catch (Exception e) {
            throw e;
        }
        String da = "";
        try {
            Context context = new Context();
            data.put("myPattern", new Constants());

            context.setVariables(data);
            //将渲染后的html文件内容以字符串形式输出
            da = templateEngine.process(templateStr.toString(), context);
        } catch (Exception e) {
            throw e;
        }
        return da;
    }



    public static void main(String[] args) {
        try {
            JSONObject data=new JSONObject();
            data.put("reportData",new JSONObject());//初始化顶级父节点

            //工商信息
            JSONObject businessInfo =new JSONObject();
            JSONObject businessInfoObject1 = new JSONObject();
            businessInfoObject1.put("key","企业名称");
            businessInfoObject1.put("value","苏州蒂玛克机械有限公司");
            businessInfo.put("entName",businessInfoObject1);
            JSONObject businessInfoObject2 = new JSONObject();
            businessInfoObject2.put("key","统一社会信用代码");
            businessInfoObject2.put("value","91320594571440050T");
            businessInfo.put("creditCode",businessInfoObject2);
            JSONObject businessInfoObject3 = new JSONObject();
            businessInfoObject3.put("key","法定代表人");
            businessInfoObject3.put("value","李玉");
            businessInfo.put("frName",businessInfoObject3);
            businessInfo.put("businessInfo","businessInfo_1");
            data.getJSONObject("reportData").put("businessInfoObject",businessInfo);

            //企业年报
            JSONObject annualReportList = new JSONObject();
            LinkedList headList = new LinkedList();
            //headList.add("序号");
            headList.add("报送年度");
            headList.add("公示日期");
            annualReportList.put("headList",headList);
            LinkedList bodyList = new LinkedList();//size<=20
            int count=0;
            while (count<4){
                count++;
                LinkedList bodyItemList = new LinkedList();
                //bodyItemList.add(count);
                bodyItemList.add("2016");//如果有空 需要补位
                bodyItemList.add("2016-08-19");
                bodyList.add(bodyItemList);
            }
            annualReportList.put("bodyList",bodyList);
            data.getJSONObject("reportData").put("annualReportList",annualReportList);

            //企业强规则
            JSONObject businessStrongRule= new JSONObject();
            //工商照面身份验证
            JSONObject businessInfoStrongRule = new JSONObject();
            LinkedList businessInfoHead  = new LinkedList();
            businessInfoHead.add("工商照面身份验证规则名称");
            businessInfoHead.add("权重");
            businessInfoHead.add("命中");
            businessInfoStrongRule.put("headList",businessInfoHead);
            LinkedList businessInfoBodyList = new LinkedList();
            LinkedList businessInfoBody = new LinkedList();
            businessInfoBody.add("企业四要素验证-公司名称不一致");
            businessInfoBody.add("100");
            businessInfoBody.add("是");
            businessInfoBodyList.add(businessInfoBody);
            LinkedList businessInfoBody2 = new LinkedList();
            businessInfoBody2.add("法人不为实际控制人");
            businessInfoBody2.add("80");
            businessInfoBody2.add("是");
            businessInfoBodyList.add(businessInfoBody2);
            LinkedList businessInfoBody3 = new LinkedList();
            businessInfoBody3.add("环保等级低");
            businessInfoBody3.add("900");
            businessInfoBody3.add("是");
            businessInfoBodyList.add(businessInfoBody3);
            businessInfoStrongRule.put("bodyList",businessInfoBodyList);
            businessStrongRule.put("businessInfoStrongRule",businessInfoStrongRule);

            //准入身份查询
            JSONObject accessInfoStrongRule = new JSONObject();
            LinkedList accessInfoHead  = new LinkedList();
            accessInfoHead.add("准入身份查询规则名称");
            accessInfoHead.add("权重");
            accessInfoHead.add("命中");
            accessInfoStrongRule.put("headList",accessInfoHead);
            LinkedList accessInfoBodyList = new LinkedList();
            LinkedList accessInfoBody = new LinkedList();
            accessInfoBody.add("注册资本范围");
            accessInfoBody.add("60");
            accessInfoBody.add("是");
            accessInfoBodyList.add(accessInfoBody);
            LinkedList accessInfoBody2 = new LinkedList();
            accessInfoBody2.add("成立年限小于1年");
            accessInfoBody2.add("40");
            accessInfoBody2.add("是");
            accessInfoBodyList.add(accessInfoBody2);
            accessInfoStrongRule.put("bodyList",accessInfoBodyList);
            businessStrongRule.put("accessInfoStrongRule",accessInfoStrongRule);
            data.getJSONObject("reportData").put("businessStrongRule",businessStrongRule);

            //裁判文书
            LinkedList bizJudgmentDocBodyList = new LinkedList();
            JSONObject bizJudgmentDoc =new JSONObject();
            JSONObject bizJudgmentDocItem1 = new JSONObject();
            bizJudgmentDocItem1.put("key","判决案号");
            bizJudgmentDocItem1.put("value","（2017）粤0305民初14716-14736号");
            bizJudgmentDoc.put("caseNo",bizJudgmentDocItem1);

            JSONObject bizJudgmentDocItem2 = new JSONObject();
            bizJudgmentDocItem2.put("key","案件类型");
            bizJudgmentDocItem2.put("value","民事");
            bizJudgmentDoc.put("caseType",bizJudgmentDocItem2);

            JSONObject bizJudgmentDocItem3 = new JSONObject();
            bizJudgmentDocItem3.put("key","案件名称");
            bizJudgmentDocItem3.put("value","48腾讯科技（深圳）有限公司与阿里巴巴（杭州）文化创意有限公司北京锤子数码科技有限公司锤子科技（北京）股份有限公司侵害作品信息网络传播权纠纷一审准许或不准许撤诉民事裁定书");
            bizJudgmentDoc.put("caseName",bizJudgmentDocItem3);

            JSONObject bizJudgmentDocItem4 = new JSONObject();
            bizJudgmentDocItem4.put("key","裁判日期");
            bizJudgmentDocItem4.put("value","2017-09-12");
            bizJudgmentDoc.put("judgementDate",bizJudgmentDocItem4);

            JSONObject bizJudgmentDocItem5 = new JSONObject();
            bizJudgmentDocItem5.put("key","执行法院");
            bizJudgmentDocItem5.put("value","深圳市南山区人民法院");
            bizJudgmentDoc.put("courtName",bizJudgmentDocItem5);

            JSONObject bizJudgmentDocItem6 = new JSONObject();
            bizJudgmentDocItem6.put("key","判决结果");
            bizJudgmentDocItem6.put("value","准许原告腾讯科技（深圳）有限公司撤诉。 本系列案各案受理费人民币25元，各案减半收取计人民币12.5元，由原告腾讯科技（深圳）有限公司负担。");
            bizJudgmentDoc.put("judgeResult",bizJudgmentDocItem6);
            bizJudgmentDocBodyList.add(bizJudgmentDoc);

            JSONObject bizJudgmentDoc2 =new JSONObject();
            JSONObject bizJudgmentDocItem21 = new JSONObject();
            bizJudgmentDocItem21.put("key","判决案号");
            bizJudgmentDocItem21.put("value","（2017）粤0305民初15954-15975号");
            bizJudgmentDoc2.put("caseNo",bizJudgmentDocItem21);

            JSONObject bizJudgmentDocItem22 = new JSONObject();
            bizJudgmentDocItem22.put("key","案件类型");
            bizJudgmentDocItem22.put("value","民事案件");
            bizJudgmentDoc2.put("caseType",bizJudgmentDocItem22);

            JSONObject bizJudgmentDocItem23 = new JSONObject();
            bizJudgmentDocItem23.put("key","案件名称");
            bizJudgmentDocItem23.put("value","02深圳市腾讯计算机系统有限公司与阿里巴巴（杭州）文化创意有限公司北京锤子数码科技有限公司锤子科技（北京）股份有限公司侵害作品信息网络传播权纠纷一审准许或不准许撤诉民事裁定书");
            bizJudgmentDoc2.put("caseName",bizJudgmentDocItem23);

            JSONObject bizJudgmentDocItem24 = new JSONObject();
            bizJudgmentDocItem24.put("key","裁判日期");
            bizJudgmentDocItem24.put("value","2018-09-12");
            bizJudgmentDoc2.put("judgementDate",bizJudgmentDocItem24);

            JSONObject bizJudgmentDocItem25 = new JSONObject();
            bizJudgmentDocItem25.put("key","执行法院");
            bizJudgmentDocItem25.put("value","深圳市南山区人民法院");
            bizJudgmentDoc2.put("courtName",bizJudgmentDocItem25);

            JSONObject bizJudgmentDocItem26 = new JSONObject();
            bizJudgmentDocItem26.put("key","判决结果");
            bizJudgmentDocItem26.put("value","准许原告深圳市腾讯计算机系统有限公司撤回起诉。 各案受理费人民币25元，由原告承担，本院减半收取为人民币12.5元，各案余款退还原告。");
            bizJudgmentDoc2.put("judgeResult",bizJudgmentDocItem26);
            bizJudgmentDocBodyList.add(bizJudgmentDoc2);
            data.getJSONObject("reportData").put("bizJudgmentDocList",bizJudgmentDocBodyList);

            System.out.println(data);
            String tempalteFullPath="D:/workspace/htmltopdf_files/report/template_full.html";
            //读取模板被渲染后的html完整文件内容
            String htmlContext = getHtmlContentFull(data, tempalteFullPath);

            //将html文件内容 生成html文件
            String uuid = UUID.randomUUID().toString();
            String htmlPath ="D:/workspace/htmltopdf_files/report/html/"+uuid +".html";
            FileUtils.write(new File(htmlPath), htmlContext, "UTF-8");

            System.out.println(htmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
