package com.biz.credit.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.DFlowPlatform;
import com.biz.credit.utils.tools.*;
import com.biz.credit.vo.DFlowBizVO;
import com.biz.credit.vo.DFlowLinkVO;
import com.biz.credit.vo.DFlowPlatformVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.*;
import java.util.regex.Pattern;

public class Constants {

    public static final String DATETIME_PATTERN_DATE_YYYY_MM_DD ="yyyy-MM-dd";
    public static final String DATETIME_PATTERN_DATE_YYYYMMDD="yyyyMMdd";
    public static final String DATETIME_PATTERN_DATE_YYYYMM="yyyyMM";

    public static final long USER_STATUS_VALID = 0;    //用户可用状态
    public static final long USER_STATUS_INVALID = 1;     //用户不可用状态+

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
    public static String REDIS_BIZ_PUBLIC_KEY = "biz_credit:publicKey";
    public static String SESSION_BIZ_PUBLIC_KEY = "bizPublicKey";

    public static String SCORE_CARD_RETURN_FIELD= "ScoreCardField";//评分卡返回字段类型

    public static final Integer COMMON_STATUS_VALID = 1;
    public static final Integer COMMON_STATUS_INVALID = 0;

    public static final long IS_TEMPLATE_FALSE = 0;    //是否为模板：非模板
    public static final long IS_TEMPLATE_TRUE = 1;     //是否为模板：模板

    public static final Integer MODULE_TYPE_API_STATUS_VALID = 1;
    public static final Integer MODULE_TYPE_API_STATUS_UNVALID = 0;

    public static final Integer INPUTFILE_DELETE_STATUS=4;

    //进件参数代码定义
    public static final String COMPANY_NAME = "company_name";
    public static final String CREDIT_CODE = "credit_code";
    public static final String BANK_ID = "bank_id";
    public static final String NAME = "name";
    public static final String ID_NO = "id_no";
    public static final String CELL = "cell";
    public static final String HOME_ADDR = "home_addr";
    public static final String WORK_ADDR = "biz_addr";
    public static final String STOCK_CODE = "stock_code";


    //评分卡是否发布
    public static final Integer SCORE_CARD_PUBLISH_YES = 1;  //发布
    public static final Integer SCORE_CARD_PUBLISH_NO = 0;   //未发布

    //评分api判断类型
    public static Integer API_CONDITION_TYPE_CONTINUITY = 1;  // 连续型
    public static Integer API_CONDITION_TYPE_DISCRETE = 2;    // 离散型

    //评分卡状态
    public static Integer SCORE_CARD_STATUS_VALID = 1; //有效
    public static Integer SCORE_CARD_STATUS_UNVALID = 0; //无效

    //评分边界类型
    public static Integer SCORE_BOUNDARY_TYPE_LEFT_CONTAIN = 1;  //左包含
    public static Integer SCORE_BOUNDARY_TYPE_RIGHT_CONTAIN = 2;  //右包含

    //评分卡类型
    public static Integer SCORE_CARD_TYPE_DEFAULT = 1;  //默认类型
    public static Integer SCORE_CARD_TYPE_DIMENSION = 2;  //带维度

    public static String SCORE_CARD_DEFAULT_VALUE = "1.0";
    public static Integer DRULE_TYPE_CPMPANY = 1; //企业
    public static Integer DRULE_TYPE_PERSON = 2; //个人

    //决策树
    public static Integer TREE_STATUS_PUSH=1;  //发布
    public static Integer TREE_STATUS_SAVE=0; //保存
    public static Integer TREE_STATUS_UNVALID=2;//取消

    public static Integer TREEVAR_STATUS_VALID=1;  //生效
    public static Integer TREEVAR_STATUS_UNVALID=0; //失效

    public static Integer TREECOND_STATUS_VALID=1;  //生效
    public static Integer TREECOND_STATUS_UNVALID=0; //失效


    //决策表
    public static Integer TABLE_STATUS_PUSH=1;  //发布
    public static Integer TABLE_STATUS_SAVE=0; //保存
    public static Integer TABLE_STATUS_UNVALID=2;//取消

    public static Integer TABLEVAR_STATUS_VALID=1;  //生效
    public static Integer TABLEVAR_STATUS_UNVALID=0; //失效
    public static Integer TABLECOND_STATUS_VALID=1;  //生效
    public static Integer TABLECOND_STATUS_UNVALID=0; //失效

