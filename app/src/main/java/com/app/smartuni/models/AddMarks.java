package com.app.smartuni.models;

public class AddMarks {
  private String mCourseId;
  private String mCourseName;
  private String mStudentId;
  private String mMarks;

  public AddMarks(String mCourseId, String mStudentId, String mMarks, String mCourseName) {
    this.mCourseId = mCourseId;
    this.mStudentId = mStudentId;
    this.mMarks = mMarks;
    this.mCourseName = mCourseName;
  }

  public String getmCourseId() {
    return mCourseId;
  }

  public void setmCourseId(String mCourseId) {
    this.mCourseId = mCourseId;
  }

  public String getmStudentId() {
    return mStudentId;
  }

  public void setmStudentId(String mStudentId) {
    this.mStudentId = mStudentId;
  }

  public String getmMarks() {
    return mMarks;
  }

  public void setmMarks(String mMarks) {
    this.mMarks = mMarks;
  }

  public String getmCourseName() {
    return mCourseName;
  }

  public void setmCourseName(String mCourseName) {
    this.mCourseName = mCourseName;
  }
}
