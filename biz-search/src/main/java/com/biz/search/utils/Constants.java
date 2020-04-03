package com.biz.search.utils;

import org.apache.commons.lang.math.NumberUtils;

public class Constants {
    private Constants() {}

    public static final String USER_TYPE = "userType";      //Session中用户类型的key
    public static final String USER_ID = "userId";      //Session中用户类型的key
    public static final String API_CODE = "apiCode";      //Session中用户类型的key
    public static final String GROUP_ID = "groupId";               //Session中用户分组的key
    public static final String BAIRONG_SUPER_ADMIN = NumberUtils.INTEGER_ONE.toString();  //百融超级管理员用户id
    public static final String USER_TYPE_SUPER_ADMIN = "0";     //超级管理员键值

    // IK 分词器
    public static final String IK_ANALYZER = "ik_max_word";
    public static final String IK_SEARCH_ANALYZER = "ik_smart";

    // ES BASIC INDEX / TYPE
    public static final String BASIC_INDEX = "basic_index";
    public static final String BASIC_TYPE = "basic_info";
}
