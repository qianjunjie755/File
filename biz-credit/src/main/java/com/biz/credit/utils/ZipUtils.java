package com.biz.credit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private ZipUtils(){
    }

    public static void doCompress(String keyNo,String srcFile, String zipFile) throws IOException {
        doCompress(keyNo,new File(srcFile), new File(zipFile));
    }

    /**
     * 文件压缩
     * @param srcFile 目录或者单个文件
     * @param zipFile 压缩后的ZIP文件
     */
    public static void doCompress(String keyNo,File srcFile, File zipFile) throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            doCompress(keyNo,srcFile, out);
        } catch (Exception e) {
            throw e;
        } finally {
            out.close();//记得关闭资源
        }
    }

    public static void doCompress(String keyNo,String filelName, ZipOutputStream out) throws IOException{
        doCompress(keyNo,new File(filelName), out);
    }

    public static void doCompress(String keyNo,File file, ZipOutputStream out) throws IOException{
        doCompress(keyNo,file, out, "");
    }

    public static void doCompress(String keyNo,File inFile, ZipOutputStream out, String dir) throws IOException {
        if ( inFile.isDirectory() ) {
            File[] files = inFile.listFiles();
            if (files!=null && files.length>0) {
                for (File file : files) {
                    String name =  inFile.getName();
                    if(keyNo!=null && !"".equals(keyNo)){
                        name=keyNo+".pdf";
                    }

                    if (!"".equals(dir)) {
                        name = dir + "/" + name;
                    }
                    ZipUtils.doCompress(keyNo,file, out, name);
                }
            }
        } else {
            ZipUtils.doZip(keyNo,inFile, out, dir);
        }
    }

    public static void doZip(String keyNo,File inFile, ZipOutputStream out, String dir) throws IOException {
        String entryName = null;
        if (!"".equals(dir)) {
            if(keyNo!=null && !"".equals(keyNo)){
                entryName = dir + "/" + keyNo+".pdf";
            }else {
                entryName = dir + "/" + inFile.getName();
            }

        } else {
            if(keyNo!=null && !"".equals(keyNo)){
                entryName = keyNo+".pdf";
            }else {
                entryName = inFile.getName();
            }

        }
        ZipEntry entry = new ZipEntry(entryName);
        out.putNextEntry(entry);

        int len = 0 ;
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(inFile);
        while ((len = fis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
            out.flush();
        }
        out.closeEntry();
        fis.close();
    }

    public static void main(String[] args) throws IOException {
        doCompress(null,"D:/java/", "D:/java.zip");
    }

}
