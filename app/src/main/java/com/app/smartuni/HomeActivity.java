package com.app.smartuni;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.User;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class HomeActivity extends BaseActivity {

  TextView mUserName;
  Boolean is_admin = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    is_admin = prefs.getBoolean("is_admin", false);
    if (is_admin) {
      setContentView(R.layout.activity_admin_home);
    } else {
      setContentView(R.layout.activity_student_home);
    }
    mUserName = findViewById(R.id.home_user_full_name);
    setUserName();
    ButterKnife.bind(this);
  }

  @Optional
  @OnClick({
      R.id.home_addto_events_card, R.id.home_logout_card, R.id.home_add_event_card,
      R.id.home_time_table_card, R.id.home_generate_cv_card, R.id.home_profile_card,
      R.id.home_marks_add_card, R.id.home_marks_card, R.id.home_create_user_card,
          R.id.home_view_events, R.id.home_mark_attendance_card
  })
  public void submit(View view) {
    switch (view.getId()) {
      case R.id.home_view_events:
        showEvents();
        break;
      case R.id.home_create_user_card:
        startActivity(new Intent(getApplicationContext(), CreateUserActivity.class));
        break;
      case R.id.home_add_event_card:
        addEvents();
        break;
      case R.id.home_time_table_card:
        addTimeTable();
        break;
      case R.id.home_generate_cv_card:
        showCV();
        break;
      case R.id.home_profile_card:
        showProfile();
        break;
      case R.id.home_addto_events_card:
        startActivity(new Intent(getApplicationContext(), EventActivity.class));
        break;
      case R.id.home_marks_add_card:
        startActivity(new Intent(getApplicationContext(), AddMarksActivity.class));
        break;
      case R.id.home_marks_card:
        startActivity(new Intent(getApplicationContext(), ViewMarksActivity.class));
        break;
      case R.id.home_mark_attendance_card:
        startActivity(new Intent(getApplicationContext(), MarkAttendanceActivity.class));
        break;
      case R.id.home_logout_card: {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor mEditor = prefs.edit();
        mEditor.clear();
        mEditor.apply();
        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
      }
    }
  }

  private void setUserName() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String mUserString = prefs.getString("user", null);
    if (mUserString != null) {
      User mUser = new Gson().fromJson(mUserString, User.class);
      mUserName.setText(mUser.getmFirstName() + " " + mUser.getmLastName());
    }
  }

  private void showProfile() {
    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
  }

  private void addTimeTable() {
    if (is_admin) {
      startActivity(new Intent(getApplicationContext(), AddTimeTableActivity.class));
    } else {
      startActivity(new Intent(getApplicationContext(), ViewTimeTableActivity.class));
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  private void addEvents() {
    startActivity(new Intent(getApplicationContext(), AddEventActivity.class));
  }

  private void showEvents() {
    startActivity(new Intent(getApplicationContext(), ViewEventsActivity.class));
  }

  private void showCV() {
    startActivity(new Intent(getApplicationContext(), MyCvActivity.class));
  }
}
