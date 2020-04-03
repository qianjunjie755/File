package com.biz.credit.domain;

import java.io.Serializable;

public class Message implements Serializable {

    private String num;//统一社会编码
    private String key_no;//公司名称

    public Message(String num, String key_no) {
        this.num = num;
        this.key_no = key_no;
    }
    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getKey_no() {
        return key_no;
    }

    public void setKey_no(String key_no) {
        this.key_no = key_no;
    }
}
