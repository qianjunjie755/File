package com.biz.credit.utils;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.utils.tools.CompanyValidator;
import com.biz.credit.utils.tools.IValidator;
import com.biz.credit.vo.ApiRequestVO;
import com.biz.credit.vo.ApiResponseVO;
import com.biz.credit.vo.ApiVO;
import com.biz.credit.vo.BiRuleDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

/**
 *解析excel文件数据
 */
@Slf4j
public class ExcelUtil {

    //private static final log log = log.getlog(ExcelData.class);
    public static List<String[]> getExcelData(MultipartFile file) throws IOException{
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getLastCellNum();
                    String[] cells = new String[row.getLastCellNum()];
                    //循环当前行
                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
        }
        return list;
    }


    public static Workbook ruleHitListWorkBookCreate(List<BiRuleDataVO> dataList){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellStyle cellStyle = workbook.createCellStyle();
        Font hssfFont = workbook.createFont();
        hssfFont.setBold(true);
        hssfFont.setFontHeightInPoints((short) 20);
        cellStyle.setFont(hssfFont);
        Row row= sheet.createRow(0);
        Cell cell = row.createCell(0);cell.setCellValue("序号");cell.setCellStyle(cellStyle);sheet.setColumnWidth(0,"序号".length()*256*4);
        Cell cell1 = row.createCell(1);cell1.setCellValue("规则名称");cell1.setCellStyle(cellStyle);sheet.setColumnWidth(1,"规则名称".length()*256*4);
        Cell cell2 = row.createCell(2);cell2.setCellValue("时间粒度");cell2.setCellStyle(cellStyle);sheet.setColumnWidth(2,"时间粒度".length()*256*4);
        Cell cell3 = row.createCell(3);cell3.setCellValue("判断条件");cell3.setCellStyle(cellStyle);sheet.setColumnWidth(3,"判断条件".length()*256*4);
        Cell cell4 = row.createCell(4);cell4.setCellValue("命中次数");cell4.setCellStyle(cellStyle);sheet.setColumnWidth(4,"命中次数".length()*256*4);
        if(!CollectionUtils.isEmpty(dataList)){
            IntStream.range(0,dataList.size()).forEach(i->{
                BiRuleDataVO jsonObject = dataList.get(i);
                String prodName = jsonObject.getProdName();
                String interval = jsonObject.getInterval();
                String threshold = jsonObject.getThreshold();
                int count = jsonObject.getCount()==null?0:jsonObject.getCount().intValue();
                Row dataRow= sheet.createRow(i+1);
                Cell numCell = dataRow.createCell(0);numCell.setCellValue(i+1);
                Cell nameCell = dataRow.createCell(1);nameCell.setCellValue(prodName);
                Cell intervalCell = dataRow.createCell(2);intervalCell.setCellValue(interval);
                Cell thresholdCell = dataRow.createCell(3);thresholdCell.setCellValue(threshold);
                Cell countCell = dataRow.createCell(4);countCell.setCellValue(count);
            });
        }

        return workbook;
    }

    public static Workbook createWorkBookByHeaders(String[] headers,String[] personHeaders){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellStyle cellStyle = workbook.createCellStyle();
        Font hssfFont = workbook.createFont();
        hssfFont.setBold(true);
        hssfFont.setFontHeightInPoints((short) 20);
        cellStyle.setFont(hssfFont);
        Row row= sheet.createRow(0);
        for(int i = 0;i<headers.length;i++ ){
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(cellStyle);
            sheet.setColumnWidth(i,headers[i].length()*256*4);
        }
        if(null!=personHeaders){
            for(int i = headers.length,j=0;i<(headers.length+personHeaders.length);i++,j++ ){
                Cell cell = row.createCell(i);
                cell.setCellValue(personHeaders[j]);
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(i,personHeaders[j].length()*256*4);
            }
        }



        CellStyle exampleCellStyle = workbook.createCellStyle();
        Font exampleFont = workbook.createFont();
        //hssfFont.setBold(true);
        exampleFont.setColor(HSSFColor.VIOLET.index);
        exampleFont.setFontHeightInPoints((short) 15);
        exampleCellStyle.setFont(exampleFont);
        Row exampleRow= sheet.createRow(1);
        for(int i = 0;i<headers.length;i++ ){
            Cell cell = exampleRow.createCell(i);
            String str = Constants.EXCEL_EXAMPLE_VALUES.get(headers[i]);
            cell.setCellValue(str);
            cell.setCellStyle(exampleCellStyle);
        }
        if(null!=personHeaders){
            for(int i = headers.length,j=0;i<(headers.length+personHeaders.length);i++,j++ ){
                Cell cell = exampleRow.createCell(i);
                String str = Constants.EXCEL_EXAMPLE_VALUES.get(personHeaders[j]);
                cell.setCellValue(str);
                cell.setCellStyle(exampleCellStyle);
            }
        }

        Cell cell = exampleRow.createCell(headers.length+(null==personHeaders?0:personHeaders.length));
        String str = "（该行数据为示例，上传时建议删除）";
        cell.setCellValue(str);
        cell.setCellStyle(exampleCellStyle);
        return workbook;
    }
    public static boolean checkExampleRow(Row row ,Map<Integer,String> headerMap){
        for(int i = 0;i<headerMap.size();i++){
            Cell cell = row.getCell(i);
            String tmpVal = getCellValue(cell);
            String exampleVal = Constants.EXCEL_EXAMPLE_VALUES.get( headerMap.get(i));
            if(StringUtils.equals(tmpVal,exampleVal)){
                return true;
            }
        }
        return false;
    }

    public static List<String[]> getExcelData(Workbook workbook, JSONObject jo, List<String> headList, List<String> relatedHeadList,
                                              Map<Integer, String> headerMap, List<String> requiredHead, List<String> requiredHeadPerson) {
        //checkFile(file);
        //获得Workbook工作薄对象
        //Workbook workbook = getWorkBook(suffix,is);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        boolean noCompany = !headList.contains(Constants.companyName);
        List<String[]> list = new ArrayList<>();
        CellStyle errorCellStyle = workbook.createCellStyle();
        Font errorFont = workbook.createFont();
        errorFont.setColor(Font.COLOR_RED);
        errorCellStyle.setFont(errorFont);
        CellStyle warnStyle = workbook.createCellStyle();
        Font warnFont = workbook.createFont();
        warnFont.setColor(HSSFColor.SKY_BLUE.index);
        warnStyle.setFont(warnFont);
        Set<String> nameSet = new HashSet<>();
        if(workbook != null){
            for(int sheetNum = 0;sheetNum < 1;sheetNum++){
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //表头检验
                List<String> excelColumnHead = new ArrayList<>();
                for(int i=0;;i++){
                    Cell cell = sheet.getRow(firstRowNum).getCell(i);
                    if(null==cell){
                        break;
                    }
                    excelColumnHead.add(getCellValue(cell));
                }
                int personSize = 0;
                if(excelColumnHead.size()!=headList.size()){
                    if(CollectionUtils.isEmpty(relatedHeadList)){
                        jo.put("templateError",1);
                        String[] arr = new String[excelColumnHead.size()];
                        excelColumnHead.toArray(arr);
                        list.add(arr);
                        return list;
                    }else{
                        int tmp = headList.size()+relatedHeadList.size();
                        if((excelColumnHead.size()-tmp)%relatedHeadList.size()!=0||excelColumnHead.size()-tmp<0){
                            jo.put("templateError",1);
                            String[] arr = new String[excelColumnHead.size()];
                            excelColumnHead.toArray(arr);
                            list.add(arr);
                            return list;
                        }
                        personSize = (excelColumnHead.size()-headList.size())/relatedHeadList.size();
                    }
                }
                if(personSize>0){
                    int i = personSize;

                    while(i>1){
                        relatedHeadList.addAll(relatedHeadList);
                        requiredHeadPerson.addAll(requiredHeadPerson);
                        i = i - 1;
                    }
                    requiredHead.addAll(requiredHeadPerson);
                }
                int relatedStart = headList.size();
                if (!CollectionUtils.isEmpty(relatedHeadList)) {
                    headList.addAll(relatedHeadList);
                }
                for(int i=0;i<headList.size();i++){
                    Cell cell = sheet.getRow(firstRowNum).getCell(i);
                    if(!StringUtils.equals(getCellValue(cell),headList.get(i))){
                        jo.put("templateError",1);
                        excelColumnHead.add(getCellValue(cell));
                    }
                }
                if(jo.containsKey("templateError")){
                    if(excelColumnHead.size()>0){
                        String [] arr = new String[excelColumnHead.size()];
                        excelColumnHead.toArray(arr);
                        list.add(arr);
                    }
                    return list;
                }
                for(int cellNum = 0; cellNum < headList.size();cellNum++){
                    String colName =  getCellValue(sheet.getRow(firstRowNum).getCell(cellNum));
                    headerMap.put(cellNum,colName);
                }
                jo.put("errorCount",0);
                //循环除了第一行的所有行
                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if(row == null){
                        continue;
                    }
                    if(checkEmptyRow(row)){
                        continue;
                    }
                    if(checkExampleRow(row,headerMap)){
                        continue;
                    }
                    String[] cells = new String[ headList.size()];
                    //循环当前行
                    boolean hasError = false;
                    boolean companyError = false;
                    StringBuffer errMsg = new StringBuffer();
                    StringBuffer warnMsg = new StringBuffer();
                    for(int cellNum = 0; cellNum <  headList.size();cellNum++){
                        Cell cell = row.getCell(cellNum);
                        try {
                            String headName = headerMap.get(cellNum);
                            String cellValue = getCellValue(cell);
                            IValidator validator = Constants.EXCEL_VALIDATORS2.get(headerMap.get(cellNum));
                            if(!validator.validate(cellValue)) {
                                hasError = true;
                                jo.put("uploadResult", false);
                                String eMsg = Constants.EXCEL_VALIDATORS2.get(headName).errMsg();
                                eMsg = noCompany?eMsg.replaceAll("法人代表","申请人"):eMsg;
                                if (validator instanceof CompanyValidator||(!CollectionUtils.isEmpty(requiredHead)&&StringUtils.equals(headName,requiredHead.get(cellNum)))){
                                    if(cellNum>=relatedStart){
                                        eMsg = eMsg.replaceAll("法人代表|申请人","关联人");
                                    }
                                    errMsg.append(eMsg).append(",");
                                    companyError = true;
                                    continue;
                                }else{
                                    if(cellNum>=relatedStart){
                                        eMsg = eMsg.replaceAll("法人代表|申请人","关联人");
                                    }
                                    warnMsg.append(eMsg).append(",");
                                }
                            }else{
                                if(Constants.companyName.equals(headName)||(noCompany&&Constants.applyIdNumber.equals(headName))){
                                    if(nameSet.contains(cellValue)){
                                        hasError = true;
                                        companyError = true;
                                        errMsg.append("进件内容重复").append(",");
                                        jo.put("uploadResult",false);
                                        continue;
                                    }
                                    nameSet.add(cell.getStringCellValue());
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(),e);
                        }
                        cells[cellNum] = getCellValue(cell).trim().replaceAll("\u0020|\u3000|\\u00A0",StringUtils.EMPTY);
                    }
                    if(!hasError){
                        list.add(cells);
                    }else{
                        if(!companyError){
                            list.add(cells);
                        }else{
                            jo.put("errorCount",jo.getIntValue("errorCount")+1);
                        }
                        System.out.println("errMsg:"+errMsg);
                        System.out.println("warnMsg:"+warnMsg);
                        if(errMsg.length()>0){
                            Cell addCell = row.createCell(headList.size());
                            addCell.setCellValue(errMsg.substring(0,errMsg.length()-1));
                            addCell.setCellStyle(errorCellStyle);
                        }
                        if(warnMsg.length()>0){
                            Cell addCell = null==row.getCell(headList.size())||StringUtils.isEmpty(getCellValue(row.getCell(headList.size())))?row.createCell(headList.size()):row.createCell(headList.size()+1);
                            addCell.setCellValue(warnMsg.substring(0,warnMsg.length()-1));
                            addCell.setCellStyle(warnStyle);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static List<String[]> getExcelData(Workbook workbook, JSONObject jo, boolean hasCompany, List<String> headList,
                                              Map<Integer, String> headerMap, List<String> requiredHead) {
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<>();
        CellStyle errorCellStyle = workbook.createCellStyle();
        Font errorFont = workbook.createFont();
        errorFont.setColor(Font.COLOR_RED);
        errorCellStyle.setFont(errorFont);
        CellStyle warnStyle = workbook.createCellStyle();
        Font warnFont = workbook.createFont();
        warnFont.setColor(HSSFColor.SKY_BLUE.index);
        warnStyle.setFont(warnFont);
        Set<String> nameSet = new HashSet<>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < 1; sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //表头检验
                List<String> excelColumnHead = new ArrayList<>();
                for (int i = 0; ; i++) {
                    Cell cell = sheet.getRow(firstRowNum).getCell(i);
                    if (null == cell) {
                        break;
                    }
                    excelColumnHead.add(getCellValue(cell));
                }
                if (excelColumnHead.size() != headList.size()) {
                    jo.put("templateError", 1);
                    String[] arr = new String[excelColumnHead.size()];
                    excelColumnHead.toArray(arr);
                    list.add(arr);
                    return list;
                }
                for (int i = 0; i < headList.size(); i++) {
                    Cell cell = sheet.getRow(firstRowNum).getCell(i);
                    if (!StringUtils.equals(getCellValue(cell), headList.get(i))) {
                        jo.put("templateError", 1);
                        excelColumnHead.add(getCellValue(cell));
                    }
                }
                if (jo.containsKey("templateError")) {
                    if (excelColumnHead.size() > 0) {
                        String[] arr = new String[excelColumnHead.size()];
                        excelColumnHead.toArray(arr);
                        list.add(arr);
                    }
                    return list;
                }
                for (int cellNum = 0; cellNum < headList.size(); cellNum++) {
                    String colName = getCellValue(sheet.getRow(firstRowNum).getCell(cellNum));
                    headerMap.put(cellNum, colName);
                }
                jo.put("errorCount", 0);
                //循环除了第一行的所有行
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    if (checkEmptyRow(row)) {
                        continue;
                    }
                    if (checkExampleRow(row, headerMap)) {
                        continue;
                    }
                    String[] cells = new String[headList.size()];
                    //循环当前行
                    boolean hasError = false;
                    boolean companyError = false;
                    StringBuffer errMsg = new StringBuffer();
                    StringBuffer warnMsg = new StringBuffer();
                    for (int cellNum = 0; cellNum < headList.size(); cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        try {
                            String headName = headerMap.get(cellNum);
                            String cellValue = getCellValue(cell);
                            IValidator validator = Constants.EXCEL_VALIDATORS2.get(headerMap.get(cellNum));
                            if (validator != null && !validator.validate(cellValue)) {
                                hasError = true;
                                jo.put("uploadResult", false);
                                String eMsg = Constants.EXCEL_VALIDATORS2.get(headName).errMsg();
                                if (validator instanceof CompanyValidator || (!CollectionUtils.isEmpty(requiredHead) && StringUtils.equals(headName, requiredHead.get(cellNum)))) {
                                    errMsg.append(eMsg).append(",");
                                    companyError = true;
                                    continue;
                                } else {
                                    warnMsg.append(eMsg).append(",");
                                }
                            } else {
                                if (Objects.equals(Constants.companyName, headName) ||
                                   (!hasCompany && StringUtils.endsWith(headName, "身份证"))) {
                                    if (nameSet.contains(cellValue)) {
                                        hasError = true;
                                        companyError = true;
                                        errMsg.append("进件内容重复").append(",");
                                        jo.put("uploadResult", false);
                                        continue;
                                    }
                                    nameSet.add(cell.getStringCellValue());
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        cells[cellNum] = getCellValue(cell).trim().replaceAll("\u0020|\u3000|\\u00A0", StringUtils.EMPTY);
                    }
                    if (!hasError) {
                        list.add(cells);
                    } else {
                        if (!companyError) {
                            list.add(cells);
                        } else {
                            jo.put("errorCount", jo.getIntValue("errorCount") + 1);
                        }
                        System.out.println("errMsg:" + errMsg);
                        System.out.println("warnMsg:" + warnMsg);
                        if (errMsg.length() > 0) {
                            Cell addCell = row.createCell(headList.size());
                            addCell.setCellValue(errMsg.substring(0, errMsg.length() - 1));
                            addCell.setCellStyle(errorCellStyle);
                        }
                        if (warnMsg.length() > 0) {
                            Cell addCell = null == row.getCell(headList.size()) || StringUtils.isEmpty(getCellValue(row.getCell(headList.size()))) ? row.createCell(headList.size()) : row.createCell(headList.size() + 1);
                            addCell.setCellValue(warnMsg.substring(0, warnMsg.length() - 1));
                            addCell.setCellStyle(warnStyle);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static boolean checkEmptyRow(Row row){
        boolean ret = false;
        int emptyCount = 0;
        for(int cellNum = 0; cellNum < 6;cellNum++){
            Cell cell = row.getCell(cellNum);
            if(StringUtils.isEmpty(getCellValue(cell))){
                emptyCount = emptyCount+1;
            }
        }
        if(6==emptyCount){
            ret = true;
        }
        return ret;
    }


    /**
     * 检查文件
     * @param file
     */
    public static void checkFile(MultipartFile file) {
        //判断文件是否存在
        if(null == file){
            log.error("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if(!fileName.endsWith("xls") && !fileName.endsWith("xlsx")){
            log.error(fileName + "不是excel文件");
        }
    }

    /**
     * 获取文件
     * @param file
     * @return
     */
    public static  Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith("xls")){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith("xlsx")){
                //2007 及2007以上
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return workbook;
    }
    public static  Workbook getWorkBook(String suffix, InputStream is) {
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(StringUtils.equals("xls",suffix)){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(StringUtils.equals("xlsx",suffix)){
                //2007 及2007以上
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return workbook;
    }
    /**
     * 获取单元格Cell数据
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){
        String cellValue = StringUtils.EMPTY;
        if(cell == null){
            return cellValue;
        }
        CellType cellType = cell.getCellTypeEnum();
        //判断数据的类型
        if(CellType.NUMERIC==cellType){
            cellValue = stringDateProcess(cell);
        }else if(CellType.STRING==cellType){
            cellValue = String.valueOf(cell.getStringCellValue()).trim();
        }else if(CellType.BOOLEAN==cellType){
            cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
        }else if(CellType.FORMULA==cellType){
            try {
                cellValue = String.valueOf(cell.getNumericCellValue()).trim();
            }catch (Exception e){
                try{
                    cellValue = String.valueOf(cell.getStringCellValue()).trim();
                }catch (Exception e1){
                    log.error(e1.getMessage(),e1);
                }
            }
        }else if(CellType.BLANK==cellType){
            cellValue = StringUtils.EMPTY;
        }else if(CellType.ERROR==cellType){
            cellValue = "非法字符";
        }else {
            cellValue = "未知类型";
        }
        return cellValue;
    }


    public static String getCellValueNoTrim(Cell cell){
        String cellValue = StringUtils.EMPTY;
        if(cell == null){
            return cellValue;
        }
        CellType cellType = cell.getCellTypeEnum();
        //判断数据的类型
        if(CellType.NUMERIC==cellType){
            cellValue = stringDateProcess(cell);
        }else if(CellType.STRING==cellType){
            cellValue = String.valueOf(cell.getStringCellValue());
        }else if(CellType.BOOLEAN==cellType){
            cellValue = String.valueOf(cell.getBooleanCellValue());
        }else if(CellType.FORMULA==cellType){
            try {
                cellValue = String.valueOf(cell.getNumericCellValue());
            }catch (Exception e){
                try{
                    cellValue = String.valueOf(cell.getStringCellValue());
                }catch (Exception e1){
                    log.error(e1.getMessage(),e1);
                }
            }
        }else if(CellType.BLANK==cellType){
            cellValue = StringUtils.EMPTY;
        }else if(CellType.ERROR==cellType){
            cellValue = "非法字符";
        }else {
            cellValue = "未知类型";
        }
        return cellValue;

    }





    /**
     * 时间格式处理
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


    public static Map<String,ApiVO> getApisFromExcel(List<ApiVO> bizValidApiList,String excelFileName) throws IOException {
        Map<String,ApiVO> apiMap = new LinkedHashMap<>();
        File excelFile = new File(excelFileName);
        if(!excelFile.exists()){
            return apiMap;
        }
        Set<String> bizValidApiProdCodeSet = new HashSet<>();

        bizValidApiList.forEach(apiVO -> {
            bizValidApiProdCodeSet.add(apiVO.getProdCode());
        });
        InputStream is = FileUtils.openInputStream(new File(excelFileName));
        Workbook wb = ExcelUtil.getWorkBook("xlsx", is);
        Sheet prodListSheet = wb.getSheet("企业数据产品");
        List<String> apiNames = new ArrayList<>();
        int lastRowNum = prodListSheet.getLastRowNum();
        for(int i=2;i<=lastRowNum;i++){
            Cell nameCH = prodListSheet.getRow(i).getCell(3);
            Hyperlink hyperlink = nameCH.getHyperlink();
            /*if(getCellValue(nameCH).equals("身份证识别")){
                log.info("");
            }*/
            String address = null==hyperlink?"No_Finish":hyperlink.getAddress();
            String apiName = getCellValueNoTrim(nameCH).concat("|").concat(getCellValueNoTrim(prodListSheet.getRow(i).getCell(4)).concat("|").concat(address));
            apiNames.add(apiName);
        }

        apiNames.forEach(apiName->{

            String[] tmp =  apiName.split("\\|");
            String nameCH = tmp[0];
            String prodCode=tmp[1];
            String sheetName = tmp[2];
            if(!StringUtils.equals("No_Finish",sheetName)&&
                    !StringUtils.contains(nameCH,"发票信息")&&
                    !StringUtils.contains(nameCH,"税务报告-插件版")&&
                    bizValidApiProdCodeSet.contains(prodCode)
                    ){
                Sheet prodSheet = wb.getSheet(sheetName.substring(0,sheetName.indexOf("!")).replaceAll("'",StringUtils.EMPTY));
                if(null!=prodSheet){
                    log.info("apiName:"+apiName+" collect start;");
                    int  base = 1;
                    /*String start=getCellValue(prodSheet.getRow(7).getCell(1));
                    base = StringUtils.isEmpty(start)?2:1;*/

                    int lastRowNum2 = prodSheet.getLastRowNum();

                    for(int i=0;i<lastRowNum2;i++){
                        Row row = prodSheet.getRow(i);
                        if(null==row){
                            continue;
                        }
                        for(int j=0;j<5;j++){
                            if(StringUtils.equals("二、请求参数",getCellValue(row.getCell(j)))){
                                base=j;
                                break;
                            }
                        }
                    }


                    String introduction = getCellValueNoTrim(prodSheet.getRow(4).getCell(3));
                    String dataDescription = getCellValueNoTrim(prodSheet.getRow(5).getCell(3));
                    String description = getCellValueNoTrim(prodSheet.getRow(6).getCell(3));
                    /*if(StringUtils.equals("BizFour",prodCode)){
                        log.info("");
                    }*/
                    int requestStart=9;
                    for (int i=0;i<=lastRowNum2;i++){
                        Row row = prodSheet.getRow(i);
                        if(null==row){
                            continue;
                        }
                        String requestStartSign = getCellValue(row.getCell(base));
                        if(StringUtils.equals("二、请求参数",requestStartSign)){
                            requestStart = i + 2;
                            break;
                        }
                    }
                    ApiVO apiVO = new ApiVO();
                    apiVO.setIntroduction(introduction);
                    apiVO.setProdCode(prodCode);
                    apiVO.setVersion(1.0);
                    apiVO.setProdName(nameCH);
                    apiVO.setDataDescription(dataDescription);
                    apiVO.setDescription(description);
                    int responseStart = 0;
                    int requestParamTypeIndex = base+2;

                    for(int i=requestStart;i<=lastRowNum2;i++){
                        Row row = prodSheet.getRow(i);
                        String requestEnd = getCellValueNoTrim(row.getCell(base));
                        if(StringUtils.equals("三、响应参数",requestEnd)){
                            responseStart = i + 2;
                            break;
                        }

                        if(i==requestStart&&StringUtils.isEmpty(getCellValueNoTrim(row.getCell(requestParamTypeIndex)))){
                            requestParamTypeIndex = base+3;
                        }
                        ApiRequestVO apiRequestVO = new ApiRequestVO();
                        apiRequestVO.setParamCode(getCellValueNoTrim(row.getCell(base+1)));
                        if(StringUtils.isBlank(apiRequestVO.getParamCode())){
                            continue;
                        }
                        apiRequestVO.setParamType(getCellValueNoTrim(row.getCell(requestParamTypeIndex)));
                        apiRequestVO.setRequired(StringUtils.equals("是",getCellValueNoTrim(row.getCell(requestParamTypeIndex+1)))?1:0);
                        apiRequestVO.setParamName(getCellValueNoTrim(row.getCell(requestParamTypeIndex+2)));
                        apiRequestVO.setParamDesc(getCellValueNoTrim(row.getCell(requestParamTypeIndex+3)));
                        apiRequestVO.setApiProdCode(apiVO.getProdCode());
                        apiRequestVO.setApiVersion(apiVO.getVersion());
                        apiVO.getRequestParamList().add(apiRequestVO);
                    }
                    List<ApiResponseVO> apiResponseVOListTmp = new ArrayList<>();
                    int responseParamTypeIndex = base+2;
                    /*if(apiVO.getProdCode().equals("BizEmployee")){
                        log.info("");
                    }*/
                    boolean indentionOdd=false;
                    for(int i = responseStart;i<=lastRowNum2;i++){
                        Row row = prodSheet.getRow(i);
                        Cell paramCodeCell = row.getCell(base+1);
                        String paramCode = getCellValueNoTrim(paramCodeCell);
                        if(StringUtils.isBlank(paramCode)){
                            break;
                        }
                        Short indention =  paramCodeCell.getCellStyle().getIndention();
                        if(indention.intValue()>0){
                            if(indention.intValue()==1){
                                indentionOdd=true;
                            }
                            break;
                        }
                    }
                    for(int i = responseStart;i<=lastRowNum2;i++){
                        Row row = prodSheet.getRow(i);
                        ApiResponseVO apiResponseVO = new ApiResponseVO();
                        Cell paramCodeCell = row.getCell(base+1);

                        String paramCode = getCellValueNoTrim(paramCodeCell);
                        if(StringUtils.isBlank(paramCode)){
                            break;
                        }
                        apiResponseVO.setParamCode(paramCode.trim());
                        Short indention =  paramCodeCell.getCellStyle().getIndention();
                        if(indention.intValue()>0){
                            apiResponseVO.setDepth(indention.intValue()/(indentionOdd?1:2));
                        }else{
                            int blankCount = 0;
                            for(int j=0;j<paramCode.length();j++){
                                if(' '!=paramCode.charAt(j)){
                                    break;
                                }
                                blankCount = blankCount + 1;
                            }
                            apiResponseVO.setDepth(blankCount/4);
                        }
                        if(i==responseStart&&StringUtils.isEmpty(getCellValueNoTrim(row.getCell(responseParamTypeIndex)))){
                            responseParamTypeIndex = base+3;
                        }
                        apiResponseVO.setApiProdCode(apiVO.getProdCode());
                        apiResponseVO.setApiVersion(apiVO.getVersion());
                        apiResponseVO.setParamType(getCellValueNoTrim(row.getCell(responseParamTypeIndex)));
                        String lengthStr = getCellValueNoTrim(row.getCell(responseParamTypeIndex+1));
                        apiResponseVO.setLength(StringUtils.isNotEmpty(lengthStr)?Integer.parseInt(lengthStr):null);
                        apiResponseVO.setParamName(getCellValueNoTrim(row.getCell(responseParamTypeIndex+2)));
                        apiResponseVO.setParamDesc(getCellValueNoTrim(row.getCell(responseParamTypeIndex+3)));
                        apiResponseVOListTmp.add(apiResponseVO);
                    }

                    List<ApiResponseVO> apiResponseVOListFinal = new ArrayList<>();
                    List<ApiResponseVO> wrongList = new ArrayList<>();
                    updateApiResponseParent(apiResponseVOListFinal,apiResponseVOListTmp,null,wrongList);
                    if(wrongList.size()>0){
                        log.warn("api接口结构错误：[{}]",apiName);
                    }
                    //if(apiName.contains("BizEmployee"))
                    log.info(apiName+":"+JSONObject.toJSONString(apiResponseVOListFinal));
                    apiVO.setResponseParamList(apiResponseVOListFinal);
                    apiMap.put(apiName,apiVO);
                }else{
                    log.info("没找到相对应的产品sheet:{}",apiName);
                }
            }

        });
        return apiMap;
        //log.info(apiNames.toString());
    }


    public static Map<String,ApiVO> getApisFromEasyExcel(List<ApiVO> bizValidApiList,String excelFileName) throws IOException {
        Map<String,ApiVO> apiMap = new LinkedHashMap<>();
        File excelFile = new File(excelFileName);
        if(!excelFile.exists()){
            return apiMap;
        }
        Set<String> bizValidApiProdCodeSet = new HashSet<>();

        bizValidApiList.forEach(apiVO -> {
            bizValidApiProdCodeSet.add(apiVO.getProdCode());
        });
        EasyExcel.read(new File(excelFileName));
        return apiMap;
        //log.info(apiNames.toString());
    }




    public static void updateApiResponseParent(List<ApiResponseVO> retList,List<ApiResponseVO> originList,ApiResponseVO parent,List<ApiResponseVO> wrongList){
        if(!CollectionUtils.isEmpty(originList)){
            ApiResponseVO apiResponseVO = originList.remove(0);
            if(apiResponseVO.getDepth()==0){
                retList.add(apiResponseVO);
                updateApiResponseParent(retList,originList,apiResponseVO,wrongList);
            }else{
                ApiResponseVO parentTmp = findParent(parent, apiResponseVO.getDepth());
                if(parentTmp!=null){
                    apiResponseVO.setParent(parentTmp);
                    parentTmp.getChildren().add(apiResponseVO);
                    updateApiResponseParent(retList,originList,apiResponseVO,wrongList);
                }else{
                    wrongList.add(apiResponseVO);
                    updateApiResponseParent(retList,originList,apiResponseVO,wrongList);
                }

            }
        }
    }

    public static ApiResponseVO findParent(ApiResponseVO parent,int targetDepth){
        if(parent!=null&&parent.getDepth()-targetDepth!=-1){
            parent = findParent(parent.getParent(),targetDepth);
        }
        return parent;
    }

    public static void main(String[] args) throws IOException {
        //System.out.println(" ".equals(" "));
        Map<String, ApiVO> map = getApisFromExcel(new ArrayList<>(),"C:\\cenruyu\\Documents\\biz\\api_admin\\百融企业征信数据产品字典V5.4-商务版(1) - 副本.xlsx");
        log.info(JSONObject.toJSONString(map));
        /*JSONObject jo = new JSONObject();
        InputStream is = FileUtils.openInputStream(new File("C:\\Users\\yangjinping\\Desktop\\re\\template.xlsx"));
        Workbook wb = ExcelUtil.getWorkBook("xlsx", is);

        //keyNo#idNumber#name#cellPhone#creditCode#bankId
        List  headList = Arrays.asList("keyNo#idNumber#cellPhone#creditCode#name#bankId".split("#"));
        Map<Integer,String> headerMap = new HashMap<>();
        List<String[]> list = getExcelData("xlsx",wb,jo,headList.size(),headerMap);
        for(String[] row :list){
            StringBuffer e=new StringBuffer();
            for(String s : row){
                e.append(s+"#");
            }
            System.out.println(e);
        }
        System.out.println(jo.toJSONString());
        OutputStream os = FileUtils.openOutputStream(new File("C:\\Users\\yangjinping\\Desktop\\re\\template_err13221.xlsx"));
        wb.write(os);*/
        /*System.out.println(jo.toJSONString());
        OutputStream os = FileUtils.openOutputStream(new File("D:\\workspace\\htmltopdf_files\\report\\template_err.xlsx"));
        wb.write(os);*/
       // System.out.println(Regex_CreditCode.isValid(code));
        /*String s ="公司全名#法人身份证#法人姓名#法人联系电话#统一社会信用代码#企业开户账号#法人居住地址#法人工作地址";
        String[] arr = s.split("#");
        try {
            Workbook workbook = ExcelUtil.createWorkBookByHeaders(s.split("#"),null);
            //workbook.write(response.getOutputStream());
            OutputStream os = FileUtils.openOutputStream(new File("C:\\Users\\yangjinping\\Desktop\\re\\template_0823.xlsx"));
            workbook.write(os);
            System.out.println("qqq");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*InputStream is = FileUtils.openInputStream(new File("C:\\cenruyu\\Documents\\和运\\20190423\\信审奖金计算(201903).xlsx"));
        Workbook wb = ExcelUtil.getWorkBook("xlsx", is);
        Sheet sheet = wb.getSheet("设备审查明细");
        Set<String> set = new LinkedHashSet<>();
        for(int i=1;i<347;i++){
            Row row = sheet.getRow(i);
            String companyName = row.getCell(5).getStringCellValue();
            if(companyName.length()>3)
            set.add(companyName);
        }
        System.out.println(set.toString().replaceAll("\\[|\\]","'").replaceAll(", ","','"));*/
    }

}
