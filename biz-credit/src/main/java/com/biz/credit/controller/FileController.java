package com.biz.credit.controller;

import com.biz.credit.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

@Slf4j
@RestController
public class FileController {

    /**
     * 单文件上传
     * 单个文件大小不能超过10Mb
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("/file/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String saveFileName = file.getOriginalFilename();
        File saveFile = new File(request.getSession().getServletContext().getRealPath("/upload/") + saveFileName);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        if (!file.isEmpty()) {
            try {
                //解析excel数据
                List<String[]> excelDataList= ExcelUtil.getExcelData(file);
            } catch (IOException e) {
                log.error(saveFile.getName() + " excel数据解析失败");
                return "excel数据解析失败：" + e.getMessage();
            }

            //将excel文件存放到指定目录
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
                out.write(file.getBytes());
                out.flush();
                out.close();
                log.info(saveFile.getName() + " 上传成功");
                return saveFile.getName() + " 上传成功";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                log.error(saveFile.getName() + " 上传失败");
                return "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                log.error(saveFile.getName() + " 上传失败");
                return "上传失败," + e.getMessage();
            }
        } else {
            log.error("上传失败");
            return "上传失败，因为文件为空.";
        }
    }

    /**
     * 多文件上传
     * 总上传文件的数据大小不能超过10Mb
     * @param request
     * @return
     */
    @RequestMapping("/file/uploadFiles")
    @ResponseBody
    public String uploadFiles(HttpServletRequest request) throws IOException {
        File savePath = new File(request.getSession().getServletContext().getRealPath("/upload/"));
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    File saveFile = new File(savePath, file.getOriginalFilename());
                    stream = new BufferedOutputStream(new FileOutputStream(saveFile));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    if (stream != null) {
                        stream.close();
                        stream = null;
                    }
                    return "第 " + i + " 个文件上传有错误" + e.getMessage();
                }
            } else {
                return "第 " + i + " 个文件为空";
            }
        }
        return "所有文件上传成功";
    }
}
