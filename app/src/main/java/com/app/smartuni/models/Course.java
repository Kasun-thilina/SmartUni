package com.app.smartuni.models;

public class Course {

  private Integer mCourseId;
  private String mCourseName;

  public Course(Integer mCourseId, String mCourseName) {
    this.mCourseId = mCourseId;
    this.mCourseName = mCourseName;
  }

  public Integer getmCourseId() {
    return mCourseId;
  }

  public void setmCourseId(Integer mCourseId) {
    this.mCourseId = mCourseId;
  }

  public String getmCourseName() {
    return mCourseName;
  }

  public void setmCourseName(String mCourseName) {
    this.mCourseName = mCourseName;
  }
}
