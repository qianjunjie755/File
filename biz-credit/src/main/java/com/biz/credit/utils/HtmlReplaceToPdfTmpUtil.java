package com.biz.credit.utils;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;


public class HtmlReplaceToPdfTmpUtil {

    public static void main(String[] args) throws IOException {
        String filePath = args[0];
        Document dom = Jsoup.parse(new File(filePath), "utf-8");
        Elements eles = dom.getElementsByTag("table");
        int ret=0;
        for(int i =0;i<eles.size();i++){
            Element ele = eles.get(i);
            if(ele.html().contains("被执行人")&&ele.html().contains("失信被执行人")&&!ele.html().contains("司法负面规则名称")){
                ret = ret + 1;
                ele.remove();
            }
            if(ele.html().contains("地址验证")){
                ret = ret + 1;
                ele.remove();
            }
        }
        if(ret>0){
            FileUtils.write(new File(filePath),dom.html(),"utf-8",false);
        }
        System.out.println(ret);

    }
}
