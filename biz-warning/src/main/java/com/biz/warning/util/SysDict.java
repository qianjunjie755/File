package com.biz.warning.util;

import com.biz.warning.util.tools.*;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统数据字典表，存储系统常量
 */
public class SysDict {
    public static final long USER_STATUS_VALID = 0;    //用户可用状态
    public static final long USER_STATUS_INVALID = 1;     //用户不可用状态+

    public static final String COOKIE = "cookie";
    public static final String USER_TYPE = "userType";      //Session中用户类型的key
    public static final String USER_ID = "userId";      //Session中用户类型的key
    public static final String API_CODE = "apiCode";      //Session中用户类型的key
    public static final String INSTITUTION_ID = "institution_id";  //Session中用户所属机构的key
    public static final String GROUP_ID = "groupId";               //Session中用户分组的key
    public static final String BAIRONG_API_CODE = "4000159";  //百融apicode
    public static final String BAIRONG_SUPER_ADMIN = NumberUtils.INTEGER_ONE.toString();  //百融超级管理员用户id
    public static final String USER_TYPE_NORMAL = "1";     //普通用户键值
    public static final String USER_TYPE_SUPER_ADMIN = "0";     //超级管理员键值
    public static final String USER_TYPE_ADMIN = "2";     //管理员键值


    public static final long IS_TEMPLATE_FALSE = 0;    //是否为模板：非模板
    public static final long IS_TEMPLATE_TRUE = 1;     //是否为模板：模板

    public static final int IS_ORIGIN_FALSE = 0;    //是否为根源：非根源
    public static final int IS_ORIGIN_TRUE = 1;     //是否为根源：根源

    public static final long AND = 0;    //运算逻辑AND
    public static final long OR = 1;     //运算逻辑OR


    public static final long RULE_STATUS_DRAFT = 0;    //规则状态：草稿
    public static final long RULE_STATUS_ACTIVE = 1;     //规则状态：生效并推广
    public static final long RULE_STATUS_INACTIVE = 2;     //规则状态：生效不推广

    public static final long STRATEGY_STATUS_DRAFT = 0;    //策略状态：草稿
    public static final long STRATEGY_STATUS_ACTIVE = 1;     //策略状态：生效

    public static final long TASK_STATUS_READY = 0;    //任务状态：保存
    public static final long TASK_STATUS_ACTIVE = 1;     //任务状态：生效
    public static final long TASK_STATUS_INACTIVE = 2;   //任务状态：失效


    public static final int WARN_RESULT_ORDER_BY_TIME = 0;      //预警结果排序：按时间轴排序
    public static final int WARN_RESULT_ORDER_BY_ORDER = 1;     //预警结果排序：按规则排序



