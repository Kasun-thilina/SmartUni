package com.app.smartuni.models;

public class TimeTableEvent {
  private String mId;
  private String mDate;
  private String mTime;
  private String mLecturer;
  private String mHall;
  private String mCourse;
  private String mEndTme;

  public TimeTableEvent(String mId, String mDate, String mTime, String mLecturer,
      String mHall, String mCourse, String mEndTme) {
    this.mId = mId;
    this.mDate = mDate;
    this.mTime = mTime;
    this.mLecturer = mLecturer;
    this.mHall = mHall;
    this.mCourse = mCourse;
    this.mEndTme = mEndTme;
  }

  public String getmId() {
    return mId;
  }

  public void setmId(String mId) {
    this.mId = mId;
  }

  public String getmDate() {
    return mDate;
  }

  public void setmDate(String mDate) {
    this.mDate = mDate;
  }

  public String getmTime() {
    return mTime;
  }

  public void setmTime(String mTime) {
    this.mTime = mTime;
  }

  public String getmLecturer() {
    return mLecturer;
  }

  public void setmLecturer(String mLecturer) {
    this.mLecturer = mLecturer;
  }

  public String getmHall() {
    return mHall;
  }

  public void setmHall(String mHall) {
    this.mHall = mHall;
  }

  public String getmCourse() {
    return mCourse;
  }

  public void setmCourse(String mCourse) {
    this.mCourse = mCourse;
  }

  public String getmEndTme() {
    return mEndTme;
  }

  public void setmEndTme(String mEndTme) {
    this.mEndTme = mEndTme;
  }
}
