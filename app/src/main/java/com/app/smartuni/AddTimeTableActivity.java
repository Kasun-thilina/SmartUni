package com.app.smartuni;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smartuni.adapters.TimeTableAdapter;
import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.TimeTableEvent;
import com.app.smartuni.utill.DateUtills;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTimeTableActivity extends BaseActivity {
  @BindView(R.id.edit_date) EditText mTimeTableDate;
  @BindView(R.id.button_add) Button mAddEvent;
  @BindView(R.id.recyclerView) RecyclerView mTimeTableEvents;
  @BindView(R.id.text_empty_recycle) TextView mEmptyText;
  @BindView(R.id.text_title) TextView mTitle;
  Context mContext;
  ResultSet mResults;
  private ArrayList<TimeTableEvent> mTimeTableEvent;
  private TimeTableAdapter mTimeTableAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_time_table);
    ButterKnife.bind(this);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle("Create Time Table");
    mTimeTableEvent = new ArrayList<>();
    mContext = this;
    mTimeTableAdapter = new TimeTableAdapter();
    mTimeTableEvents.setAdapter(mTimeTableAdapter);
    mTimeTableEvents.setHasFixedSize(true);
    mTimeTableEvents.setLayoutManager(new LinearLayoutManager(mContext));
    setRecycleView(true);
  }

  @OnClick({ R.id.edit_date, R.id.button_add })
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.edit_date:
        showDatePicker();
        break;
      case R.id.button_add:
        if (isDataSelected()) {
          Intent mIntent = new Intent(getApplicationContext(), CreateTimeTableEventActivity.class);
          mIntent.putExtra("SELCTION_DATE", mTimeTableDate.getText().toString());
          startActivity(mIntent);
        }
        break;
    }
  }

  private boolean isDataSelected() {
    mTimeTableDate.setError(null);
    if (mTimeTableDate.getText().toString().isEmpty()) {
      mTimeTableDate.setError("");
      showWarning("Please Select  Time Table Date !");
      return false;
    }

    return true;
  }

  @Override protected void onResume() {
    super.onResume();
    mTimeTableAdapter.clearItems();
    getTimeTableData(mTimeTableDate.getText().toString());
  }

  private void getTimeTableData(String mDateString) {
    if (mDateString != null) {
      mTimeTableAdapter.clearItems();
      showProgress();
      new Thread(new Runnable() {
        @Override public void run() {
          try {
            mResults = fetchData();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                hideProgress();
                try {
                  while (mResults.next()) {
                    System.out.println(mResults.getString("idtime_table") + "-----------");
                    String mId = mResults.getString("idtime_table");
                    String mDate = mResults.getString("date");
                    String mTime = mResults.getString("time");
                    String mLecturer = mResults.getString("lecturer");
                    String mHall = mResults.getString("hall");
                    String mCourse = mResults.getString("name");
                    String mEndTime = mResults.getString("end_time");
                    mTimeTableEvent.add(
                        new TimeTableEvent(mId, mDate, mTime, mLecturer, mHall, mCourse, mEndTime));
                  }
                  mTimeTableAdapter.setItems(mTimeTableEvent);

                  if (mTimeTableEvent.size() == 0) {
                    setRecycleView(false);
                  } else {
                    setRecycleView(true);
                  }
                } catch (Exception e) {
                  hideProgress();
                  showError(e.getMessage());
                  e.printStackTrace();
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

  private ResultSet fetchData() throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql =
        "select * from time_table join course on course.idcourse = time_table.course_idcourse where time_table.date='"
            + mTimeTableDate.getText().toString()
            + "'";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeQuery(sql);
  }

  private void showDatePicker() {
    final Calendar newCalendar = Calendar.getInstance();
    DatePickerDialog mStartTime =
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
          Calendar newDate = Calendar.getInstance();
          newDate.set(year, monthOfYear, dayOfMonth);
          mAddEvent.setEnabled(true);
          mTimeTableDate.setText(DateUtills.getCalenderFormatted(newDate, DateUtills.YYYY_MM_DD));
          getTimeTableData(mTimeTableDate.getText().toString());
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH));
    mStartTime.show();
  }

  private void setRecycleView(Boolean mValue) {
    if (mValue) {
      mTimeTableEvents.setVisibility(View.VISIBLE);
      mEmptyText.setVisibility(View.GONE);
    } else {
      mTimeTableEvents.setVisibility(View.GONE);
      mEmptyText.setVisibility(View.VISIBLE);
    }
  }
}
