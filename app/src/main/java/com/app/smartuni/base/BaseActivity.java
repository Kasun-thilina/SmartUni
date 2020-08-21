package com.app.smartuni.base;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.app.smartuni.R;
import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity {

  private Dialog mProgressDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mProgressDialog = new Dialog(this);
    mProgressDialog.setContentView(R.layout.progress_dialog);
    mProgressDialog.setCancelable(false);
    mProgressDialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }
  }

  public void showProgress() {
    if (mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
    mProgressDialog.show();
  }

  public void hideProgress() {
    mProgressDialog.hide();
  }

  public void showError(String message) {
    Toasty.error(getApplicationContext(), message, Toast.LENGTH_SHORT, true).show();
  }

  public void showWarning(String message) {
    Toasty.warning(getApplicationContext(), message, Toast.LENGTH_SHORT, true).show();
  }

  public void showSuccess(String message) {
    Toasty.success(getApplicationContext(), message, Toast.LENGTH_SHORT, true).show();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        //finish();
        onBackPressed();
        break;
    }
    return true;
  }

  @Override public void finish() {
    if (mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
    super.finish();
  }

  @Override
  public void onBackPressed() {
    finish();
  }
}
