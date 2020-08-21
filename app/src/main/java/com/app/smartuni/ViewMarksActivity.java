package com.app.smartuni;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.app.smartuni.adapters.AddMarksAdapter;
import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.AddMarks;
import com.app.smartuni.models.User;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ViewMarksActivity extends BaseActivity {

  @BindView(R.id.recycle_show_marks) RecyclerView mRecycleView;
  @BindView(R.id.text_empty_recycle) TextView mEmptyRecycleView;
  private AddMarksAdapter mAddMarksAdapter;
  ResultSet mResultSet;
  private ArrayList<AddMarks> mMarks = new ArrayList<>();
  private Integer mResults = -1;
  private User mUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_marks);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ButterKnife.bind(this);
    mAddMarksAdapter = new AddMarksAdapter();
    mAddMarksAdapter.setIsShowMarks(true);
    mRecycleView.setAdapter(mAddMarksAdapter);
    mRecycleView.setHasFixedSize(true);
    mRecycleView.setLayoutManager(new LinearLayoutManager(this));
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String mUserString = prefs.getString("user", null);
    if (mUserString != null) {
      mUser = new Gson().fromJson(mUserString, User.class);
      getData(mUser.getmId());
    }
  }

  private void getData(String mUserId) {
    showProgress();
    new Thread(new Runnable() {
      @Override public void run() {
        try {
          mResultSet = getUserMarks(mUserId);
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              hideProgress();

              try {
                while (mResultSet.next()) {
                  String mGrade = mResultSet.getString("marks");
                  String mCourseName = mResultSet.getString("course_name");
                  mMarks.add(new AddMarks(null, null, mGrade, mCourseName));
                }
                setData();
              } catch (SQLException e) {
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

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  private void setData() {
    if (mMarks.size() > 0) {
      mEmptyRecycleView.setVisibility(View.GONE);
      mRecycleView.setVisibility(View.VISIBLE);
      mAddMarksAdapter.setItems(mMarks);
    } else {
      mEmptyRecycleView.setVisibility(View.VISIBLE);
      mRecycleView.setVisibility(View.GONE);
    }
  }

  public ResultSet getUserMarks(String mUserId) throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql =
        "SELECT marks.marks as marks ,course.name as course_name from marks join course on course.idcourse = marks.course_idcourse where  marks.user_iduser='"
            + mUserId
            + "'";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeQuery(sql);
  }
}
