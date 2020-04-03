package com.biz.chart.utils;

import org.apache.commons.lang.math.NumberUtils;

public class Constants {
    private Constants() {}

    public static final String USER_ID = "userId";
    public static final String API_CODE = "apiCode";  //Session中用户类型的key
    public static final String USER_TYPE = "userType";      //Session中用户类型的key
    public static final String GROUP_ID = "groupId";               //Session中用户分组的key
    public static final String BAIRONG_SUPER_ADMIN = NumberUtils.INTEGER_ONE.toString();  //百融超级管理员用户id
    public static final String USER_TYPE_SUPER_ADMIN = "0";     //超级管理员键值
    //
    public static final String STAR_NETWORK = "1";
    public static final String TREE_NETWORK = "2";
}
