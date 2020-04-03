package com.biz.credit.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class testFiles {
    public static void main(String[] args) throws IOException {
        //doCompress(null,"D:/java/", "D:/java.zip");
        Map<String, Object> data = new HashMap<>();
        JSONObject s=new JSONObject();
        s.put("testData","thymeleafTemplateTest");
        data.put("report",s);
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        String str = "";
        String htmlContext = "";
        StringBuffer templateStr = null;
        try {
            //随机生成html、pdf文件名
            String htmlPath = "";
            //确认html、pdf文件目录是否存在，如果不存在，则新建
            htmlPath = "D:/workspace/htmltopdf_files/report/";
            File fileHtml = new File(htmlPath);
            if (!fileHtml.exists()) {
                fileHtml.mkdirs();
            }
            //读取html模板
            FileInputStream fileInputStream = new FileInputStream(new File("D:/workspace/htmltopdf_files/report/thymeleafTest.html"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            templateStr = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                templateStr.append(str);
            }

            Context context = new Context();
            data.put("myPattern", new Constants());
            context.setVariables(data);
            //将渲染后的html文件内容以字符串形式输出
            htmlContext = templateEngine.process(templateStr.toString(), context);
        } catch (Exception e) {
            throw e;
        }

        //生成html临时文件
        FileUtils.write(new File("D:/workspace/htmltopdf_files/report/"+UUID.randomUUID()+".html"), htmlContext, "UTF-8");

    }

}