    public static final String RULE_OPER_LOG_ADD = "新增";    //规则操作日志：新增
    public static final String RULE_OPER_LOG_UPDATE = "编辑";     //规则操作日志：编辑
    public static final String RULE_OPER_LOG_EFFECTIVE = "生效";
    public static final String RULE_OPER_LOG_INVALID = "失效";
    public static final String RULE_OPER_LOG_EXTENSION = "推广";
    public static final String RULE_OPER_LOG_CANCEL_EXTENSION = "取消推广";
    public static String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};

    public static final String companyName = "公司全名";
    public static final String idNumber = "法人代表身份证";
    public static final String legalPerson = "法人代表姓名";
    public static final String cellPhone = "法人代表电话号码";
    public static final String creditCode = "统一社会信用代码";
    public static final String bankId = "企业开户账号";
    public static final String homeAddress = "法人代表居住地址";
    public static final String bizAddress = "法人代表工作地址";
    public static final String ENTITY_EXPIRE_TIME_NAME = "截止日期";
    public static final String ENTITY_APPLICATION_DATE = "申请日期";
    public static final Integer ENTITY_STATUS_VALID = 1;
    public static final Integer ENTITY_STATUS_EXPIRED = 2;
    public static final Integer ENTITY_STATUS_DELETED = 3;
    public static final String MONITOR_STATUS_VALID = "监控中";
    public static final String MONITOR_STATUS_EXPIRE = "过期";
    public static final String MONITOR_STATUS_INVALID = "失效";

    //进件参数代码定义
    public static final String COMPANY_NAME = "company_name";
    public static final String CREDIT_CODE = "credit_code";
    public static final String BANK_ID = "bank_id";
    public static final String NAME = "name";
    public static final String ID_NO = "id_no";
    public static final String CELL = "cell";
    public static final String HOME_ADDR = "home_addr";
    public static final String WORK_ADDR = "biz_addr";
    public static final String APPLY_DATE = "apply_date";

    public static final String COLLECTION_REPLACE_TO_STRING = "\\[|\\]|\u0020";


    public static Map<String,String> EXCEL_EXAMPLE_VALUES = new HashMap<String,String>(){
        {
            put(companyName,"xx公司");
            put(idNumber,"310108xxxxxxxx033");
            put(legalPerson,"王XX");
            put(cellPhone,"1351xxxx678");
            put(creditCode,"913xxxxxxxxxxx8040");
            put(bankId,"622xxxxxxxxxxx7244");
            put(homeAddress,"上海市浦东新区xxx路xx号xx室");
            put(bizAddress, "上海市浦东新区xxx路xx号xx室");
            put(ENTITY_EXPIRE_TIME_NAME,"1900/01/01");
            put(ENTITY_APPLICATION_DATE,"1900/01/01");
        }
    };

    public static Map<String,IValidator> EXCEL_VALIDATORS = new HashMap<String,IValidator>(){
        {
            //公司全名	法人身份证	法人姓名	 法人电话号码 	统一社会信用代码	企业开户账号
            put(companyName,new CompanyValidator());
            put(idNumber,new IDCard());
            put(legalPerson,new NameValidator());
            put(cellPhone,new PhoneValidator());
            put(creditCode,new Regex_CreditCode());
            put(bankId,new BankIdValidator());
            put(homeAddress,new HomeAddressValidator());
            put(bizAddress, new WorkAddressValidator());
            put(ENTITY_EXPIRE_TIME_NAME,new ExpireTimeValidator());
            put(ENTITY_APPLICATION_DATE,new ApplicationDateValidator());
        }
    };



    public static Map<String,List<String>> EntityNameMap = new HashMap<String,List<String>>(){
        {
            put("companyName",new ArrayList<String>(){{
                add("companyName");add("company_name");add("search_key");add("key_no");
            }});
            put("creditCode",new ArrayList<String>(){{
                add("creditCode");add("credit_code");
            }});
            put("legalPerson",new ArrayList<String>(){{
                add("legalPerson");add("legal_person");add("name");
            }});
            put("personId",new ArrayList<String>(){{
                add("personId");add("person_id");add("id");
            }});
            put("cell",new ArrayList<String>(){{
                add("cell");
            }});
            put("bankId",new ArrayList<String>(){{
                add("bankId");add("_bank_id");add("bank_id");
            }});
            put("homeAddr",new ArrayList<String>(){{
                add("homeAddr");add("home_addr");
            }});
            put("bizAddr",new ArrayList<String>(){{
                add("bizAddr");add("biz_addr");
            }});
            put("expireTime",new ArrayList<String>(){{
                add("expireTime");
            }});
            put("applicationDate",new ArrayList<String>(){{
                add("applicationDate");
            }});
        }

    };
    public static  Map<String,String> DetailPropMap = new HashMap<String,String>(){
        {
            put(companyName,"companyName");
            put(idNumber,"personId");
            put(legalPerson,"legalPerson");
            put(cellPhone,"cell");
            put(creditCode,"creditCode");
            put(bankId,"bankId");
            put(homeAddress,"homeAddr");
            put(bizAddress,"bizAddr");
            put(ENTITY_EXPIRE_TIME_NAME,"expireTime");
            put(ENTITY_APPLICATION_DATE,"applicationDate");
        }
    };



}
