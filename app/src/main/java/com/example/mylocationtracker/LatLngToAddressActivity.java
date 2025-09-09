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

public class LatLngToAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText mLatitudeEditText, mLongitudeEditText;
    private Button mShowMarkerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latlng_to_address);
        mLatitudeEditText = findViewById(R.id.et_latitude);
        mLongitudeEditText = findViewById(R.id.et_longitude);
        mShowMarkerButton = findViewById(R.id.btn_show_marker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        mShowMarkerButton.setOnClickListener(v -> showMarkerFromInput());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void showMarkerFromInput() {
        String latStr = mLatitudeEditText.getText().toString().trim();
        String lngStr = mLongitudeEditText.getText().toString().trim();
        if (TextUtils.isEmpty(latStr) || TextUtils.isEmpty(lngStr)) {
            Toast.makeText(this, "请输入有效的经纬度", Toast.LENGTH_SHORT).show();
            return;
        }
        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "经纬度格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng latLng = new LatLng(lat, lng);
        String address = getAddressFromLatLng(latLng);
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(address != null ? address : "未知地址"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "无法获取地址", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}