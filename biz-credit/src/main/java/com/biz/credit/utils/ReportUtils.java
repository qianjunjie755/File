package com.biz.credit.utils;

import com.biz.credit.domain.ModuleType;
import com.biz.credit.domain.Task;
import com.biz.credit.vo.ModuleTypeVO;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class ReportUtils {

    /**
     * list集合分页处理
     * @param listData
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static List<Task> getPageList(List<Task> listData , Integer pageNo, Integer pageSize){
        List<Task> list=new ArrayList<>();
        Integer totalCount = listData.size();//总记录数
        Integer fromIndex = (pageNo-1) * pageSize;//其实页码
        if(totalCount<fromIndex){
         return list;
        }
        //如果总数少于PAGE_SIZE,为了防止数组越界,toIndex直接使用totalCount即可
        Integer toIndex = Math.min(totalCount, pageNo * pageSize);//结束页码

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*
         *根据日期对list降序排列
         * int compare(Task o1, Task o2) 返回一个基本类型的整型，
         * 返回负数表示：o1 小于o2，
         * 返回0 表示：o1和o2相等，
         * 返回正数表示：o1大于o2。
         */
        Collections.sort(listData, new Comparator<Task>(){
            @Override
            public int compare(Task arg0, Task arg1) {
                int mark = 1;
                try {
                    long long0 =sdf.parse(arg0.getCreateTime()).getTime();
                    long long1= sdf.parse(arg1.getCreateTime()).getTime();
                    if(long0 < long1){
                        mark =  -1;
                    }
                    if(long0 == long1){
                        mark =  0;
                    }
                } catch (ParseException e) {
                    log.error("日期转换异常", e);
                }
                return -mark;// 不取反，则按正序排列
            }
        });

        listData = listData.subList(fromIndex, toIndex);

        return listData;
    }

    /**
     * list集合正序排序
     */
    public static List<ModuleTypeVO> getObjectListASC(List<ModuleTypeVO> listData ){

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*
         *根据id对list降序排列
         * int compare(Task o1, Task o2) 返回一个基本类型的整型，
         * 返回负数表示：o1 小于o2，
         * 返回0 表示：o1和o2相等，
         * 返回正数表示：o1大于o2。
         */
        Collections.sort(listData, new Comparator<ModuleType>(){
            @Override
            public int compare(ModuleType arg0, ModuleType arg1) {
                int mark = 1;
                try {
                    long long0 =arg0.getModuleTypeId();
                    long long1= arg1.getModuleTypeId();
                    if(long0 < long1){
                        mark =  -1;
                    }
                    if(long0 == long1){
                        mark =  0;
                    }
                } catch (Exception e) {
                    log.error("getObjectList类型转换异常", e);
                }
                return mark;// -mark 取反，则按倒序排列 ；mark 按正序排序
            }
        });

        return listData;
    }
}
