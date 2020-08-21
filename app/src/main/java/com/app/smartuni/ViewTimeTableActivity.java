package com.app.smartuni;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.TimeTableEvent;
import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewTimeTableActivity extends BaseActivity {

  @BindView(R.id.timetable) TimetableView mTimetableView;
  @BindView(R.id.text_current_week) TextView mCurrentWeek;
  ResultSet mResultSet;
  private ArrayList<TimeTableEvent> mEvents = new ArrayList<>();
  private Integer mResults = -1;
  private String mStartingDate;
  private String mEndingDate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_time_table);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//      getSupportActionBar().setTitle("Events");
    ButterKnife.bind(this);
    setWeekStart();
    getData();
  }

  private void getData() {
    new Thread(new Runnable() {
      @Override public void run() {
        try {
          mResultSet = getAllEvents();
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              hideProgress();
              try {
                while (mResultSet.next()) {
                  String mLecturer = mResultSet.getString("lecturer");
                  String mHall = mResultSet.getString("hall");
                  String mTime = mResultSet.getString("time");
                  String mCourse = mResultSet.getString("name");
                  String mDate = mResultSet.getString("date");
                  String mEndTime = mResultSet.getString("end_time");
                  mEvents.add(
                      new TimeTableEvent(null, mDate, mTime, mLecturer, mHall, mCourse, mEndTime));
                }
                setData();
              } catch (SQLException e) {
                hideProgress();
                showError(e.getMessage());
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

  public ResultSet getAllEvents() throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql =
        "SELECT * from time_table join course on course.idcourse = time_table.course_idcourse where time_table.date>='"
            + mStartingDate
            + "'and time_table.date <='"
            + mEndingDate
            + "'";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeQuery(sql);
  }

  private void setWeekStart() {
    mStartingDate = getWeekStartDate();
    mEndingDate = getWeekEndDate();

    mCurrentWeek.setText(mStartingDate + " - " + mEndingDate);
  }

  public String getWeekStartDate() {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Calendar calendar = Calendar.getInstance();
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
      calendar.add(Calendar.DATE, -1);
    }

    return dateFormat.format(calendar.getTime());
  }

  public String getWeekEndDate() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Calendar calendar = Calendar.getInstance();
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
      calendar.add(Calendar.DATE, 1);
    }
    calendar.add(Calendar.DATE, -3);
    return dateFormat.format(calendar.getTime());
  }

  private void setData() {
    ArrayList<Schedule> schedules = new ArrayList<Schedule>();

    for (int i = 0; i < mEvents.size(); i++) {
      Schedule schedule = new Schedule();
      schedule.setClassTitle(mEvents.get(i).getmCourse());
      schedule.setClassPlace(mEvents.get(i).getmHall()); // sets place
      schedule.setProfessorName(mEvents.get(i).getmLecturer());
      schedule.setDay(0);

      String mTime = mEvents.get(i).getmTime();
      String[] time = mTime.split(":");
      int hour = Integer.parseInt(time[0].trim());
      int min = Integer.parseInt(time[1].trim());

      schedule.setStartTime(new Time(hour, min));

      mTime = mEvents.get(i).getmEndTme();
      time = mTime.split(":");
      hour = Integer.parseInt(time[0].trim());
      min = Integer.parseInt(time[1].trim());

      schedule.setEndTime(new Time(hour, min)); // sets the end of class time (hour,minute)
      schedules.add(schedule);
    }

    mTimetableView.add(schedules);
  }
}
