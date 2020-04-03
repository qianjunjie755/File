package com.biz.warning.util;


import com.alibaba.fastjson.JSONObject;
import com.biz.warning.util.tools.ApplicationDateValidator;
import com.biz.warning.util.tools.CompanyValidator;
import com.biz.warning.util.tools.ExpireTimeValidator;
import com.biz.warning.util.tools.IValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *解析excel文件数据
 */
@Slf4j
@Component
public class ExcelUtil {

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





    public static List<String[]> getExcelData(String suffix, Workbook workbook, JSONObject jo,List<Object> headList,Map<Integer,String> headerMap,Set<String> existedCompanyNames) throws IOException{
        //checkFile(file);
        //获得Workbook工作薄对象
        //Workbook workbook = getWorkBook(suffix,is);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
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
                if(excelColumnHead.size()!=headList.size()){
                    if(excelColumnHead.size()>headList.size()){
                        Cell cell = sheet.getRow(firstRowNum).getCell(headList.size());
                        if(StringUtils.isNotEmpty(getCellValue(cell))){
                            jo.put("templateError",1);
                            String [] arr = new String[excelColumnHead.size()];
                            excelColumnHead.toArray(arr);
                            list.add(arr);
                            return list;
                        }
                    }else{
                        jo.put("templateError",1);
                        String [] arr = new String[excelColumnHead.size()];
                        excelColumnHead.toArray(arr);
                        list.add(arr);
                        return list;
                    }

                }
                for(int i=0;i<headList.size();i++){
                    Cell cell = sheet.getRow(firstRowNum).getCell(i);
                    if(!StringUtils.equals(getCellValue(cell),headList.get(i).toString())){
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
                    if(checkEmptyRow(row,6)){
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
                    DateTime expireTime = null;
                    DateTime applyTime = null;
                    for(int cellNum = 0; cellNum <  headList.size();cellNum++){
                        Cell cell = row.getCell(cellNum);
                        IValidator validator = SysDict.EXCEL_VALIDATORS.get(headerMap.get(cellNum));
                        try {
                            String cellValue = getCellValue(cell);
                            if(!validator.validate(cellValue)) {
                                hasError = true;
                                if (validator instanceof CompanyValidator
                                        ||validator instanceof ExpireTimeValidator
                                        ||validator instanceof ApplicationDateValidator){
                                    errMsg.append(SysDict.EXCEL_VALIDATORS.get(headerMap.get(cellNum)).errMsg()).append(",");
                                    companyError = true;
                                    jo.put("uploadResult", false);
                                    continue;
                                }else{
                                    warnMsg.append(SysDict.EXCEL_VALIDATORS.get(headerMap.get(cellNum)).errMsg()).append(",");
                                }
                            }else{
                                if(SysDict.companyName.equals(headerMap.get(cellNum))){
                                    if(nameSet.contains(cellValue)){
                                        hasError = true;
                                        companyError = true;
                                        errMsg.append("进件内容重复").append(",");
                                        jo.put("uploadResult",false);
                                        continue;
                                    }else if(existedCompanyNames.contains(cellValue)){
                                        hasError = true;
                                        companyError = true;
                                        errMsg.append("进件公司和任务监控中公司重复").append(",");
                                        jo.put("uploadResult",false);
                                        continue;
                                    }
                                    nameSet.add(cell.getStringCellValue());
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(),e);
                        }
                        cells[cellNum] = (null!=cell&&cell.getCellType()==Cell.CELL_TYPE_NUMERIC&&HSSFDateUtil.isCellDateFormatted(cell))?getCellValue(cell).trim():getCellValue(cell).trim().replaceAll("\u0020|\u3000|\\u00A0",StringUtils.EMPTY);
                        if((validator instanceof ExpireTimeValidator||validator instanceof ApplicationDateValidator)
                                &&StringUtils.isNotEmpty(cells[cellNum])){
                            cells[cellNum] = cells[cellNum].split(" ")[0];
                            if(SysDict.ENTITY_APPLICATION_DATE.equals(headerMap.get(cellNum))){
                                applyTime = ApplicationDateValidator.parseFromString(cells[cellNum]);
                            }else if(SysDict.ENTITY_EXPIRE_TIME_NAME.equals(headerMap.get(cellNum))){
                                expireTime = ApplicationDateValidator.parseFromString(cells[cellNum]);
                            }
                        }
                        //截止日期和申请日期判断
                        if(null!=expireTime&&null!=applyTime
                                &&Integer.parseInt(expireTime.toString("yyyyMMdd"))
                                <Integer.parseInt(applyTime.toString("yyyyMMdd"))){
                            hasError = true;
                            companyError = true;
                            errMsg.append("截止日期小于申请日期").append(",");
                            jo.put("uploadResult",false);
                        }
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
    public static boolean checkEmptyRow(Row row,int size){
        boolean ret = false;
        int emptyCount = 0;
        for(int cellNum = 0; cellNum < size;cellNum++){
            Cell cell = row.getCell(cellNum);
            if(StringUtils.isEmpty(getCellValue(cell))){
                emptyCount = emptyCount+1;
            }
        }
        if(size==emptyCount){
            ret = true;
        }
        return ret;
    }

    public static boolean checkExampleRow(Row row ,Map<Integer,String> headerMap){
        for(int i = 0;i<headerMap.size();i++){
            Cell cell = row.getCell(i);
            String tmpVal = getCellValue(cell);
            String exampleVal = SysDict.EXCEL_EXAMPLE_VALUES.get( headerMap.get(i));
            if(StringUtils.equals(tmpVal,exampleVal)){
                return true;
            }
        }
        return false;
    }




    /**
     * 检查文件
     * @param file
     * @throws IOException
     */
    public static  void checkFile(MultipartFile file) throws IOException{
        //判断文件是否存在
        if(null == file){
            log.info("文件不存在！");
            log.error("");
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
        CellType type = cell.getCellTypeEnum();
        if(CellType.NUMERIC==type){
            cellValue = stringDateProcess(cell);
        }else if (CellType.STRING==type){
            cellValue = String.valueOf(cell.getStringCellValue());
        }else if (CellType.BOOLEAN==type){
            cellValue = String.valueOf(cell.getBooleanCellValue());
        }else if (CellType.FORMULA==type){
            cellValue = String.valueOf(cell.getCellFormula());
        }else if (CellType.BLANK==type){
            cellValue = StringUtils.EMPTY;
        }else if (CellType.ERROR==type){
            cellValue = "非法字符";
        }else{
            cellValue = "未知类型";
        }


        //判断数据的类型
        /*switch (cell.getCellType()){

            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = stringDateProcess(cell);
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }*/
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
            Date date = HSSFDateUtil
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


    public static void addTemplateErrorMsg(Workbook workbook,String msg){
        if(workbook != null){
            CellStyle errorCellStyle = workbook.createCellStyle();
            Font errorFont = workbook.createFont();
            errorFont.setColor(Font.COLOR_RED);
            errorCellStyle.setFont(errorFont);
            for(int sheetNum = 0;sheetNum < 1;sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum  = sheet.getFirstRowNum();
                Row errorRow = sheet.getRow(firstRowNum);
                if(null==errorRow){
                    errorRow = sheet.createRow(firstRowNum);
                }
                for(int i=0;;i++){
                    Cell cell = errorRow.getCell(i);
                    if(null==cell){
                        cell = errorRow.createCell(i);
                        cell.setCellStyle(errorCellStyle);
                        cell.setCellValue(msg);
                        break;
                    }
                }
            }
        }
    }
    public static Workbook createWorkBookByHeaders(String[] headers){
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
        CellStyle exampleCellStyle = workbook.createCellStyle();
        Font exampleFont = workbook.createFont();
        //hssfFont.setBold(true);
        exampleFont.setColor(HSSFColor.VIOLET.index);
        exampleFont.setFontHeightInPoints((short) 15);
        exampleCellStyle.setFont(exampleFont);
        Row exampleRow= sheet.createRow(1);
        for(int i = 0;i<headers.length;i++ ){
            Cell cell = exampleRow.createCell(i);
            String str = SysDict.EXCEL_EXAMPLE_VALUES.get(headers[i]);
            cell.setCellValue(str);
            cell.setCellStyle(exampleCellStyle);
        }
        Cell cell = exampleRow.createCell(headers.length);
        String str = "（该行数据为示例，上传时建议删除）";
        cell.setCellValue(str);
        cell.setCellStyle(exampleCellStyle);
        return workbook;
    }

    public static XSSFWorkbook getWorkbook(List<Map<String,Object>> exportData, Map<String,String> rowMapper) {
        XSSFWorkbook wb = new XSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        XSSFSheet sheet = wb.createSheet();
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        XSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        XSSFCellStyle style = wb.createCellStyle();
        //style.setAlignment(CellStyle.ALIGN_CENTER); // 创建一个居中格式
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
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
        for (int j = 0; j < exportData.size(); j++) {
            row = sheet.createRow(j + 1);
            // 第四步，创建单元格，并设置值
            Map<String, Object> rows = exportData.get(j);
            //Set<String> exportkeys = rowMapper.keySet();
            int a = 0;
            for (String exportKey : keys) {
                String[] keyArray = exportKey.split("\\+");
                String value = "";
                for (String key : keyArray) {
                    if(rows.get(key) != null){
                        value += rows.get(key).toString();
                    }
                }
                //row.createCell((short) a).setCellValue(rows.get(exportkey) == null ? "" : rows.get(exportkey).toString());
                row.createCell((short) a).setCellValue(value);
                a++;
            }
        }
        return wb;
    }


    /**
     * 读取第一个sheet的数据
     * @param
     * @return
     */
    public static List<Map<String,String>> getFirstSheetData(MultipartFile file) {
        Workbook workbook = ExcelUtil.getWorkBook(file);
        Map<Object, List<Map<String, String>>> setFromWorkbook = getSetFromWorkbook(workbook);
        if(!CollectionUtils.isEmpty(setFromWorkbook)){
            return getSetFromWorkbook(workbook).entrySet().iterator().next().getValue();
        }
        return new ArrayList<>();
    }

    /**
     * 读取第一个sheet的数据
     * @param workbook
     * @return
     */
    public static List<Map<String,String>> getFirstSheetData(Workbook workbook) {
        return getSetFromWorkbook(workbook).entrySet().iterator().next().getValue();
    }

    /**
     * 从输入流读取excel
     * @param inputstream
     * @return
     * @throws Exception
     */
    public static Workbook getWorkBook(InputStream inputstream) {
        Object book = null;
        if(!((InputStream)inputstream).markSupported()) {
            inputstream = new PushbackInputStream((InputStream)inputstream, 8);
        }
        try {
            book = WorkbookFactory.create(inputstream);
        } catch (Exception ioe){
            ioe.printStackTrace();
        }
        return (Workbook) book;
    }

    /**
     * 取单元格的值
     * @param cell 单元格对象
     * @param treatAsStr 为true时，当做文本来取值
     * @return
     */
    public static String getCellValue(Cell cell, boolean treatAsStr) {
        if (cell == null) {
            return "";
        }
        if (treatAsStr) {
            //设置为文本来读取
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        if(cell.getCellType() != Cell.CELL_TYPE_STRING && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date theDate = cell.getDateCellValue();
            if(theDate==null){
                return "";
            }
            return sdf.format(theDate);
        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            DecimalFormat df = new DecimalFormat("#");
            return String.valueOf(df.format(cell.getNumericCellValue()));
        } else {
            return String.valueOf(cell.getStringCellValue()).trim();
        }
    }

    public static List<String> getHeaderListFromWorkBook(Workbook workbook, int sheetNum){
        Sheet sheet = workbook.getSheetAt(sheetNum);
        if (sheet == null) {
            return null;
        }
        int firstRowIndex = sheet.getFirstRowNum();
        // 读取首行 即,表头
        List<String> headList = new ArrayList<String>();
        Row firstRow = sheet.getRow(firstRowIndex);
        for (int i = firstRow.getFirstCellNum(); i < firstRow.getLastCellNum(); i++) {
            Cell cell = firstRow.getCell(i);
            String cellValue = getCellValue(cell, false);
            headList.add(cellValue);
        }
        return headList;
    }

    public static void addMsgToWorkbookFirstSheet(Workbook workbook,Map<Position,String> msgMap){
        if(workbook != null){
            CellStyle errorCellStyle = workbook.createCellStyle();
            Font errorFont = workbook.createFont();
            errorFont.setColor(Font.COLOR_RED);
            errorCellStyle.setFont(errorFont);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return;
            }
            for (Map.Entry<Position, String> entry : msgMap.entrySet()) {
                Position position = entry.getKey();
                Row errorRow = sheet.getRow(position.getLineNumnber());
                if(null==errorRow){
                    errorRow = sheet.createRow(position.getLineNumnber());
                }
                Cell cell = errorRow.createCell(position.getColumnNumber());
                cell.setCellStyle(errorCellStyle);
                cell.setCellValue(entry.getValue());
            }
        }
    }

    /**
     * Workbook转Collection
     * @param workbook
     * @return
     */
    public static Map<Object,List<Map<String,String>>> getSetFromWorkbook(Workbook workbook){
        Map<Object,List<Map<String,String>>> result = new HashMap<Object,List<Map<String,String>>>();
        try {
            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                Sheet sheet = workbook.getSheetAt(numSheet);
                if(sheet == null){
                    continue;
                }
                int firstRowIndex = sheet.getFirstRowNum();
                int lastRowIndex = sheet.getLastRowNum();
                if(lastRowIndex == 0){
                    continue;
                }
                List<String> headList = getHeaderListFromWorkBook(workbook,numSheet);
                // 读取数据行
                List<Map<String,String>> sheetRows = new ArrayList<>();
                for (int rowIndex = firstRowIndex + 1; rowIndex <= lastRowIndex; rowIndex++) {
                    Row currentRow = sheet.getRow(rowIndex);// 当前行
                    if(checkEmptyRow(currentRow,6)){
                        continue;
                    }
                    int firstColumnIndex = currentRow.getFirstCellNum(); // 首列
                    int lastColumnIndex = currentRow.getLastCellNum();// 最后一列
                    Map<String,String> rowMap = new LinkedHashMap<String,String>();
                    //for (int columnIndex = firstColumnIndex,sysIndex = 0; columnIndex < lastColumnIndex; columnIndex++,sysIndex++) {
                    for (int columnIndex = firstColumnIndex,sysIndex = 0; columnIndex < headList.size(); columnIndex++,sysIndex++) {
                        Cell currentCell = currentRow.getCell(columnIndex);// 当前单元格
                        String currentCellValue = getCellValue(currentCell, false);// 当前单元格的值
                        rowMap.put(headList.get(sysIndex),currentCellValue);
                    }
                    if(lastColumnIndex < headList.size()){
                        for(int patchIndex = lastColumnIndex; patchIndex < headList.size(); patchIndex ++ ){
                            rowMap.put(headList.get(patchIndex),null);
                        }
                    }
                    sheetRows.add(rowMap);
                }

                result.put(sheet.getSheetName(), sheetRows);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            return result;
        }
    }

    public static class Position{
        //行
        private Integer lineNumnber;
        //列
        private Integer columnNumber;

        public Integer getLineNumnber() {
            return lineNumnber;
        }

        public Position(){}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return Objects.equals(lineNumnber, position.lineNumnber) &&
                    Objects.equals(columnNumber, position.columnNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lineNumnber, columnNumber);
        }

        public Position(int lineNumnber, int columnNumber){
            this.columnNumber = columnNumber;
            this.lineNumnber = lineNumnber;
        }
        public void setLineNumnber(Integer lineNumnber) {
            this.lineNumnber = lineNumnber;
        }

        public Integer getColumnNumber() {
            return columnNumber;
        }

        public void setColumnNumber(Integer columnNumber) {
            this.columnNumber = columnNumber;
        }
    }

    public static void main(String[] args) throws Exception {
        JSONObject jo = new JSONObject();
        InputStream is = new FileInputStream(new File("d://test.xlsx"));
        Workbook wb = ExcelUtil.getWorkBook("xlsx", is);
        List<Object> headList = new ArrayList<>(Arrays.asList("公司全名_法人代表身份证_法人代表姓名_法人代表电话号码_统一社会信用代码_企业开户账号_法人代表居住地址_法人代表工作地址_截止日期_申请日期".split("_")));
        Map<Integer,String> headerMap = new HashMap<>();
        Set<String> existedCompanyNames = new HashSet<>();
        List<String[]> list = ExcelUtil.getExcelData(".xlsx", wb, jo, headList, headerMap, existedCompanyNames);
        for(String[] arr:list){
            String str = "";
            for(int i =0;i<arr.length;i++){
                str = str.concat("::::").concat(arr[i]);
            }
            System.out.println(str);
        }
        //ExcelUtil.getExcelData(".xlsx",wb,jo)
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

        /*try {
            OutputStream fos = new FileOutputStream(new File("d://xx.xlsx"));
            Workbook workbook = createWorkBookByHeaders(new String[]{"公司全名","法人代表身份证","法人代表姓名","法人代表电话号码","统一社会信用代码","企业开户账号"});
            workbook.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*Workbook workbook = ExcelUtil.createWorkBookByHeaders("公司全名_法人代表身份证_法人代表姓名_法人代表电话号码_统一社会信用代码_企业开户账号_法人代表居住地址_法人代表工作地址_截止日期_申请日期".split("_"));
        OutputStream fos = new FileOutputStream(new File("d://xx.xlsx"));
        workbook.write(fos);*/
        /*Workbook wb = ExcelUtil.getWorkBook("xlsx", FileUtils.openInputStream(new File("D:\\template (35).xlsx")));
        ExcelUtil.addTemplateErrorMsg(wb,"fdasfafs");
        OutputStream fos = new FileOutputStream(new File("d://xx.xlsx"));
        wb.write(fos);*/
        /*String str = FileUtils.readFileToString(new File("C:\\cenruyu\\Documents\\biz\\api_admin\\mysql\\20181017\\31231.txt"),Charset.forName(CharsetNames.UTF_8));
        JSONObject jsonObject = JSONObject.parseObject(str);

        System.out.println(jsonObject.size());*/
        /*System.out.println(jo.toJSONString());
        OutputStream os = FileUtils.openOutputStream(new File("D:\\workspace\\htmltopdf_files\\report\\template_err.xlsx"));
        wb.write(os);*/
       // System.out.println(Regex_CreditCode.isValid(code));
    }
}
