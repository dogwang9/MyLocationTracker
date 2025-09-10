package com.example.mylocationtracker;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText mAddressEditText;
    private Button mAddressToMarkerButton;
    private EditText mLatitudeEditText, mLongitudeEditText;
    private Button mLatLngToMarkerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocoding);
        initView();
    }

    private void initView() {
        mAddressEditText = findViewById(R.id.et_address);
        mAddressToMarkerButton = findViewById(R.id.btn_address_to_latlng);
        mLatitudeEditText = findViewById(R.id.et_latitude);
        mLongitudeEditText = findViewById(R.id.et_longitude);
        mLatLngToMarkerButton = findViewById(R.id.btn_latlng_to_address);
        mAddressToMarkerButton.setOnClickListener(v -> showMarkerFromAddress());
        mLatLngToMarkerButton.setOnClickListener(v -> showMarkerFromLatLng());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * 地址 -> 坐标
     */
    private void showMarkerFromAddress() {
        String addressStr = mAddressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(addressStr)) {
            Toast.makeText(this, "Please enter address!", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                updateMap(latLng, addressStr);
            } else {
                Toast.makeText(this, "Unable to resolve the address!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoding error!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 经纬度 -> 坐标
     */
    private void showMarkerFromLatLng() {
        String latStr = mLatitudeEditText.getText().toString().trim();
        String lngStr = mLongitudeEditText.getText().toString().trim();
        if (TextUtils.isEmpty(latStr) || TextUtils.isEmpty(lngStr)) {
            Toast.makeText(this, "Please enter the latitude and longitude!", Toast.LENGTH_SHORT).show();
            return;
        }

        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Incorrect latitude or longitude format!", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng latLng = new LatLng(lat, lng);
        String address = getAddressFromLatLng(latLng);
        updateMap(latLng, address != null ? address : "Unknown address");
    }

    /**
     * 根据坐标获取地址
     */
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to obtain address!", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 更新地图显示
     */
    private void updateMap(LatLng latLng, String title) {
        if (mMap == null) return;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
}