    //模型类型 1-企业规则 2-自然人规则 3-反欺诈评分 4-评分模型 5-评分卡 6-决策表 7-决策树 8-雷达模型
    public static Integer ENTERPRISE_RULE =1;
    public static Integer NATURAL_PERSON_RULE=2;
    public static Integer ANTI_FRAUD_SCORE=3;
    public static Integer CREDIT_SCORE_MODEL=4;
    public static Integer SCORE_CARD=5;
    public static Integer DECISION_TABLE=6;
    public static Integer DECISION_TREE=7;
    public static Integer RADAR_MODEL=8;

    public static String ALL_SRC_VAR_LIST = "allSrcVarList";
    public static String ALL_INSTANCE_VAR_LIST = "allInstanceVarList";
    public static String ALL_SRC_REF_VAR_LIST = "allSrcRefVarList";
    public static String ALL_INSTANCE_REF_VAR_LIST = "allInstanceRefVarList";


    public static final String companyName = "公司全名";
    public static final String idNumber = "法人代表身份证";
    public static final String legalPerson = "法人代表姓名";
    public static final String cellPhone = "法人代表电话号码";
    public static final String creditCode = "统一社会信用代码";
    public static final String bankId = "企业开户账号";
    public static final String homeAddress = "法人代表居住地址";
    public static final String bizAddress = "法人代表工作地址";
    public static final String applyIdNumber = "申请人身份证";
    public static final String applyLegalPerson = "申请人姓名";
    public static final String applyCellPhone = "申请人电话号码";
    public static final String applyHomeAddress = "申请人居住地址";
    public static final String applyBizAddress = "申请人工作地址";
    public static final String DetailCompanyGroupTitle = "填写企业进件参数";
    public static final String DetailLegalPersonTitle = "填写法人进件参数";
    public static final String DetailApplyPersonTitle = "填写申请人进件参数";
    public static final String DetailRelatedPersonTitle = "填写关联人进件参数";
    public static final String RelatedPerson = "关联人姓名";
    public static final String RelatedCellPhone = "关联人电话号码";
    public static final String RelatedIdNumber = "关联人身份证";
    public static final String RelatedHomeAddress = "关联人居住地址";
    public static final String RelatedBizAddress = "关联人工作地址";
    public static final String ApiInputFileDetail = "apiInputFileDetailId";
    public static final String ApiContactId = "apiContactId";
    //public static String ApiTask = "apiTaskId";

    public static final Map<Integer, DFlowPlatformVO> FLOW_PLATFORM_MAP = new LinkedHashMap<>();
    public static final Map<Integer, DFlowBizVO> FLOW_BIZ_MAP = new LinkedHashMap<>();
    public static final Map<Integer, DFlowLinkVO> FLOW_LINK_MAP = new LinkedHashMap<>();



    public static Map<Integer, IValidator> EXCEL_VALIDATORS = new HashMap<Integer, IValidator>() {
        {
            put(0, new CompanyValidator());
            put(1, new IDCard());
            put(2, new NameValidator());
            put(3, new PhoneValidator());
        }
    };
    public static Map<String, IValidator> EXCEL_VALIDATORS2 = new HashMap<String, IValidator>() {
        {
            //公司全名	法人身份证	法人姓名	 法人电话号码 	统一社会信用代码	企业开户账号
            put(companyName, new CompanyValidator());
            put(idNumber, new IDCard());
            put(legalPerson, new NameValidator());
            put(cellPhone, new PhoneValidator());
            put(creditCode, new Regex_CreditCode());
            put(bankId, new BankIdValidator());
            put(homeAddress, new HomeAddressValidator());
            put(bizAddress, new WorkAddressValidator());
            put(RelatedPerson, new NameValidator());
            put(RelatedIdNumber, new IDCard());
            put(RelatedCellPhone, new PhoneValidator());
            put(RelatedBizAddress, new WorkAddressValidator());
            put(RelatedHomeAddress, new HomeAddressValidator());
            put(applyLegalPerson, new NameValidator());
            put(applyIdNumber, new IDCard());
            put(applyCellPhone, new PhoneValidator());
            put(applyBizAddress, new WorkAddressValidator());
            put(applyHomeAddress, new HomeAddressValidator());
        }
    };


