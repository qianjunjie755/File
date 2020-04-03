package com.biz.model.radar.chart.v10;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamConst {


    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String SPAN = "span";




    public static final Map<String,String> PARAM_ITEM_GROUP = new HashMap<String,String>(){
        {
            put(param_EntStatus,COMPANY_QUALIFICATION);
            put(param_EsDateDetailYear,COMPANY_QUALIFICATION);
            put(param_FiliationNum,COMPANY_QUALIFICATION);
            put(param_PersonNum,COMPANY_QUALIFICATION);
            put(param_RegCap,COMPANY_QUALIFICATION);

            put(param_FrInvEsDateMax,COMPANY_CAPACITY);
            put(param_FrInvEsDateMin,COMPANY_CAPACITY);
            put(param_FrInvFundedRatioAll,COMPANY_CAPACITY);
            put(param_FrInvFundedRatioAvg,COMPANY_CAPACITY);
            put(param_FrInvFundedRatioMax,COMPANY_CAPACITY);
            put(param_FrInvRegCapMax,COMPANY_CAPACITY);
            put(param_FrInvRegCapMin,COMPANY_CAPACITY);
            put(param_FrInvSubConAmtAll,COMPANY_CAPACITY);
            put(param_FrInvSubConAmtMin,COMPANY_CAPACITY);
            put(param_FrInvTimes,COMPANY_CAPACITY);

            put(param_Alter_1,COMPANY_STABILITY);
            put(param_Alter_1_12,COMPANY_STABILITY);
            put(param_Alter_3,COMPANY_STABILITY);
            put(param_Alter_4,COMPANY_STABILITY);
            put(param_Alter_6_12,COMPANY_STABILITY);
            put(param_Alter_8,COMPANY_STABILITY);
            put(param_AlterDetalDaysMin,COMPANY_STABILITY);
            put(param_AlterTimes,COMPANY_STABILITY);
            put(param_SharesImpawnTimes,COMPANY_STABILITY);

            put(param_JudgmentDoc_1,COMPANY_HONESTY);
            put(param_JudgmentDoc_12,COMPANY_HONESTY);
            put(param_JudgmentDocTimes,COMPANY_HONESTY);
            put(param_CaseTimes,COMPANY_HONESTY);
            //put(param_justiceExecutedCnt,COMPANY_HONESTY);
            //put(param_justiceDefaultCnt,COMPANY_HONESTY);

            put(param_AlsLstIdNbankInteday,COMPANY_PERSON);
            put(param_AlsLstIdBankInteday,COMPANY_PERSON);
            put(param_AlsM1IdNbankP2pAllnum,COMPANY_PERSON);
            put(param_AlsM12IdRelAllnum,COMPANY_PERSON);
            put(param_AlsM3IdCaonOrgnum,COMPANY_PERSON);
            put(param_AlsM12IdNbankP2pAllnum,COMPANY_PERSON);
        }
    };


    public static final String COMPANY_QUALIFICATION = "qualification";//企业资质
    public static final String COMPANY_CAPACITY = "capacity";//企业经营能力
    public static final String COMPANY_STABILITY = "stability";//企业稳定性
    public static final String COMPANY_HONESTY = "honesty";//企业诚信
    public static final String COMPANY_PERSON = "person";//个人

    public static final String param_EntStatus = "EntStatus";
    public static final String param_EsDateDetailYear = "EsDateDetailYear";
    public static final String param_FiliationNum = "FiliationNum";
    public static final String param_PersonNum = "PersonNum";
    public static final String param_RegCap = "RegCap";

    public static final String param_FrInvEsDateMax = "FrInvEsDateMax";
    public static final String param_FrInvEsDateMin = "FrInvEsDateMin";
    public static final String param_FrInvFundedRatioAll = "FrInvFundedRatioAll";
    public static final String param_FrInvFundedRatioAvg = "FrInvFundedRatioAvg";
    public static final String param_FrInvFundedRatioMax = "FrInvFundedRatioMax";
    public static final String param_FrInvRegCapMax = "FrInvRegCapMax";
    public static final String param_FrInvRegCapMin = "FrInvRegCapMin";
    public static final String param_FrInvSubConAmtAll = "FrInvSubConAmtAll";
    public static final String param_FrInvSubConAmtMin = "FrInvSubConAmtMin";
    public static final String param_FrInvTimes= "FrInvTimes";


    public static final String param_Alter_1 = "Alter_1";
    public static final String param_Alter_1_12 = "Alter_1_12";
    public static final String param_Alter_3 = "Alter_3";
    public static final String param_Alter_4 = "Alter_4";
    public static final String param_Alter_6_12 = "Alter_6_12";
    public static final String param_Alter_8 = "Alter_8";
    public static final String param_AlterDetalDaysMin = "AlterDetalDaysMin";
    public static final String param_AlterTimes = "AlterTimes";
    public static final String param_SharesImpawnTimes = "SharesImpawnTimes";

    public static final String param_JudgmentDoc_1 = "JudgmentDoc_1";
    public static final String param_JudgmentDoc_12 = "JudgmentDoc_12";
    public static final String param_JudgmentDocTimes = "JudgmentDocTimes";
    public static final String param_CaseTimes = "CaseTimes";

    public static final String param_AlsLstIdNbankInteday = "AlsLstIdNbankInteday";
    public static final String param_AlsLstIdBankInteday = "AlsLstIdBankInteday";;
    public static final String param_AlsM12IdRelAllnum = "AlsM12IdRelAllnum";
    public static final String param_AlsM3IdCaonOrgnum = "AlsM3IdCaonOrgnum";
    public static final String param_AlsM12IdNbankP2pAllnum = "AlsM12IdNbankP2pAllnum";
    public static final String param_AlsM1IdNbankP2pAllnum = "AlsM1IdNbankP2pAllnum";


    public static void main(String[] args) {
        BigDecimal b1 = new BigDecimal(Double.MAX_VALUE*-1);
        BigDecimal b2 = new BigDecimal(Double.MAX_VALUE*-1).add(new BigDecimal("1"));
        System.out.println(b1);
        System.out.println(b1.compareTo(b2));
    }

    public static Map<String,Map<String,List<BigDecimal>>> RADAR_PARAM_SCORE = new HashMap<String,Map<String,List<BigDecimal>>>(){
        {
            put(COMPANY_QUALIFICATION,new HashMap<String,List<BigDecimal>>(){
                {
                    put(param_EntStatus,new ArrayList<BigDecimal>(){{ add(new BigDecimal("0.81848099134694"));add(new BigDecimal("-0.0625042573608712")); }});
                    put(param_EsDateDetailYear,new ArrayList<BigDecimal>(){{add(new BigDecimal("35.9691212557604"));add(new BigDecimal("-2.7298549348071"));add(new BigDecimal("-12.3274084481672"));add(new BigDecimal("11.4950718155938"));add(new BigDecimal("43.8058254875881"));}});
                    put(param_FiliationNum,new ArrayList<BigDecimal>(){{add(new BigDecimal("-0.176721258575975"));add(new BigDecimal("-4.57215803078843"));add(new BigDecimal("8.45064592105871"));}});
                    put(param_PersonNum,new ArrayList<BigDecimal>(){{add(new BigDecimal("26.7852151429835"));add(new BigDecimal("12.7600130060102"));add(new BigDecimal("-13.7211615418802"));add(new BigDecimal("-1.06424420240741"));add(new BigDecimal("27.3724367346361"));}});
                    put(param_RegCap,new ArrayList<BigDecimal>(){{add(new BigDecimal("17.9455102363674"));add(new BigDecimal("7.16519842865235"));add(new BigDecimal("8.07728835511248"));add(new BigDecimal("-6.79364989965501"));add(new BigDecimal("-2.84859899539201"));add(new BigDecimal("9.66637816828969"));}});
                }
            });
            put(COMPANY_CAPACITY,new HashMap<String,List<BigDecimal>>(){{
                put(param_FrInvEsDateMax,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("5.78960641137018"));add(new BigDecimal("-2.15649667944594"));add(new BigDecimal("-4.18815573139556"));add(new BigDecimal("0.900848271228352"));add(new BigDecimal("4.96105851709038"));
                }});
                put(param_FrInvEsDateMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("6.18909548270942"));
                    add(new BigDecimal("1.33210819463503"));
                    add(new BigDecimal("-4.38334019995808"));
                    add(new BigDecimal("-2.17634130856292"));
                    add(new BigDecimal("2.05259276574188"));
                }});
                put(param_FrInvFundedRatioAll,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-4.26670590849609"));
                    add(new BigDecimal("2.62182924196656"));
                    add(new BigDecimal("0.244260105967846"));
                }});
                put(param_FrInvFundedRatioAvg,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("0.231132294105571"));
                    add(new BigDecimal("-0.111580286753748"));
                }});
                put(param_FrInvFundedRatioMax,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("23.4360542879971"));
                    add(new BigDecimal("-11.0505875933632"));
                }});
                put(param_FrInvRegCapMax,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("7.82383941886375"));
                    add(new BigDecimal("1.96699066172119"));
                    add(new BigDecimal("-3.28116544205283"));
                    add(new BigDecimal("1.92934687596385"));
                }});
                put(param_FrInvRegCapMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("4.74644402514582"));
                    add(new BigDecimal("0.573353935339263"));
                    add(new BigDecimal("-0.856722619982216"));
                    add(new BigDecimal("-3.93767255861139"));
                    add(new BigDecimal("-1.05293513315939"));
                }});
                put(param_FrInvSubConAmtAll,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("0.520013990957083"));
                    add(new BigDecimal("0.181580211304632"));
                    add(new BigDecimal("-0.393960679474836"));
                    add(new BigDecimal("-0.226288169281493"));
                    add(new BigDecimal("0.145911907729733"));
                }});
                put(param_FrInvSubConAmtMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-0.483776847805228"));
                    add(new BigDecimal("-0.330768840169311"));
                    add(new BigDecimal("0.176101964011016"));
                    add(new BigDecimal("0.430800778741395"));
                    add(new BigDecimal("-0.10759182241046"));
                }});
                put(param_FrInvTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-3.57428549062933"));
                    add(new BigDecimal("1.88778590293594"));
                    add(new BigDecimal("0.460605811877766"));
                    add(new BigDecimal("-1.43184834574479"));
                    add(new BigDecimal("-2.40289530987216"));
                }});
            }});
            put(COMPANY_STABILITY,new HashMap<String,List<BigDecimal>>(){{
                put(param_Alter_1,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-10.1536947232108"));
                    add(new BigDecimal("18.7944305566527"));
                    add(new BigDecimal("47.5126558552338"));
                    add(new BigDecimal("61.0443741497058"));
                }});
                put(param_Alter_1_12,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("0.198204372510558"));
                    add(new BigDecimal("-0.901791083388873"));
                    add(new BigDecimal("-2.60879856906013"));
                }});
                put(param_Alter_3,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-1.82512455682461"));
                    add(new BigDecimal("14.5211100455082"));
                }});
                put(param_Alter_4,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("5.07223078114741"));
                    add(new BigDecimal("-28.0738436868438"));
                }});
                put(param_Alter_6_12,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("2.45465207238878"));
                    add(new BigDecimal("-25.9620684066503"));
                }});
                put(param_Alter_8,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("2.35731440148579"));
                    add(new BigDecimal("-19.5603851811945"));
                }});
                put(param_AlterDetalDaysMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-2.67774938325353"));
                    add(new BigDecimal("-10.7138988959043"));
                    add(new BigDecimal("-2.47174550868927"));
                    add(new BigDecimal("10.7913180646672"));
                }});
                put(param_AlterTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-6.0456029661433"));
                    add(new BigDecimal("-22.1720086502582"));
                    add(new BigDecimal("10.9494949584362"));
                    add(new BigDecimal("11.0148806971727"));
                }});
                put(param_SharesImpawnTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("5.29497708325242"));
                    add(new BigDecimal("-54.973368620697"));
                }});
            }});
            put(COMPANY_HONESTY,new HashMap<String,List<BigDecimal>>(){{
                put(param_JudgmentDoc_1,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-3.79159487446538"));
                    add(new BigDecimal("25.4764661200353"));
                }});
                put(param_JudgmentDoc_12,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("3.09928164390823"));
                    add(new BigDecimal("-27.1093639221405"));
                }});
                put(param_JudgmentDocTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("1.68361742129217"));
                    add(new BigDecimal("37.5274809542167"));
                    add(new BigDecimal("-41.1032508431548"));
                }});
                put(param_CaseTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("1.27120828553607"));
                    add(new BigDecimal("-19.5179862024838"));
                }});
            }});
            put(COMPANY_PERSON,new HashMap<String,List<BigDecimal>>(){{
                put(param_AlsLstIdNbankInteday,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("36.5953230886246"));
                    add(new BigDecimal("-31.9212804903095"));
                    add(new BigDecimal("-22.3699312171079"));
                    add(new BigDecimal("-5.64135057190712"));
                    add(new BigDecimal("13.4397918971818"));
                }});
                put(param_AlsLstIdBankInteday,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-1.17540744567159"));
                    add(new BigDecimal("-19.2779079864155"));
                    add(new BigDecimal("-9.84219458302306"));
                    add(new BigDecimal("23.2300244540431"));
                    add(new BigDecimal("26.2382234590943"));
                }});
                put(param_AlsM1IdNbankP2pAllnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("0.371905361215697"));
                    add(new BigDecimal("-3.15217154686662"));
                }});

                put(param_AlsM12IdRelAllnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("0.115381108164869"));
                    add(new BigDecimal("2.4120588977333"));
                    add(new BigDecimal("-5.38758269241901"));
                }});
                put(param_AlsM3IdCaonOrgnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("-3.35704746207826"));
                    add(new BigDecimal("14.6751607627211"));
                }});
                put(param_AlsM12IdNbankP2pAllnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal("9.03328089880116"));
                    add(new BigDecimal("-27.2356193779694"));
                }});
            }});
        }
    };




    public static Map<String,Map<String,List<BigDecimal>>> RADAR_PARAM_RANGE = new HashMap<String,Map<String,List<BigDecimal>>>(){
        {
            put(COMPANY_QUALIFICATION,new HashMap<String,List<BigDecimal>>(){{
                put(param_EntStatus,new ArrayList<BigDecimal>(){
                    {
                        add(new BigDecimal(-1*Double.MAX_VALUE));
                        add(new BigDecimal("0.5"));
                        add(new BigDecimal(Double.MAX_VALUE));
                    }
                });
                put(param_EsDateDetailYear,new ArrayList<BigDecimal>(){
                    {
                        add(new BigDecimal(-1*Double.MAX_VALUE));
                        add(new BigDecimal("1.275"));
                        add(new BigDecimal("2.385"));
                        add(new BigDecimal("9.545"));
                        add(new BigDecimal("18.305"));
                        add(new BigDecimal(Double.MAX_VALUE));
                    }
                });
                put(param_FiliationNum,new ArrayList<BigDecimal>(){
                    {
                        add(new BigDecimal(-1*Double.MAX_VALUE));
                        add(new BigDecimal("0.5"));
                        add(new BigDecimal("1.5"));
                        add(new BigDecimal(Double.MAX_VALUE));
                    }
                });
                put(param_PersonNum,new ArrayList<BigDecimal>(){
                    {
                        add(new BigDecimal(-1*Double.MAX_VALUE));
                        add(new BigDecimal("0.5"));
                        add(new BigDecimal("1.5"));
                        add(new BigDecimal("2.5"));
                        add(new BigDecimal("3.5"));
                        add(new BigDecimal(Double.MAX_VALUE));
                    }
                });
                put(param_RegCap,new ArrayList<BigDecimal>(){
                    {
                        add(new BigDecimal(-1*Double.MAX_VALUE));
                        add(new BigDecimal("4.25"));
                        add(new BigDecimal("50.5"));
                        add(new BigDecimal("99.0"));
                        add(new BigDecimal("626.5"));
                        add(new BigDecimal("4009.526"));
                        add(new BigDecimal(Double.MAX_VALUE));
                    }
                });
            }});
            put(COMPANY_CAPACITY,new HashMap<String,List<BigDecimal>>(){{
                put(param_FrInvEsDateMax,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(1.295));
                    add(new BigDecimal(3.455));
                    add(new BigDecimal(9.545));
                    add(new BigDecimal(18.305));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvEsDateMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.035));
                    add(new BigDecimal(1.985));
                    add(new BigDecimal(3.915));
                    add(new BigDecimal(9.235));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvFundedRatioAll,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(50.725));
                    add(new BigDecimal(149.905));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvFundedRatioAvg,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(41.225));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvFundedRatioMax,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(50.75));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvRegCapMax,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(17.5));
                    add(new BigDecimal(99.0));
                    add(new BigDecimal(2011.0));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvRegCapMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(2.565));
                    add(new BigDecimal(99.0));
                    add(new BigDecimal(184.5));
                    add(new BigDecimal(804.0));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvSubConAmtAll,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(29.85));
                    add(new BigDecimal(59.7));
                    add(new BigDecimal(195.3));
                    add(new BigDecimal(1879.6));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvSubConAmtMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(10.685));
                    add(new BigDecimal(38.63));
                    add(new BigDecimal(745.35));
                    add(new BigDecimal(1243.75));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_FrInvTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(1.5));
                    add(new BigDecimal(4.5));
                    add(new BigDecimal(6.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
            }});
            put(COMPANY_STABILITY,new HashMap<String,List<BigDecimal>>(){{
                put(param_Alter_1,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(1.5));
                    add(new BigDecimal(2.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_Alter_1_12,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(1.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_Alter_3,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_Alter_4,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_Alter_6_12,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_Alter_8,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_AlterDetalDaysMin,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(54.5));
                    add(new BigDecimal(119.5));
                    add(new BigDecimal(298.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_AlterTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(2.5));
                    add(new BigDecimal(6.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_SharesImpawnTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
            }});
            put(COMPANY_HONESTY,new HashMap<String,List<BigDecimal>>(){{
                put(param_JudgmentDoc_1,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_JudgmentDoc_12,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_JudgmentDocTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(4.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_CaseTimes,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
            }});
            put(COMPANY_PERSON,new HashMap<String,List<BigDecimal>>(){{
                put(param_AlsLstIdNbankInteday,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.0));
                    add(new BigDecimal(8.5));
                    add(new BigDecimal(36.5));
                    add(new BigDecimal(110.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_AlsLstIdBankInteday,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.0));
                    add(new BigDecimal(74.5));
                    add(new BigDecimal(178.5));
                    add(new BigDecimal(273.5));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_AlsM1IdNbankP2pAllnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.0));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_AlsM12IdRelAllnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.0));
                    add(new BigDecimal(2.0));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_AlsM3IdCaonOrgnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.0));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
                put(param_AlsM12IdNbankP2pAllnum,new ArrayList<BigDecimal>(){{
                    add(new BigDecimal(-1*Double.MAX_VALUE));
                    add(new BigDecimal(0.0));
                    add(new BigDecimal(Double.MAX_VALUE));
                }});
            }});
        }
    };


    public static final Map<String,Map<String,BigDecimal>> RADAR_EXTREME = new HashMap<String,Map<String,BigDecimal>>(){
        {
            put(COMPANY_QUALIFICATION,new HashMap<String,BigDecimal>(){
                {
                    put(MIN,new BigDecimal("-37.4768821778517"));
                    put(MAX,new BigDecimal("98.3928993709972"));
                    put(SPAN,new BigDecimal("135.869781548849"));
                }
            });
            put(COMPANY_CAPACITY,new HashMap<String,BigDecimal>(){
                {
                    put(MIN,new BigDecimal("-35.6712307385403"));
                    put(MAX,new BigDecimal("53.6766018347928"));
                    put(SPAN,new BigDecimal("89.3478325733331"));
                }
            });
            put(COMPANY_STABILITY,new HashMap<String,BigDecimal>(){
                {
                    put(MIN,new BigDecimal("-176.043191290644"));
                    put(MAX,new BigDecimal("112.749061667839"));
                    put(SPAN,new BigDecimal("288.792252958483"));
                }
            });
            put(COMPANY_HONESTY,new HashMap<String,BigDecimal>(){
                {
                    put(MIN,new BigDecimal("-91.5221958422445"));
                    put(MAX,new BigDecimal("67.3744370036963"));
                    put(SPAN,new BigDecimal("158.8966328459408"));
                }
            });
            put(COMPANY_PERSON,new HashMap<String,BigDecimal>(){
                {
                    put(MIN,new BigDecimal("-90.33160955605829"));
                    put(MAX,new BigDecimal("89.325952468190157"));
                    put(SPAN,new BigDecimal("179.657562024248447"));
                }
            });
        }
    };


}
