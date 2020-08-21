package com.app.smartuni;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.app.smartuni.base.BaseActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;

public class AddLocationActivity extends BaseActivity
    implements OnMapReadyCallback {
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 200;
  private static final float DEFAULT_ZOOM = 10f;
  @BindView(R.id.text_location) TextView mLocation;
  @BindView(R.id.mv_location_picker) MapView mMapView;
  GoogleMap map;
  Boolean isPaused = false;
  Context mContext;
  List<Address> mAddress = null;
  private boolean mLocationPermissionGranted;
  private Location mLastKnownLocation;
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private LatLng mDefaultLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_picker);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Select a Location");
    ButterKnife.bind(this);
    mMapView.onCreate(savedInstanceState);
    mMapView.getMapAsync(this);
    mContext = this;
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
  }

  @OnClick({ R.id.button_done })
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.button_done:
        Intent intent = new Intent();
        intent.putExtra("LOCATION_NAME", mAddress.get(0).getAddressLine(0));
        intent.putExtra("LOCATION_LATITUDE", mAddress.get(0).getLatitude());
        intent.putExtra("LOCATION_LONGATIUDE", mAddress.get(0).getLongitude());
        setResult(Activity.RESULT_OK, intent);
        finish();
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    updateLocationUI();

    // Get the current location of the device and set the position of the map.
    getDeviceLocation();

    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override public void onMapClick(LatLng latLng) {

        map.clear();
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.addMarker(new MarkerOptions().position(latLng));
        Geocoder geocoder = new Geocoder(mContext);
        try {
          mAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 3);
        } catch (IOException e) {
          e.printStackTrace();
        }
        mLocation.setText(mAddress.get(0).getAddressLine(0));
      }
    });
  }

  private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
        android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      mLocationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(this,
          new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    mLocationPermissionGranted = false;
    switch (requestCode) {
      case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          mLocationPermissionGranted = true;
        }
      }
    }
    updateLocationUI();
  }

  private void updateLocationUI() {
    if (map == null) {
      return;
    }
    try {
      if (mLocationPermissionGranted) {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
      } else {
        map.setMyLocationEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        mLastKnownLocation = null;
        getLocationPermission();
      }
    } catch (SecurityException e) {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  @SuppressLint("ResourceType")
  private void getDeviceLocation() {
    try {
      if (mLocationPermissionGranted) {
        Task locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener() {
          @Override
          public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
              // Set the map's camera position to the current location of the device.
              mLastKnownLocation = (Location) task.getResult();
              if (mLastKnownLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
              }
            } else {
              //Log.d(TAG, "Current location is null. Using defaults.");
              //Log.e(TAG, "Exception: %s", task.getException());
              map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
              map.getUiSettings().setMyLocationButtonEnabled(false);
            }
          }
        });
      }
    } catch (SecurityException e) {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  @Override protected void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mMapView.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();
    mMapView.onResume();
    if (isPaused) {
      getDeviceLocation();
      isPaused = false;
    }
  }

  @Override public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
  }
}
