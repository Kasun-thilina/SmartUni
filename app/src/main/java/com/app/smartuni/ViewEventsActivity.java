package com.app.smartuni;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.app.smartuni.adapters.ViewEventsAdapter;
import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.Events;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ViewEventsActivity extends BaseActivity {

  @BindView(R.id.recycle_show_events) RecyclerView mRecycleView;
  @BindView(R.id.text_empty_recycle) TextView mEmptyRecycleView;
  ResultSet mResultSet;
  private ViewEventsAdapter mViewEventsAdapter;
  private ArrayList<Events> mEvents = new ArrayList<>();
  private Integer mResults = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_events);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ButterKnife.bind(this);
    mViewEventsAdapter = new ViewEventsAdapter();
    mRecycleView.setAdapter(mViewEventsAdapter);
    mRecycleView.setHasFixedSize(true);
    mRecycleView.setLayoutManager(new LinearLayoutManager(this));
    getData();
  }

  private void getData() {
    showProgress();
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
                  String mTitle = mResultSet.getString("title");
                  String mDescription = mResultSet.getString("description");
                  String mTime = mResultSet.getString("time");
                  String mLocation = mResultSet.getString("location");
                  String mDate = mResultSet.getString("date");
                  mEvents.add(new Events(null, mTitle, mDate, mDescription, mTime, mLocation));
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

  private void setData() {
    if (mEvents.size() > 0) {
      mEmptyRecycleView.setVisibility(View.GONE);
      mRecycleView.setVisibility(View.VISIBLE);
      mViewEventsAdapter.setItems(mEvents);
    } else {
      mEmptyRecycleView.setVisibility(View.VISIBLE);
      mRecycleView.setVisibility(View.GONE);
    }
  }

  public ResultSet getAllEvents() throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql =
        "SELECT * from event";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeQuery(sql);
  }
}
