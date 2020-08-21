package com.app.smartuni;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.utill.DateUtills;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class AddEventActivity extends BaseActivity {
    private static final int PLACE_RESULT = 120;
    @BindView(R.id.edit_date)
    EditText mUserDate;
    @BindView(R.id.edit_time)
    EditText mUserTime;
    @BindView(R.id.edit_title)
    EditText mTitle;
    @BindView(R.id.edit_description)
    EditText mDescription;
    @BindView(R.id.edit_field)
    EditText mEventField;
    @BindView(R.id.edit_location)
    EditText mEventLocation;
    String mLongatiude = null;
    String mLatitude = null;
    private Integer mResults = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Event");
        ButterKnife.bind(this);
    }

    @OnClick({ R.id.edit_date, R.id.edit_time, R.id.button_create, R.id.edit_location })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_date:
                showDatePicker();
                break;

            case R.id.edit_time:
                showTimePicker();
                break;
            case R.id.button_create:
                processAddEvent();
                break;
            case R.id.edit_location:
                showLocationPicker();
                break;
        }
    }

    private void showLocationPicker() {
        startActivityForResult(new Intent(getApplicationContext(), AddLocationActivity.class),
            PLACE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_RESULT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mEventLocation.setText(data.getStringExtra("LOCATION_NAME"));
                    mLatitude = data.getStringExtra("LOCATION_LATITUDE");
                    mLongatiude = data.getStringExtra("LOCATION_LONGATIUDE");
                }
            }
        }
    }

    private void processAddEvent() {
        if (isValidForm()) {
            String mDateString = mUserDate.getText().toString().trim();
            String mTimeString = mUserTime.getText().toString().trim();
            String mTitleString = mTitle.getText().toString().trim();
            String mDescriptionString = mDescription.getText().toString().trim();
            String mFieldString = mEventField.getText().toString().trim();
            String mLocationString = mEventLocation.getText().toString().trim();
            showProgress();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection connection = DbConnection.getConnect();
                        Statement mStatement = connection.createStatement();
                        String mquery =
                            "INSERT INTO event (date,title,description,category,longatiude,lattitude,location,time) VALUES ('"
                                +
                                mDateString
                                + "','"
                                +
                                mTitleString
                                + "','"
                                +
                                mDescriptionString
                                + "','"
                                +
                                mFieldString
                                + "','"
                                +
                                mLongatiude + "',"
                                + "'"
                                +
                                mLatitude + "',"
                                + "'"
                                +
                                mLocationString + "','" +
                                mTimeString
                                + "')";
                        System.out.println("----" + mquery);
                        mResults = mStatement.executeUpdate(mquery);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                                if (mResults == 1) {
                                    showSuccess("Successful!");
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

    private boolean isValidForm() {
        clearValidations();
        if (mUserDate.getText().toString().isEmpty()) {
            showWarning("Event Date Required!");
            mUserDate.setError("");
            return false;
        }

        if (mUserTime.getText().toString().isEmpty()) {
            showWarning("Event Time Required!");
            mUserTime.setError("");
            return false;
        }

        if (mTitle.getText().toString().isEmpty()) {
            showWarning("Event Title Required!");
            mTitle.setError("");
            return false;
        }

        if (mDescription.getText().toString().isEmpty()) {
            showWarning("Event Description Required!");
            mDescription.setError("");
            return false;
        }

        if (mEventField.getText().toString().isEmpty()) {
            showWarning("Event Field Required!");
            mEventField.setError("");
            return false;
        }
        if (mEventLocation.getText().toString().isEmpty()) {
            showWarning("Event Location Required!");
            mEventLocation.setError("");
            return false;
        }
        return true;
    }

    private void clearValidations() {
        mUserDate.setError(null);
        mUserTime.setError(null);
        mTitle.setError(null);
        mDescription.setError(null);
        mEventField.setError(null);
        mEventLocation.setError(null);
    }

    private void showTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                StringBuilder mTime = new StringBuilder();
                mTime.append(selectedHour);
                mTime.append(":");
                mTime.append(selectedMinute);
                mUserTime.setText(mTime.toString());
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void showDatePicker() {
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog mStartTime =
            new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                System.out.println("--------------" + newDate.getTime());
                mUserDate.setText(DateUtills.getCalenderFormatted(newDate, DateUtills.YYYY_MM_DD));
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        mStartTime.show();
    }
}
