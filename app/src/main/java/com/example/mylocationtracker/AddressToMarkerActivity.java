package com.example.mylocationtracker;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class AddressToMarkerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText mAddressEditText;
    private Button mShowMarkerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_to_marker);

        mAddressEditText = findViewById(R.id.et_address);
        mShowMarkerButton = findViewById(R.id.btn_show_marker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mShowMarkerButton.setOnClickListener(v -> showMarkerFromAddress());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void showMarkerFromAddress() {
        String addressStr = mAddressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(addressStr)) {
            Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        geocoder.getFromLocationName(addressStr, 1, new Geocoder.GeocodeListener() {

            @Override
            public void onError(@Nullable String errorMessage) {
                Geocoder.GeocodeListener.super.onError(errorMessage);
                runOnUiThread(() ->
                        Toast.makeText(AddressToMarkerActivity.this, "地理编码错误: " + errorMessage, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onGeocode(@NonNull List<Address> addresses) {
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    runOnUiThread(() -> {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(addressStr));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(AddressToMarkerActivity.this, "无法解析该地址", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

//    private void showMarkerFromAddress() {
//        String addressStr = etAddress.getText().toString().trim();
//        if (TextUtils.isEmpty(addressStr)) {
//            Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        LatLng latLng = getLatLngFromAddress(addressStr);
//        if (latLng != null) {
//            mMap.clear();
//            mMap.addMarker(new MarkerOptions().position(latLng).title(addressStr));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//        } else {
//            Toast.makeText(this, "无法解析该地址", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private LatLng getLatLngFromAddress(String addressStr) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address address = addresses.get(0);
//                return new LatLng(address.getLatitude(), address.getLongitude());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
