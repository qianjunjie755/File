package com.biz.credit.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
public class ReportDetailController {

    @Value("${biz.decision.report.html-pdf-files-path}")
    private  String htmlPdfFilesPath;

    /**
     *get请求
     * @param request -pdfFilePath 下载pdf的服务器路径
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/reportDetail/download")
    public ResponseEntity<byte[]> download(HttpServletRequest request) throws IOException {
        String pdfFilePath=request.getParameter("pdfFilePath");
        String key_no=request.getParameter("key_no");
        if(key_no!=null && !"".equals(key_no)){
            key_no=URLDecoder.decode(request.getParameter("key_no"),"utf-8");
        }else {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            key_no=sdf.format(date);
        }

        File file=new File(pdfFilePath);
        if (!file.exists()){
            log.info("pdf文件不存在");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        String fileName=new String((key_no+".pdf").getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }

    /**
     *id -- 生成的pdf文件名称
     * company_name 公司名称
     * @param request -
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/reportDetail/downloadFileApi")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) throws IOException {
        String id = request.getParameter("id");
        String pdfFilePath = htmlPdfFilesPath + "pdf/" + id + ".pdf";//pdf文件名
        String company_name = request.getParameter("companyName");
        if (id == null || "".equals(id) || company_name == null || "".equals(company_name)) {
            log.info("id或者companyName不能为空");
            return null;
        }
        if (company_name != null && !"".equals(company_name)) {
            company_name = URLDecoder.decode(request.getParameter("companyName"), "utf-8");
        } else {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            company_name = sdf.format(date);
        }

        File file = new File(pdfFilePath);
        if (!file.exists()) {
            log.info("pdf文件不存在");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        String fileName = new String((company_name + ".pdf").getBytes("UTF-8"), "iso-8859-1");//为了解决中文名称乱码问题
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }

}
