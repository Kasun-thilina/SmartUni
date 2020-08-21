package com.app.smartuni;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.smartuni.base.BaseActivity;
import com.app.smartuni.utill.DateUtills;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventActivity extends BaseActivity implements OnMapReadyCallback {
    ResultSet mResults;
    Integer mEventId = -1;
    Integer mResult = -1;
    String userId;
    String[] skills;
    @BindView(R.id.tv_event_name)
    TextView mTextTitle;
    @BindView(R.id.text_date)
    TextView mTextDate;
    @BindView(R.id.text_location)
    TextView mTextLocation;
    @BindView(R.id.text_category)
    TextView mTextCategory;
    @BindView(R.id.text_time)
    TextView mTextTime;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView tvEventDetails;
    private MapView mMapView;
    private GoogleMap mMap;
    private String userskills = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userId = prefs.getString("user_id", "");
        tvEventDetails = findViewById(R.id.text_location);
        mMapView = findViewById(R.id.fl_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mTextTime.setVisibility(View.INVISIBLE);
        //if (nfcAdapter == null) {
        //  showWarning("This device doesn't support NFC.");
        //  finish();
        //}
        //if (!nfcAdapter.isEnabled()) {
        //  showWarning("NFC is disabled.");
        //}
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Events");
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


    }

    @OnClick({R.id.button_participate, R.id.button_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_participate: {
                new LovelyInfoDialog(this)
                        .setTopColorRes(R.color.colorAccent)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitle("New Skills Added")
                        .setMessage(userskills)
                        .show();
                break;
            }

            case R.id.button_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                showWirelessSettings();
            }

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        showWarning("You need to enable NFC");
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                final Ndef ndef = Ndef.get(tag);
                try {
                    ndef.connect();
                    NdefMessage ndefMessage = ndef.getNdefMessage();
                    Log.d("", "");
                    showSuccess("New NFC Tag Detected!");
                    displayMsgs(ndefMessage);
                } catch (IOException | FormatException e) {
                    e.printStackTrace();
                }
            }

            //displayMsgs(ndef);
        }
    }

    private String dumpTagData(Tag tag) {
        return null;
    }

    private void displayMsgs(NdefMessage ndefMessages) {

        tvEventDetails.setVisibility(View.VISIBLE);
        mTextDate.setVisibility(View.VISIBLE);
        mTextCategory.setVisibility(View.VISIBLE);
        mTextTime.setVisibility(View.VISIBLE);

        mTextTitle.setVisibility(View.VISIBLE);
        if (ndefMessages.getRecords() != null) {
//          String[] locationName = new String(ndefMessages.getRecords()[0].getPayload()).split(",", 3);
            tvEventDetails.setText("SLIIT Academy");
//          String[] coordinates = new String(ndefMessages.getRecords()[1].getPayload()).split(":", 2);
//          skills = new String(ndefMessages.getRecords()[2].getPayload()).split(":", 5);
//          String[] latlng = coordinates[1].split(",", 2);
            LatLng location = new LatLng(6.91279, 79.8484543F);
            mMap.addMarker(new MarkerOptions().position(location).title("SLIIT Academy"));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    location, 15);
            mMap.animateCamera(cameraUpdate);
        }

        //
        //if (msgs == null || msgs.length == 0)
        //  return;
        //
        //StringBuilder builder = new StringBuilder();
        //List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        //final int size = records.size();
        //
        //for (int i = 0; i < size; i++) {
        //  ParsedNdefRecord record = records.get(i);
        //  String str = record.str();
        //  builder.append(str).append("\n");
        //}
        //
        //text.setText(builder.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public final void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void getEventData(String mEventId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mResults = fetchData(mEventId);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            try {
                                if (mResults.next()) {
                                    System.out.println(mResults.getString("title") + "-----------");
                                    String mTitle = mResults.getString("title");
                                    String mDate = mResults.getString("date");
                                    String mTime = mResults.getString("time");
                                    String mCategory = mResults.getString("category");
                                    String location = mResults.getString("location");
                                    mTextLocation.setText(location);
                                    mTextTitle.setText(mTitle);
                                    mTextDate.setText(mDate);
                                    mTextCategory.setText(mCategory);
                                    mTextTime.setText(mTime);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ResultSet fetchData(String mEventId) throws SQLException {
        Connection mConnection = DbConnection.getConnect();
        String sql = "select * from event where idevent='"
                + mEventId
                + "'";
        Statement mStatement = mConnection.createStatement();
        System.out.println(sql
                + "***********");
        return mStatement.executeQuery(sql);
    }

    private void participateEvent(String mEventId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = DbConnection.getConnect();
                    Statement mStatement = connection.createStatement();
                    String mquery =
                            "INSERT INTO user_events (user_iduser,attended_at,event_idevent) VALUES ('"
                                    +
                                    userId
                                    + "','"
                                    +
                                    DateUtills.getCalenderFormatted(Calendar.getInstance(),
                                            DateUtills.YYYY_MM_DD_HH_MM_SS)
                                    + "','"
                                    +
                                    mEventId
                                    + "')";
                    System.out.println("----" + mquery);
                    mResult = mStatement.executeUpdate(mquery);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            if (mResult == 1) {
                                showSuccess("Successful!");
                                finish();
                            } else {
                                showError("Failed To Save The Event");
                            }
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