    public static Map<String, String> DetailPropMap = new HashMap<String, String>() {
        {
            put(companyName, "keyNo");
            put(idNumber, "idNumber");
            put(legalPerson, "name");
            put(cellPhone, "cellPhone");
            put(creditCode, "creditCode");
            put(bankId, "bankId");
            put(homeAddress, "homeAddr");
            put(bizAddress, "bizAddr");
            put(applyIdNumber, "idNumber");
            put(applyLegalPerson, "name");
            put(applyCellPhone, "cellPhone");
            put(applyHomeAddress, "homeAddr");
            put(applyBizAddress, "bizAddr");
            put(RelatedPerson, "relatedPerson");
            put(RelatedIdNumber, "relatedIdNumber");
            put(RelatedCellPhone, "relatedCellPhone");
            put(RelatedBizAddress, "relatedBizAddress");
            put(RelatedHomeAddress, "relatedHomeAddress");
        }
    };


    public static LinkedHashMap<String, List<String>> DetailInputGroupList = new LinkedHashMap<String, List<String>>() {
        {
            put(DetailCompanyGroupTitle, new ArrayList<String>() {{
                add(companyName);
                add(creditCode);
                add(bankId);
            }});
            put(DetailLegalPersonTitle, new ArrayList<String>() {{
                add(legalPerson);
                add(idNumber);
                add(cellPhone);
                add(homeAddress);
                add(bizAddress);
            }});
            put(DetailRelatedPersonTitle, new ArrayList<String>() {{
                add(RelatedPerson);
                add(RelatedIdNumber);
                add(RelatedCellPhone);
                add(RelatedHomeAddress);
                add(RelatedBizAddress);
            }});
        }
    };

