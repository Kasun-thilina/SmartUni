package com.app.smartuni.models;

public class Events {
  private String mEventId;
  private String mEventTitle;
  private String mEventDate;
  private String mEventDescription;
  private String mEventTime;
  private String mLocation;

  public Events(String mEventId, String mEventTitle, String mEventDate,
      String mEventDescription, String mEventTime, String mLocation) {
    this.mEventId = mEventId;
    this.mEventTitle = mEventTitle;
    this.mEventDate = mEventDate;
    this.mEventDescription = mEventDescription;
    this.mEventTime = mEventTime;
    this.mLocation = mLocation;
  }

  public String getmEventId() {
    return mEventId;
  }

  public void setmEventId(String mEventId) {
    this.mEventId = mEventId;
  }

  public String getmEventTitle() {
    return mEventTitle;
  }

  public void setmEventTitle(String mEventTitle) {
    this.mEventTitle = mEventTitle;
  }

  public String getmEventDate() {
    return mEventDate;
  }

  public void setmEventDate(String mEventDate) {
    this.mEventDate = mEventDate;
  }

  public String getmEventDescription() {
    return mEventDescription;
  }

  public void setmEventDescription(String mEventDescription) {
    this.mEventDescription = mEventDescription;
  }

  public String getmEventTime() {
    return mEventTime;
  }

  public void setmEventTime(String mEventTime) {
    this.mEventTime = mEventTime;
  }

  public String getmLocation() {
    return mLocation;
  }

  public void setmLocation(String mLocation) {
    this.mLocation = mLocation;
  }
}
