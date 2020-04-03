package com.biz.chart.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class ExcelUtils {
    private ExcelUtils() {
    }

    public static Map<String, Object> convertBean(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj));
    }

    public static <T> List<Map<String, Object>> convertListBean(List<T> objectList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object obj : objectList) {
            resultList.add(convertBean(obj));
        }
        return resultList;
    }

    public static <T> XSSFWorkbook getWorkbook(List<T> objectList, Map<String, String> rowMapper) {
        List<Map<String, Object>> exportData = convertListBean(objectList);
        return createExcel(exportData, rowMapper);
    }

    public static XSSFWorkbook createExcel(List<Map<String, Object>> exportData, Map<String, String> rowMapper) {
        XSSFWorkbook wb = new XSSFWorkbook();
        // 在webbook中添加一个sheet
        XSSFSheet sheet = wb.createSheet();
        // 在sheet中添加表头第0行, 注意老版本poi对Excel的行数列数有限制short
        XSSFRow row = sheet.createRow(0);
        // 创建单元格，并设置值表头
        XSSFCellStyle style = wb.createCellStyle();
        //给表头赋值
        Set<String> keys = rowMapper.keySet();
        int i = 0;
        for (String key : keys) {
            XSSFCell cell = row.createCell((short) i);
            String value = rowMapper.get(key).toString();
            cell.setCellValue(value);
            cell.setCellStyle(style);
            i++;
            sheet.setColumnWidth(i, value.length() * 256 * 4);
        }
        //写入实体数据 实际应用中这些数据从数据库得到
        if (exportData != null) {
            for (int j = 0; j < exportData.size(); j++) {
                row = sheet.createRow(j + 1);
                // 第四步，创建单元格，并设置值
                Map<String, Object> rows = exportData.get(j);
                int a = 0;
                for (String exportKey : keys) {
                    String[] keyArray = exportKey.split("\\+");
                    String value = "";
                    for (String key : keyArray) {
                        if (rows.get(key) != null) {
                            value += rows.get(key).toString();
                        }
                    }
                    row.createCell((short) a).setCellValue(value);
                    a++;
                }
            }
        }
        return wb;
    }

    public static String getCellValue(Cell cell){
        String cellValue = StringUtils.EMPTY;
        if(cell == null){
            return cellValue;
        }
        CellType typeEnum = cell.getCellTypeEnum();

        if(typeEnum==CellType.NUMERIC){
            cellValue = stringDateProcess(cell);
        }else if(typeEnum==CellType.STRING){
            cellValue = String.valueOf(cell.getStringCellValue()).trim();
        }else if(typeEnum==CellType.BOOLEAN){
            cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
        }else if(typeEnum==CellType.FORMULA){
            try {
                cellValue = String.valueOf(cell.getNumericCellValue()).trim();
            }catch (Exception e){
                try{
                    cellValue = String.valueOf(cell.getStringCellValue()).trim();
                }catch (Exception e1){
                    log.error(e1.getMessage(),e1);
                }
            }

        }else if(typeEnum==CellType.BLANK){
            cellValue = StringUtils.EMPTY;
        }else if(typeEnum==CellType.ERROR){
            cellValue = "非法字符";
        }else{
            cellValue = "未知类型";
        }
        return cellValue;
    }
    /**
     * 格式处理
     * @return
     */
    public static String stringDateProcess(Cell cell){
        String result = new String();
        if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
            SimpleDateFormat sdf = null;
            if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                sdf = new SimpleDateFormat("HH:mm");
            } else {// 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            }
            Date date = cell.getDateCellValue();
            result = sdf.format(date);
        } else if (cell.getCellStyle().getDataFormat() == 58) {
            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            double value = cell.getNumericCellValue();
            Date date = org.apache.poi.ss.usermodel.DateUtil
                    .getJavaDate(value);
            result = sdf.format(date);
        } else {
            double value = cell.getNumericCellValue();
            CellStyle style = cell.getCellStyle();
            DecimalFormat format = new DecimalFormat();
            String temp = style.getDataFormatString();
            // 单元格设置成常规
            /*if (temp.equals("General")) {
                format.applyPattern("#");
            }*/
            format.applyPattern("#");
            result = format.format(value);
        }

        return result;
    }
}
