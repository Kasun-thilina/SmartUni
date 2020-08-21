package com.app.smartuni.models;

public class User {
  private String mId;
  private String mFirstName;
  private String mLastName;
  private String mAddress;
  private String mPhone;
  private String mUserName;
  private String mEmail;
  private String mUserImage;
  private Boolean mIsAdmin;

  public User(String mId, String mFirstName, String mLastName, String mAddress,
      String mPhone, String mUserName, String mEmail, String mUserImage, Boolean mIsAdmin) {
    this.mId = mId;
    this.mFirstName = mFirstName;
    this.mLastName = mLastName;
    this.mAddress = mAddress;
    this.mPhone = mPhone;
    this.mUserName = mUserName;
    this.mEmail = mEmail;
    this.mUserImage = mUserImage;
    this.mIsAdmin = mIsAdmin;
  }

  public String getmId() {
    return mId;
  }

  public void setmId(String mId) {
    this.mId = mId;
  }

  public String getmFirstName() {
    return mFirstName;
  }

  public void setmFirstName(String mFirstName) {
    this.mFirstName = mFirstName;
  }

  public String getmLastName() {
    return mLastName;
  }

  public void setmLastName(String mLastName) {
    this.mLastName = mLastName;
  }

  public String getmAddress() {
    return mAddress;
  }

  public void setmAddress(String mAddress) {
    this.mAddress = mAddress;
  }

  public String getmPhone() {
    return mPhone;
  }

  public void setmPhone(String mPhone) {
    this.mPhone = mPhone;
  }

  public String getmUserName() {
    return mUserName;
  }

  public void setmUserName(String mUserName) {
    this.mUserName = mUserName;
  }

  public String getmEmail() {
    return mEmail;
  }

  public void setmEmail(String mEmail) {
    this.mEmail = mEmail;
  }

  public String getmUserImage() {
    return mUserImage;
  }

  public void setmUserImage(String mUserImage) {
    this.mUserImage = mUserImage;
  }

  public Boolean getmIsAdmin() {
    return mIsAdmin;
  }

  public void setmIsAdmin(Boolean mIsAdmin) {
    this.mIsAdmin = mIsAdmin;
  }
}
