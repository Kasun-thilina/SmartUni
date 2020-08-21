package com.app.smartuni;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.models.User;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfileActivity extends BaseActivity {

  @BindView(R.id.text_edit_about_me)
  TextView mEditAboutMe;

  @BindView(R.id.text_user_full_name)
  TextView mFullName;

  @BindView(R.id.text_user_name)
  TextView mUserName;

  @BindView(R.id.text_edit_contact)
  TextView mEditContact;

  @BindView(R.id.edit_phone)
  EditText mPhone;

  @BindView(R.id.edit_email)
  EditText mEmail;

  @BindView(R.id.edit_address)
  EditText mAddress;

  @BindView(R.id.image_user)
  ImageView mUserImage;

  private User mUser;
  private Integer mResults = -1;
  private Boolean mIsEditable = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    ButterKnife.bind(this);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String mUserString = prefs.getString("user", null);
    if (mUserString != null) {
      mUser = new Gson().fromJson(mUserString, User.class);
      setUserData();
    }
  }

  @OnClick({ R.id.text_edit_contact, R.id.image_user, R.id.image_up })
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.text_edit_contact:
        if (mEditContact.getText().toString().equals("Edit")) {
          mEditContact.setText("Save");
          changeEditability(true);
        } else if ((mEditContact.getText().toString().equals("Save"))) {
          if (isValidForm()) {
            showProgress();
            saveUser();
            changeEditability(false);
            mEditContact.setText("Edit");
          }
        }
        break;
      case R.id.image_up:
        onBackPressed();
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

  private boolean isValidForm() {
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
      return false;
    }
    return true;
  }

  private void changeEditability(Boolean changeTo) {
    mPhone.setEnabled(changeTo);
    mEmail.setEnabled(changeTo);
    mAddress.setEnabled(changeTo);
  }

  public void setUserData() {
    mFullName.setText(mUser.getmFirstName() + " " + mUser.getmLastName());
    String mImageEncoded = mUser.getmUserImage();
    if (mImageEncoded != null) {
      byte[] b = Base64.decode(mImageEncoded, Base64.DEFAULT);
      Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
      mUserImage.setImageBitmap(bitmap);
    }

    mUserName.setText(mUser.getmUserName());
    mPhone.setText(mUser.getmPhone());
    mEmail.setText(mUser.getmEmail());
    mAddress.setText(mUser.getmAddress());
  }

  @Override
  protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
    if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
      // or get a single image only
      Image image = ImagePicker.getFirstImageOrNull(data);
      Glide.with(this)
          .load(image.getPath())
          .into(mUserImage);
      saveUserImage(getBase64FromFile(image.getPath()));
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void saveUserImage(String mUserImage) {
    new Thread(new Runnable() {
      @Override public void run() {
        try {

          mResults = putUserImage(mUserImage);
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              hideProgress();
              if (mResults == 1) {
                mUser.setmUserImage(mUserImage);
                showSuccess("Successful!");
              } else {
                showError("Failed To Save Image");
              }
            }
          });
        } catch (SQLException e) {
          showError("Failed To Save Image");
          e.printStackTrace();
        }
      }
    }).start();
  }

  public Integer putUserImage(String mUserImage) throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql = "UPDATE user SET image='"
        + mUserImage
        + "'WHERE iduser='"
        + mUser.getmId()
        + "'";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeUpdate(sql);
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

  private void saveUser() {
    new Thread(new Runnable() {
      @Override public void run() {
        try {

          mResults = putUserData(mPhone.getText().toString(), mEmail.getText().toString().trim(),
              mAddress.getText().toString().trim());
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              hideProgress();
              if (mResults == 1) {
                mUser.setmPhone(mPhone.getText().toString());
                mUser.setmEmail(mEmail.getText().toString().trim());
                mUser.setmAddress(mAddress.getText().toString().trim());
                showSuccess("Successful!");
              } else {
                showError("Failed To Save");
              }
            }
          });
        } catch (SQLException e) {
          hideProgress();
          showError("Failed To Save ");
          e.printStackTrace();
        }
      }
    }).start();
  }

  public Integer putUserData(String mPhone, String mEmail, String mAddress) throws SQLException {
    Connection mConnection = DbConnection.getConnect();
    String sql = "UPDATE user SET email='"
        + mEmail + "',phone='"
        + mPhone + "',address='"
        + mAddress
        + "'WHERE iduser='"
        + mUser.getmId()
        + "'";
    Statement mStatement = mConnection.createStatement();
    System.out.println(sql
        + "***********");
    return mStatement.executeUpdate(sql);
  }
}
