package com.example.mylocationtracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<LatLng> mPointsList = new ArrayList<>();
    private Polyline mPolyline;
    private TextView mDistanceTextView;
    private double totalDistance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        mDistanceTextView = findViewById(R.id.tv_distance);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLoc = new LatLng(31.997, 118.781);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 12));
        mMap.setOnMapClickListener(latLng -> {
            addMarker(latLng);
            updateRoute();
        });
    }

    private void addMarker(LatLng latLng) {
        mPointsList.add(latLng);
        mMap.addMarker(new MarkerOptions().position(latLng).title("点 " + mPointsList.size()));
    }

    private void updateRoute() {
        if (mPolyline != null) {
            mPolyline.remove();
        }

        if (mPointsList.size() > 1) {
            mPolyline = mMap.addPolyline(new PolylineOptions().addAll(mPointsList).width(5).color(0xFFFF0000));
            totalDistance = 0.0;
            for (int i = 0; i < mPointsList.size() - 1; i++) {
                totalDistance += getDistance(mPointsList.get(i), mPointsList.get(i + 1));
            }
            mDistanceTextView.setText(String.format("总路程：%.2f 公里", totalDistance / 1000));
        }
    }

    private float getDistance(LatLng start, LatLng end) {
        float[] result = new float[1];
        android.location.Location.distanceBetween(
                start.latitude, start.longitude,
                end.latitude, end.longitude,
                result
        );
        return result[0];
    }
}
