package com.app.smartuni;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.User;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

  @BindView(R.id.button_login) Button mLogin;
  @BindView(R.id.edit_user_name) EditText mUserName;
  @BindView(R.id.edit_password) EditText mPassword;
  @BindView(R.id.switch_remember_me) Switch remember_me;
  //@BindView(R.id.text_forgot_password) TextView mForgotPassword;
  Context mContext;
  Editor mEdit;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
    mContext = this;
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    //mUserName.setText("test");
    //mUserName.setText("admin");
    //mPassword.setText("123");
    mEdit = prefs.edit();
    remember_me.setOnCheckedChangeListener(
        (buttonView, isChecked) -> mEdit.putBoolean("remember_me", isChecked));
    // startActivity(new Intent(getApplicationContext(), HomeActivity.class));

  }

  @OnClick({ R.id.button_login, R.id.text_forgot_password })
  public void submit(View view) {
    switch (view.getId()) {
      case R.id.button_login:
        processLogin();
        break;
      case R.id.text_forgot_password:
        showWarning("Please Contact System Administrator!");
        break;
    }
  }

  public void processLogin() {
    if (isValidForm()) {
      String mUserNameString = mUserName.getText().toString().trim();
      String mPasswordString = mPassword.getText().toString().trim();
      showProgress();
      new Thread(new Runnable() {
        @Override public void run() {
          try {
            ResultSet mResults = getUser(mUserNameString, mPasswordString);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                hideProgress();
              }
            });

            if (mResults.next()) {
              String first_name = mResults.getString("first_name");
              String userId = mResults.getString("iduser");
              String last_name = mResults.getString("last_name");
              String phone = mResults.getString("phone");
              String address = mResults.getString("address");
              String userName = mResults.getString("user_name");
              String email = mResults.getString("email");
              String mUserImage = mResults.getString("image");


              Integer userRole = mResults.getInt("user_role");
              Boolean isAdmin = false;
              if (userRole != 0) {
                isAdmin = true;
              }

              mEdit.putBoolean("is_admin", isAdmin);

              User mUser =
                  new User(userId, first_name, last_name, address, phone, userName, email,
                      mUserImage,
                      isAdmin);

              mEdit.putString("user", new Gson().toJson(mUser));
              mEdit.apply();
              mResults.close();
              startActivity(new Intent(getApplicationContext(), HomeActivity.class));
              finish();
            } else {
              new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                  hideProgress();
                  showError("Invalid Login Credentials!");
                }
              });
            }
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
  }

  public boolean isValidForm() {
    if (mUserName.getText().toString().isEmpty()) {
      showWarning("User Name Required!");
      return false;
    }

    if (mPassword.getText().toString().isEmpty()) {
      showWarning("Password Required!");
      return false;
    }

    return true;
  }

  public ResultSet getUser(String mUserName, String mPassword) throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql = "SELECT * from user where user_name='"
        + mUserName
        + "'and user_password='"
        + mPassword
        + "'";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeQuery(sql);
  }
}

