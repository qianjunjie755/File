package com.biz.credit.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.utils.Constants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class InputFileDetail implements Serializable {

    private Integer inputFileDetailId;
    private Integer inputFileId;
    private Integer taskId;
    private String pdfFilePath;
    private String pdfFileName;
    private Integer status;
    private String keyNo = StringUtils.EMPTY;
    private String creditCode = StringUtils.EMPTY;
    private String idNumber = StringUtils.EMPTY;
    private String cellPhone = StringUtils.EMPTY;
    private String name = StringUtils.EMPTY;
    private String lastUpdateTime;
    private String createTime;
    private String bankId = StringUtils.EMPTY;
    private String homeAddr = StringUtils.EMPTY;
    private String bizAddr = StringUtils.EMPTY;
    private String date;
    private Integer month;
    private Integer year;
    private String appId;
    private String relatedPeople;
    private List<Param> params;
    private List<InputFileDetailContact> contacts;

    public void setKeyNo(String keyNo) {
        if (null != keyNo)
            this.keyNo = keyNo;
    }

    public void setCreditCode(String creditCode) {
        if (null != creditCode)
            this.creditCode = creditCode;
    }

    public void setIdNumber(String idNumber) {
        if (null != idNumber)
            this.idNumber = idNumber;
    }

    public void setCellPhone(String cellPhone) {
        if (null != cellPhone)
            this.cellPhone = cellPhone;
    }

    public void setName(String name) {
        if (null != name)
            this.name = name;
    }

    public void setBankId(String bankId) {
        if (null != bankId)
            this.bankId = bankId;
    }

    public void setHomeAddr(String homeAddr) {
        if (null != homeAddr)
            this.homeAddr = homeAddr;
    }

    public void setBizAddr(String bizAddr) {
        if (null != bizAddr)
            this.bizAddr = bizAddr;
    }

    public void addParam(Param param) {
        if (param == null) {
            return;
        }
        if (this.params == null) {
            this.params = new ArrayList<>();
        }
        this.params.add(param);
    }

    public void addInputContact(InputFileDetailContact contact) {
        if (contact == null) {
            return;
        }
        if (this.contacts == null) {
            this.contacts = new ArrayList<>();
        }
        this.contacts.add(contact);
    }

    public List<InputFileDetailContact> getContacts() {
        if (this.contacts != null && this.inputFileDetailId != null) {
            for (InputFileDetailContact contact : this.contacts) {
                contact.setInputFileDetailId(this.inputFileDetailId.longValue());
            }
        }
        return contacts;
    }

    public void dealHeaderJson(List<JSONObject> headerGroupList) {
        int j = 0;
        boolean hasRelatedPerson = Boolean.FALSE.booleanValue();
        for (int i = 0; i < headerGroupList.size(); i++) {
            JSONObject headerGroup = headerGroupList.get(i);
            headerGroup.put("index", i);
            JSONArray items = headerGroup.getJSONArray("items");
            if (!StringUtils.equals(Constants.DetailRelatedPersonTitle, headerGroup.getString("title"))) {
                items.forEach(itemObj -> {
                    JSONObject item = (JSONObject) itemObj;
                    if (StringUtils.equals(item.getString("key"), Constants.companyName)) {
                        item.put("value", StringUtils.isEmpty(getKeyNo()) ? StringUtils.EMPTY : getKeyNo());
                    } else if (StringUtils.equals(item.getString("key"), Constants.legalPerson)) {
                        item.put("value", StringUtils.isEmpty(getName()) ? StringUtils.EMPTY : getName());
                    } else if (StringUtils.equals(item.getString("key"), Constants.idNumber)) {
                        item.put("value", StringUtils.isEmpty(getIdNumber()) ? StringUtils.EMPTY : getIdNumber());
                    } else if (StringUtils.equals(item.getString("key"), Constants.cellPhone)) {
                        item.put("value", StringUtils.isEmpty(getCellPhone()) ? StringUtils.EMPTY : getCellPhone());
                    } else if (StringUtils.equals(item.getString("key"), Constants.creditCode)) {
                        item.put("value", StringUtils.isEmpty(getCreditCode()) ? StringUtils.EMPTY : getCreditCode());
                    } else if (StringUtils.equals(item.getString("key"), Constants.bankId)) {
                        item.put("value", StringUtils.isEmpty(getBankId()) ? StringUtils.EMPTY : getBankId());
                    } else if (StringUtils.equals(item.getString("key"), Constants.bizAddress)) {
                        item.put("value", StringUtils.isEmpty(getBizAddr()) ? StringUtils.EMPTY : getBizAddr());
                    } else if (StringUtils.equals(item.getString("key"), Constants.homeAddress)) {
                        item.put("value", StringUtils.isEmpty(getHomeAddr()) ? StringUtils.EMPTY : getHomeAddr());
                    }
                });
            } else if (StringUtils.isNotEmpty(relatedPeople)) {
                hasRelatedPerson = true;
                j = i;
            }
            headerGroup.put("title", headerGroup.getString("title").replaceAll("填写", StringUtils.EMPTY));
        }
        if (hasRelatedPerson) {
            headerGroupList.remove(j);
            JSONArray rps = JSONArray.parseArray(relatedPeople);
            for (int i = 0; i < rps.size(); i++, j++) {
                JSONArray personItems = rps.getJSONArray(i);
                JSONObject person = new JSONObject();
                person.put("addable", 1);
                person.put("title", Constants.DetailRelatedPersonTitle.replaceAll("填写", StringUtils.EMPTY));
                person.put("items", personItems);
                person.put("index", j);
                headerGroupList.add(person);
            }
        }
    }

    public JSONObject makeQueryJsonData() {
        JSONObject queryJsonData = new JSONObject();
        if (StringUtils.isNotEmpty(getKeyNo())) {
            Constants.ParamNameMap.get("keyNo").forEach(name -> {
                queryJsonData.put(name, getKeyNo());
            });
        }
        if (StringUtils.isNotEmpty(getName())) {
            Constants.ParamNameMap.get("name").forEach(name -> {
                queryJsonData.put(name, getName());
            });
        }
        if (StringUtils.isNotEmpty(getIdNumber())) {
            Constants.ParamNameMap.get("idNumber").forEach(name -> {
                queryJsonData.put(name, getIdNumber());
            });
        }
        if (StringUtils.isNotEmpty(getCellPhone())) {
            Constants.ParamNameMap.get("cellPhone").forEach(name -> {
                queryJsonData.put(name, getCellPhone());
            });
        }
        if (StringUtils.isNotEmpty(getCreditCode())) {
            Constants.ParamNameMap.get("creditCode").forEach(name -> {
                queryJsonData.put(name, getCreditCode());
            });
        }
        if (StringUtils.isNotEmpty(getHomeAddr())) {
            Constants.ParamNameMap.get("homeAddr").forEach(name -> {
                queryJsonData.put(name, getHomeAddr());
            });
        }
        if (StringUtils.isNotEmpty(getBizAddr())) {
            Constants.ParamNameMap.get("bizAddr").forEach(name -> {
                queryJsonData.put(name, getBizAddr());
            });
        }
        return queryJsonData;
    }

    public JSONObject makeResultJsonData() {
        JSONObject resultJsonData = new JSONObject();
        if (StringUtils.isNotEmpty(getBankId())) {
            Constants.ParamNameMap.get("bankId").forEach(name -> {
                resultJsonData.put(name, getBankId());
            });
        }
        if (StringUtils.isNotEmpty(getIdNumber())) {
            Constants.ParamNameMap.get("idNumber").forEach(name -> {
                resultJsonData.put(name, getIdNumber());
            });
        }
        if (StringUtils.isNotEmpty(getName())) {
            Constants.ParamNameMap.get("name").forEach(name -> {
                resultJsonData.put(name, getName());
            });
        }
        return resultJsonData;
    }


    public void makeDetailJsonNew(String detailJsonNew) {
        JSONArray detailJsonArray = JSONObject.parseArray(detailJsonNew);
        JSONArray relatedPersonList = new JSONArray();
        detailJsonArray.forEach(detailJson -> {
            JSONObject group = (JSONObject) detailJson;
            String title = group.getString("title");
            JSONArray items = group.getJSONArray("items");
            if (StringUtils.equals(Constants.DetailCompanyGroupTitle, title) || StringUtils.equals(Constants.DetailLegalPersonTitle, title) || StringUtils.equals(Constants.DetailApplyPersonTitle, title)) {
                items.forEach(item -> {
                    JSONObject itemJson = (JSONObject) item;
                    String value = StringUtils.isEmpty(itemJson.getString("value")) ? StringUtils.EMPTY : itemJson.getString("value");
                    String key = itemJson.getString("key");
                    if (Constants.companyName.equals(key)) {
                        setKeyNo(value);
                    } else if (Constants.legalPerson.equals(key) || Constants.applyLegalPerson.equals(key)) {
                        setName(value);
                    } else if (Constants.idNumber.equals(key) || Constants.applyIdNumber.equals(key)) {
                        setIdNumber(value);
                    } else if (Constants.cellPhone.equals(key) || Constants.applyCellPhone.equals(key)) {
                        setCellPhone(value);
                    } else if (Constants.creditCode.equals(key)) {
                        setCreditCode(value);
                    } else if (Constants.bankId.equals(key)) {
                        setBankId(value);
                    } else if (Constants.homeAddress.equals(key) || Constants.applyHomeAddress.equals(key)) {
                        setHomeAddr(value);
                    } else if (Constants.bizAddress.equals(key) || Constants.applyBizAddress.equals(key)) {
                        setBizAddr(value);
                    }
                });
            } else {

                items.forEach(item -> {
                    JSONObject itemJson = (JSONObject) item;
                    String value = StringUtils.isEmpty(itemJson.getString("value")) ? StringUtils.EMPTY : itemJson.getString("value");
                    itemJson.put("value", value);
                });
                relatedPersonList.add(items);
            }

        });
        if (relatedPersonList.size() > 0) {
            this.relatedPeople = relatedPersonList.toJSONString();
        }
    }

    public static void main(String[] args) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jo1 = new JSONObject();
        jo1.put("key", 121);
        jo1.put("value", "233");
        jsonArray.add(jo1);
/*    JSONObject jo2 = new JSONObject();
    jo2.put("key",1);
    jo2.put("value","2333123");
    jsonArray.add(jo2);*/
        jsonArray.forEach(jo -> {
            JSONObject item = (JSONObject) jo;
            item.put("key", 133);
        });
        System.out.println(jsonArray.getJSONObject(0).toJSONString());
    }
}
