package com.app.smartuni;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.smartuni.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateUserActivity extends BaseActivity {

  @BindView(R.id.et_fname) EditText mFName;
  @BindView(R.id.et_lname) EditText mLName;
  @BindView(R.id.et_email) EditText mEmail;
  @BindView(R.id.et_address) EditText mAddress;
  @BindView(R.id.et_phone) EditText mPhone;
  @BindView(R.id.image_user) ImageView mUserImage;
  @BindView(R.id.et_user_name) EditText mUserName;
  @BindView(R.id.et_password) EditText mPassword;
  @BindView(R.id.et_repassword) EditText mCPassword;
  Integer mResults;
  String mImageUserString = "";

  public static boolean isValidEmail(CharSequence target) {
    return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_user);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Add new Student Profile");

    ButterKnife.bind(this);
  }

  @OnClick({ R.id.btn_register, R.id.image_user })
  public void submit(View view) {
    switch (view.getId()) {
      case R.id.btn_register:
        processSave();
        break;
      case R.id.image_user:
        ImagePicker.create(this)
            .returnMode(
                ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
            .folderMode(true) // folder mode (false by default)
            .toolbarFolderTitle("Select") // folder selection title
            .toolbarImageTitle("Tap to select") // image selection title
            .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
            .includeVideo(false) // Show video on image picker
            .single() // single mode
            .showCamera(true) // show camera or not (true by default)
            .imageDirectory(
                "Camera") // directory name for captured image  ("Camera" folder by default)
            .enableLog(false) // disabling log
            .start(); // start image picker activity with request code
        break;
    }
  }

  private void processSave() {
    if (isValidForm()) {
      saveUser();
    }
  }

  private void saveUser() {
    if (isValidForm()) {
      String mFNameString = mFName.getText().toString().trim();
      String mLNameString = mLName.getText().toString().trim();
      String mEmailString = mEmail.getText().toString().trim();
      String mAddressString = mAddress.getText().toString().trim();
      String mPhoneString = mPhone.getText().toString().trim();
      String mUserNameString = mUserName.getText().toString().trim();
      String mPasswordString = mPassword.getText().toString().trim();
      showProgress();

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Connection connection = DbConnection.getConnect();
            Statement mStatement = connection.createStatement();
            String mquery =
                "INSERT INTO user (user_name,first_name,last_name,address,phone,image,email,user_role,user_password) VALUES ('"
                    +
                    mUserNameString
                    + "','"
                    +
                    mFNameString
                    + "','"
                    +
                    mLNameString
                    + "','"
                    +
                    mAddressString
                    + "','"
                    +
                    mPhoneString
                    + "','"
                    +
                    mImageUserString
                    + "','"
                    +
                    mEmailString
                    + "','"
                    +
                    0
                    + "','"
                    +
                    mPasswordString
                    + "')";
            System.out.println("----" + mquery);
            mResults = mStatement.executeUpdate(mquery);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override
              public void run() {
                hideProgress();
                if (mResults == 1) {
                  showSuccess("Successful! UserID:8");
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

  public String getBase64FromFile(String path) {
    Bitmap bmp = null;
    ByteArrayOutputStream baos = null;
    byte[] baat = null;
    String encodeString = null;
    try {
      bmp = BitmapFactory.decodeFile(path);
      baos = new ByteArrayOutputStream();
      bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
      baat = baos.toByteArray();
      encodeString = Base64.encodeToString(baat, Base64.DEFAULT);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return encodeString;
  }

  private boolean isValidForm() {
    if (mFName.getText().toString().isEmpty()) {
      showWarning("First Name Required!");
      mFName.setError("");
      return false;
    }

    if (mLName.getText().toString().isEmpty()) {
      showWarning("Last Name Required!");
      mLName.setError("");
      return false;
    }

    if (mPhone.getText().toString().isEmpty()) {
      showWarning("Phone Number Required!");
      return false;
    }

    if (mAddress.getText().toString().isEmpty()) {
      showWarning("Address Required!");
      return false;
    }

    if (mEmail.getText().toString().isEmpty()) {
      showWarning("Email Required!");
      mEmail.setError("");
      return false;
    }

    if (!isValidEmail(mEmail.getText().toString())) {
      showWarning("Invalid Email!");
      mEmail.setError("");
      return false;
    }

    if (mUserName.getText().toString().isEmpty()) {
      showWarning("User Name Required!");
      mUserName.setError("");
      return false;
    }

    if (mPassword.getText().toString().isEmpty()) {
      showWarning("Password Required!");
      mPassword.setError("");
      return false;
    }

    if (!mCPassword.getText().toString().equals(mPassword.getText().toString())) {
      showWarning("Password Not Matched");
      mCPassword.setError("");
      return false;
    }

    if (mImageUserString.isEmpty()) {
      showWarning("User Image Required!");
      return false;
    }

    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      // or get a single image only
      Image image = ImagePicker.getFirstImageOrNull(data);
      Glide.with(this)
          .load(image.getPath())
          .into(mUserImage);
      mImageUserString = getBase64FromFile(image.getPath());
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
