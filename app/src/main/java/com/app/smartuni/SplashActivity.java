package com.app.smartuni;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.app.smartuni.base.BaseActivity;
import java.sql.Connection;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {
  Context mContext;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    mContext = this;

    new Thread(new Runnable() {
      @Override public void run() {
        Connection mConnection = DbConnection.getConnect();
      }
    }).start();

    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String mUser = prefs.getString("user", "");
        Boolean remember_me = prefs.getBoolean("remember_me", false);

        if (!mUser.isEmpty()) {
          if (remember_me) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
          } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
          }
        } else {
          startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        finish();
      }
    }, 1000);
  }
}
