package com.biz.credit;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BizCreditApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void exportTableDoc() throws InterruptedException {
        String schema = "report";
        String sql = "SELECT a.TABLE_NAME," +
                     " a.TABLE_COMMENT," +
                     " b.COLUMN_NAME," +
                     " b.COLUMN_COMMENT," +
                     " b.COLUMN_TYPE," +
                     " CASE b.COLUMN_KEY WHEN 'PRI' THEN '是' ELSE '否' END COLUMN_KEY," +
                     " CASE COLUMN_DEFAULT when 'CURRENT_TIMESTAMP' then '当前时间'  else COLUMN_DEFAULT end '默认值'" +
                     " FROM INFORMATION_SCHEMA.`TABLES` a" +
                     " INNER JOIN INFORMATION_SCHEMA.`COLUMNS` b" +
                     " ON a.TABLE_NAME = b.TABLE_NAME" +
                     " AND a.TABLE_SCHEMA = b.TABLE_SCHEMA" +
                     " WHERE a.TABLE_SCHEMA = ?" +
                     //" AND (a.TABLE_COMMENT IS NOT NULL AND a.TABLE_COMMENT != '')" +
                     " AND a.TABLE_NAME in ('t_detail_config','t_detail_config_item')" +
                     " ORDER BY a.TABLE_NAME, b.ORDINAL_POSITION";
        List<TableVO> vos = jdbcTemplate.query(sql, (resultSet, i) -> {
            TableVO vo = new TableVO();
            vo.setTableName(resultSet.getString(1));
            vo.setTableComment(resultSet.getString(2));
            vo.setColumnName(resultSet.getString(3));
            vo.setColumnComment(resultSet.getString(4));
            vo.setColumnType(resultSet.getString(5));
            vo.setColumnKey(resultSet.getString(6));
            vo.setColumnDefault(resultSet.getString(7));
            return vo;
        }, schema);
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        List<Table> tables = new ArrayList<>();
        Table _table = null;
        String tableName = null;
        for (TableVO vo : vos) {
            if (!Objects.equals(tableName, vo.getTableName())) {
                tableName = vo.getTableName();
                _table = new Table();
                _table.setTableName(vo.getTableName());
                _table.setTableComment(vo.getTableComment());
                tables.add(_table);
            }
            Column column = new Column();
            column.setColumnName(vo.getColumnName());
            column.setColumnComment(vo.getColumnComment());
            column.setColumnType(vo.getColumnType());
            column.setColumnKey(vo.getColumnKey());
            column.setColumnDefault(vo.getColumnDefault());
            _table.addColumn(column);
        }

        ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\maolin.yao\\Desktop\\" + schema + ".xlsx");
        Font font = writer.createFont();
        font.setFontName("Courier New");
        font.setColor(Font.COLOR_NORMAL);
        writer.getStyleSet().setFont(font, false);
        writer.getStyleSet().setAlign(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);

        List<String> tableHead = new ArrayList<>();
        tableHead.add("表名");
        tableHead.add("描述");
        List<String> columnHead = new ArrayList<>();
        columnHead.add("字段");
        columnHead.add("说明");
        columnHead.add("类型");
        columnHead.add("是否主键");
        columnHead.add("默认值");
        int rowNum = 0;
        for (Table table : tables) {
            //表头
            writer.writeHeadRow(tableHead);
            //合并单元格
            rowNum = writer.getCurrentRow()-1;
            //writer.merge(rowNum, rowNum, 0, 1, "表名", true);
            writer.merge(rowNum, rowNum, 1, 4, "描述", true);
            //表说明
            writer.writeRow(table, false);
            //合并单元格
            rowNum = writer.getCurrentRow()-1;
            //writer.merge(rowNum, rowNum, 0, 1, table.getTableName(), false);
            writer.merge(rowNum, rowNum, 1, 4, table.getTableComment(), false);
            //列头
            writer.writeHeadRow(columnHead);
            //列内容
            writer.write(table.getColumns(), false);
            //空一行
            writer.passCurrentRow();
            writer.flush();
        }
        writer.close();
        System.out.println("[" + schema + "]表结构已全部导出完成!!");
        Thread.sleep(10 * 60 *1000);
    }


    public static void main(String[] args) {

    }

    /**
     * 提取金额并转换
     *
     * @return
     */
    public static Integer getPunishNumber(String str_i) {
        String resStr = "";
        Integer res = -99;
        String pattern = "罚款(['壹'|'贰'|'叁'|'肆'|'伍'|'陆'|'柒'|'捌'|'玖'|'拾'|'佰'|'仟'|'万'|'亿'|'元'|'角'|'分'|'零'|'整'|'正'"
                + "|'一'|'二'|'两'|'三'|'四'|'五'|'六'|'七'|'八'|'九'|'十'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'|'0'|'十'|'百'|'千'|'\\.']*)元";
        String text = str_i.replace("人民币", "").replace("处", "罚款");
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(text);
        if (m.find()) {
            resStr = m.group(1);
        }
        try {
            res = Integer.valueOf(resStr);
            return res;
        } catch (java.lang.NumberFormatException nfe) {
            res = chineseToArabic(resStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 将金额大写转换为小写
     * @param cn
     * @return
     */
    public static Integer chineseToArabic(String cn){
        Map<String,Integer> cnNumMap = new HashMap<String,Integer>();
        cnNumMap.put("〇",0);
        cnNumMap.put("一",1);
        cnNumMap.put("二",2);
        cnNumMap.put("三",3);
        cnNumMap.put("四",4);
        cnNumMap.put("五",5);
        cnNumMap.put("六",6);
        cnNumMap.put("七",7);
        cnNumMap.put("八",8);
        cnNumMap.put("九",9);
        cnNumMap.put("零",0);
        cnNumMap.put("壹",1);
        cnNumMap.put("贰",2);
        cnNumMap.put("叁",3);
        cnNumMap.put("肆",4);
        cnNumMap.put("伍",5);
        cnNumMap.put("陆",6);
        cnNumMap.put("柒",7);
        cnNumMap.put("捌",8);
        cnNumMap.put("玖",9);
        cnNumMap.put("貮",2);
        cnNumMap.put("两",2);

        Map<String,Integer> cnUnitMap = new HashMap<String,Integer>();
        cnUnitMap.put("十",10);
        cnUnitMap.put("拾",10);
        cnUnitMap.put("百",100);
        cnUnitMap.put("佰",100);
        cnUnitMap.put("千",1000);
        cnUnitMap.put("仟",1000);
        cnUnitMap.put("万",10000);
        cnUnitMap.put("萬",10000);
        cnUnitMap.put("亿",100000000);
        cnUnitMap.put("億",100000000);

        Integer unit = 0;   // current
        List<Integer> ldig = new ArrayList<Integer>(); // digest
        String reversedCn = new StringBuffer(cn).reverse().toString();
        for(int i=0;i<reversedCn.length();i++){
            if(cnUnitMap.containsKey(String.valueOf(reversedCn.charAt(i)))){
                unit = cnUnitMap.get(String.valueOf(reversedCn.charAt(i)));
                if(unit == 10000 || unit == 100000000){
                    ldig.add(unit);
                    unit = 1;
                }
            }
            else{
                Integer dig = cnNumMap.get(String.valueOf(reversedCn.charAt(i)));
                if(unit != 0){
                    dig *= unit;
                    unit = 0;
                }
                if(dig != null){
                    ldig.add(dig);
                }

            }
        }
        if(unit == 10){
            ldig.add(10);
        }
        Integer val = 0;
        Integer tmp = 0;
        Collections.reverse(ldig);
        for(Integer x : ldig){
            if(x == 10000 || x == 100000000){
                val += tmp * x;
                tmp = 0;
            }
            else{
                tmp += x;
            }
        }

        val += tmp;

        return val;
    }

    @Test
    public  void met() {
        getPunishNumber("罚款五百元五角");
    }


}
