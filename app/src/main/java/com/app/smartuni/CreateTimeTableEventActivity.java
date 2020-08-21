package com.app.smartuni;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.Course;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateTimeTableEventActivity extends BaseActivity {
  @BindView(R.id.edit_date) EditText mTimeTableDate;
  @BindView(R.id.edit_time) EditText mEventTime;
  @BindView(R.id.edit_end_time) EditText mEventEndTime;
  @BindView(R.id.edit_lect_name) EditText mLecturer;
  @BindView(R.id.edit_course_code) EditText mCourseCode;
  @BindView(R.id.edit_hall) EditText mHall;
  private Integer mResults = -1;
  ResultSet mResultSet;
  private ArrayList<Course> mCourses = new ArrayList<>();
  private ArrayList<String> mCoursesNames = new ArrayList<>();
  private AlertDialog.Builder mCoursesAlert;
  private Integer mCourseIdPk;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_time_table_event);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Add Time Table Event");
    ButterKnife.bind(this);
    String mDate = getIntent().getStringExtra("SELCTION_DATE");
    if (mDate != null) {
      mTimeTableDate.setText(mDate);
    }
    getData();
  }

  @OnClick({
      R.id.edit_end_time, R.id.edit_time, R.id.button_create, R.id.button_cancel,
      R.id.edit_course_code
  })
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.edit_time:
        showStartTimePicker();
        break;
      case R.id.edit_end_time:
        showEndTimePicker();
        break;

      case R.id.button_create:
        processSave();
        break;

      case R.id.button_cancel:
        finish();
        break;

      case R.id.edit_course_code:
        mCoursesAlert.show();
        break;
    }
  }

  private void processSave() {
    if (isValidForm()) {
      processAddEvent();
    }
  }

  private boolean isValidForm() {
    clearValidations();
    if (mLecturer.getText().toString().isEmpty()) {
      showWarning("Lecturer Name Required!");
      mLecturer.setError("");
      return false;
    }

    if (mEventTime.getText().toString().isEmpty()) {
      showWarning("Event Time Required!");
      mEventTime.setError("");
      return false;
    }

    if (mCourseCode.getText().toString().isEmpty()) {
      showWarning("Course Code Required!");
      mCourseCode.setError("");
      return false;
    }

    if (mHall.getText().toString().isEmpty()) {
      showWarning("Hall Name Required!");
      mHall.setError("");
      return false;
    }
    return true;
  }

  private void clearValidations() {
    mLecturer.setError(null);
    mCourseCode.setError(null);
    mLecturer.setError(null);
    mCourseCode.setError(null);
    mHall.setError(null);
    mEventTime.setError(null);
  }

  private void showStartTimePicker() {
    Calendar mcurrentTime = Calendar.getInstance();
    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    int minute = mcurrentTime.get(Calendar.MINUTE);
    TimePickerDialog mTimePicker;
    mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        StringBuilder mTime = new StringBuilder();
        mTime.append(selectedHour);
        mTime.append(":");
        mTime.append(selectedMinute);
        mEventTime.setText(mTime.toString());
      }
    }, hour, minute, true);//Yes 24 hour time
    mTimePicker.setTitle("Select Time");
    mTimePicker.show();
  }

  private void showEndTimePicker() {
    Calendar mcurrentTime = Calendar.getInstance();
    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    int minute = mcurrentTime.get(Calendar.MINUTE);
    TimePickerDialog mTimePicker;
    mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        StringBuilder mTime = new StringBuilder();
        mTime.append(selectedHour);
        mTime.append(":");
        mTime.append(selectedMinute);
        mEventEndTime.setText(mTime.toString());
      }
    }, hour, minute, true);//Yes 24 hour time
    mTimePicker.setTitle("Select Time");
    mTimePicker.show();
  }

  private void processAddEvent() {
    if (isValidForm()) {
      String mDateString = mTimeTableDate.getText().toString().trim();
      String mTimeString = mEventTime.getText().toString().trim();
      String mEndTimeString = mEventEndTime.getText().toString().trim();
      String mLecturerString = mLecturer.getText().toString().trim();
      String mHallString = mHall.getText().toString().trim();

      showProgress();

      new Thread(new Runnable() {
        @Override public void run() {
          try {
            Connection connection = DbConnection.getConnect();
            Statement mStatement = connection.createStatement();
            String mquery =
                "INSERT INTO time_table (date,time,lecturer,hall,course_idcourse,end_time) VALUES ('"
                    +
                    mDateString
                    + "','"
                    +
                    mTimeString
                    + "','"
                    +
                    mLecturerString
                    + "','"
                    +
                    mHallString
                    + "','"
                    +
                    mCourseIdPk
                    + "','" +
                    mEndTimeString
                    + "')";
            mResults = mStatement.executeUpdate(mquery);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                hideProgress();
                if (mResults == 1) {
                  //showSuccess("Successful!");
                  finish();
                } else {
                  showError("Failed To Save The Event");
                }
              }
            });
          } catch (SQLException e) {
            hideProgress();
            showError(e.getMessage());
            e.printStackTrace();
          }
        }
      }).start();
    }
  }

  public ResultSet getCourses() throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql =
        "SELECT * from course";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeQuery(sql);
  }

  private void getData() {
    showProgress();
    new Thread(new Runnable() {
      @Override public void run() {
        try {
          mResultSet = getCourses();
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              hideProgress();
              try {
                while (mResultSet.next()) {
                  Integer mIdCourse = mResultSet.getInt("idcourse");
                  String mName = mResultSet.getString("name");
                  mCoursesNames.add(mName);
                  mCourses.add(
                      new Course(mIdCourse, mName));
                }
                setData();
              } catch (SQLException e) {
                e.printStackTrace();
              }
            }
          });
        } catch (Exception e) {
          hideProgress();
          showError(e.getMessage());
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void setData() {
    mCoursesAlert = new AlertDialog.Builder(this);
    mCoursesAlert.setTitle("Select Course");
    String[] types = mCoursesNames.toArray(new String[0]);
    mCoursesAlert.setItems(types, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        findSelection(types[which]);
        mCourseCode.setText(types[which]);
        showWarning(types[which]);
      }
    });
  }

  private void findSelection(String mSelection) {
    for (Course item : mCourses) {
      if (item.getmCourseName().equals(mSelection)) {
        mCourseIdPk = item.getmCourseId();
      }
    }
  }
}