    public static List<JSONObject> DetailInputGroups = new ArrayList<JSONObject>() {
        {
            add(new JSONObject() {
                {
                    put("index", 0);
                    put("title", DetailCompanyGroupTitle);
                    put("addable", 0);
                    put("items", new JSONArray() {
                        {
                            add(new JSONObject() {{
                                put("key", companyName);
                                put("value", StringUtils.EMPTY);
                                put("required", 1);
                            }});
                            add(new JSONObject() {{
                                put("key", creditCode);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", bankId);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                        }
                    });
                }
            });
            add(new JSONObject() {
                {
                    put("index", 1);
                    put("title", DetailLegalPersonTitle);
                    put("addable", 0);
                    put("items", new JSONArray() {
                        {
                            add(new JSONObject() {{
                                put("key", legalPerson);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", idNumber);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", cellPhone);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", homeAddress);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", bizAddress);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                        }
                    });
                }
            });
            add(new JSONObject() {
                {
                    put("index", 2);
                    put("title", DetailRelatedPersonTitle);
                    put("addable", 1);
                    put("items", new JSONArray() {
                        {
                            add(new JSONObject() {{
                                put("key", RelatedPerson);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedIdNumber);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedCellPhone);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedHomeAddress);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedBizAddress);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                        }
                    });
                }
            });
            add(new JSONObject() {
                {
                    put("index", 3);
                    put("title", "填写关联人进件参数");
                    put("addable", 1);
                    put("items", new JSONArray() {
                        {
                            add(new JSONObject() {{
                                put("key", RelatedPerson);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedIdNumber);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedCellPhone);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedHomeAddress);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                            add(new JSONObject() {{
                                put("key", RelatedBizAddress);
                                put("value", StringUtils.EMPTY);
                                put("required", 0);
                            }});
                        }
                    });
                }
            });
        }
    };


/*    public static Map<String,ArrayList<LinkedHashMap<String,String>>> DetailInputGroups = new LinkedHashMap<String,ArrayList<LinkedHashMap<String,String>>>() {
        {
            put("填写企业进件参数",new ArrayList<LinkedHashMap<String,String>>(){
                {
                    add(new LinkedHashMap<String,String>(){{put("key",companyName);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",creditCode);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",bankId);put("value",StringUtils.EMPTY);}});
                }
            });
            put("填写法人进件参数",new ArrayList<LinkedHashMap<String,String>>(){
                {
                    add(new LinkedHashMap<String,String>(){{put("key",legalPerson);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",idNumber);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",cellPhone);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",homeAddress);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",bizAddress);put("value",StringUtils.EMPTY);}});
                }
            });
            put("填写关联人进件参数",new ArrayList<LinkedHashMap<String,String>>(){
                {
                    add(new LinkedHashMap<String,String>(){{put("key",legalPerson);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",idNumber);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",cellPhone);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",cellPhone);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",homeAddress);put("value",StringUtils.EMPTY);}});
                    add(new LinkedHashMap<String,String>(){{put("key",bizAddress);put("value",StringUtils.EMPTY);}});
                }
            });
        }
    };*/


    public static Map<String, List<String>> ParamNameMap = new HashMap<String, List<String>>() {
        {
            put("keyNo", new ArrayList<String>() {{
                add("keyNo");
                add("company_name");
                add("search_key");
                add("key_no");
            }});
            put("creditCode", new ArrayList<String>() {{
                add("creditCode");
                add("credit_code");
            }});
            put("name", new ArrayList<String>() {{
                add("legal_person");
                add("name");
                add("_name");
                add("person_name");
            }});
            put("idNumber", new ArrayList<String>() {{
                add("idNumber");
                add("person_id");
                add("id");
                add("_id");
            }});
            put("cellPhone", new ArrayList<String>() {{
                add("cellPhone");
                add("cell");
            }});
            put("bankId", new ArrayList<String>() {{
                add("bankId");
                add("_bank_id");
                add("bank_id");
            }});
            put("homeAddr", new ArrayList<String>() {{
                add("homeAddr");
                add("home_addr");
            }});
            put("bizAddr", new ArrayList<String>() {{
                add("bizAddr");
                add("biz_addr");
            }});
        }
    };
    public static Map<String, String> EXCEL_EXAMPLE_VALUES = new HashMap<String, String>() {
        {
            put(companyName, "xx公司");
            put(idNumber, "310108xxxxxxxx033");
            put(legalPerson, "王XX");
            put(cellPhone, "1351xxxx678");
            put(creditCode, "913xxxxxxxxxxx8040");
            put(bankId, "622xxxxxxxxxxx7244");
            put(homeAddress, "上海市浦东新区xxx路xx号xx室");
            put(bizAddress, "上海市浦东新区xxx路xx号xx室");
            put(RelatedPerson, "王XX");
            put(RelatedIdNumber, "310108xxxxxxxx033");
            put(RelatedCellPhone, "1351xxxx678");
            put(RelatedBizAddress, "上海市浦东新区xxx路xx号xx室");
            put(RelatedHomeAddress, "上海市浦东新区xxx路xx号xx室");
            put(applyLegalPerson, "王XX");
            put(applyIdNumber, "310108xxxxxxxx033");
            put(applyCellPhone, "1351xxxx678");
            put(applyBizAddress, "上海市浦东新区xxx路xx号xx室");
            put(applyHomeAddress, "上海市浦东新区xxx路xx号xx室");
        }
    };

    public static Set<String> ERROR_CODES = new HashSet<String>() {
        {
            add("40000");
            add("50000");
            add("60000");
            add("80000");
            add("80001");
            add("80011");
            add("90001");
            add("90002");
        }
    };
    //入参需要4要素的接口集合（法人姓名，法人身份证，公司全名，统一社会信用代码）
    public static Set<String> BizFourVariable = new HashSet<String>() {
        {
            add("var.ic.company_name.match");
            add("var.ic.legal_person_name.match");
            add("var.ic.legal_person_id.match");
        }
    };
    //入参需要bank_id的接口集合
    public static Set<String> BizBankIdVariable = new HashSet<String>() {
        {
            add("var.ic.bank_account.match");
        }
    };

    //返回结果为中文unicode码的接口集合
    public static Set<String> CN_RESULT_API = new HashSet<String>() {
        {
            add("var.customs.credit_level");
        }
    };

    public static String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};

    public boolean find(String str) {
        boolean ret = false;
        if (StringUtils.isNotEmpty(str)) {
            return Pattern.compile("[\u4e00-\u9fa5]").matcher(str).find();
        }
        return ret;
    }

    //public static final Map<String>


    public static List<Object> getHeadList(List list) {
        List<Object> objectList = new ArrayList<>();
        for (Object s : list) {
            JSONObject json = new JSONObject();
            ////公司全名	法人身份证	法人姓名	 法人电话号码 	统一社会信用代码 	企业开户账号
            //keyNo#idNumber#name#cellPhone#creditCode#bankId
            if ("keyNo".equals(s.toString())) {
                json.put("keyNo", companyName);
                objectList.add(json);
            } else if ("idNumber".equals(s.toString())) {
                json.put("idNumber", idNumber);
                objectList.add(json);
            } else if ("name".equals(s.toString())) {
                json.put("name", legalPerson);
                objectList.add(json);
            } else if ("cellPhone".equals(s.toString())) {
                json.put("cellPhone", cellPhone);
                objectList.add(json);
            } else if ("creditCode".equals(s.toString())) {
                json.put("creditCode", creditCode);
                objectList.add(json);
            } else if ("bankId".equals(s.toString())) {
                json.put("bankId", bankId);
                objectList.add(json);
            }
        }
        return objectList;
    }
    public static final Long BANK_ID_REMOVED_TIME = 1553497200000L;   //2019-03-25 15:00:00
}
