package com.biz.credit.domain;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class User implements Serializable {

  private long userId;
  private String username;
  private String passwd;
  private String realname;
  private long institutionId;
  private String email;
  private String tel;
  private String imagePath;
  private java.sql.Timestamp lastLoginTime;
  private long isValid;
  private String apiCode;
  private long userType;
  private String description;
  private java.sql.Timestamp lastUpdateTime;
  private java.sql.Timestamp createTime;


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getPasswd() {
    return passwd;
  }

  public void setPasswd(String passwd) {
    this.passwd = passwd;
  }


  public String getRealname() {
    return realname;
  }

  public void setRealname(String realname) {
    this.realname = realname;
  }


  public long getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(long institutionId) {
    this.institutionId = institutionId;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


  public String getTel() {
    return tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }


  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }


  public java.sql.Timestamp getLastLoginTime() {
    return lastLoginTime;
  }

  public void setLastLoginTime(java.sql.Timestamp lastLoginTime) {
    this.lastLoginTime = lastLoginTime;
  }


  public long getIsValid() {
    return isValid;
  }

  public void setIsValid(long isValid) {
    this.isValid = isValid;
  }


  public String getApiCode() {
    return apiCode;
  }

  public void setApiCode(String apiCode) {
    this.apiCode = apiCode;
  }


  public long getUserType() {
    return userType;
  }

  public void setUserType(long userType) {
    this.userType = userType;
  }


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public java.sql.Timestamp getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(java.sql.Timestamp lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }


  public java.sql.Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(java.sql.Timestamp createTime) {
    this.createTime = createTime;
  }

}
