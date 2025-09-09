package com.example.mylocationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng DEFAULT_LOCATION = new LatLng(35.6895, 139.6917);
    private static final int DEFAULT_ZOOM = 15;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private TextView mRadiusTextView;
    private Circle mCircle;
    private final List<Friend> mFriends = getFriends();
    private final Set<String> mFriendsInRange = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        // 选择地图类型
        Spinner mapTypeSpinner = findViewById(R.id.spinner_map_type);
        String[] mapTypeNames = {"普通", "卫星", "地形", "混合"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mapTypeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(adapter);
        mapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap == null) return;
                switch (position) {
                    case 0:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (mMap != null) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels / 3);

        RecyclerView friendsList = findViewById(R.id.friends_list);
        friendsList.setLayoutManager(new LinearLayoutManager(this));
        List<Friend> friends = getFriends();
        FriendsAdapter friendsAdapter = new FriendsAdapter(friends, friend -> {
            if (mMap != null) {
                LatLng pos = new LatLng(friend.lat, friend.lng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 20));
            }
        });
        friendsList.setAdapter(friendsAdapter);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // 二维码生成
        Button qrButton = findViewById(R.id.btn_qr_generate);
        qrButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QrGenerateActivity.class);
//            double latitude = lastKnownLocation.getLatitude();
//            double longitude = lastKnownLocation.getLongitude();
            intent.putExtra("location", mLastKnownLocation);
            startActivity(intent);
        });
        // 二维码扫描
        Button qrScanButton = findViewById(R.id.btn_qr_scan);
        qrScanButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QrScanActivity.class);
            startActivity(intent);
        });
        // 区域管理
        Button myZoneButton = findViewById(R.id.btn_my_zone);
        myZoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyZoneActivity.class);
            startActivity(intent);
        });
        // 经纬度 -> 坐标
        Button geoButton = findViewById(R.id.btn_geo_coder);
        geoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LatLngToAddressActivity.class);
            startActivity(intent);
        });
        // 地址 -> 坐标
        Button addrButton = findViewById(R.id.btn_geo2_coder);
        addrButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddressToMarkerActivity.class);
            startActivity(intent);
        });
        // 路线
        Button routeButton = findViewById(R.id.btn_route);
        routeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RouteActivity.class);
            startActivity(intent);
        });

        // 区域
        mRadiusTextView = findViewById(R.id.tv_radius);
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadiusTextView.setText("范围: " + progress + "m");
                updateCircle(progress);
                checkFriendInRange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateCircle(int radius) {
        if (mMap == null || mLastKnownLocation == null) return;
        if (mCircle != null) mCircle.remove();
        LatLng myLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        mCircle = mMap.addCircle(new CircleOptions()
                .center(myLatLng)
                .radius(radius)
                .strokeColor(0x5500ff00)
                .fillColor(0x2200ff00)
                .strokeWidth(4f));
    }

    /**
     * 区域内朋友
     */
    private void checkFriendInRange(int radius) {
        if (mLastKnownLocation == null) {
            return;
        }
        for (Friend f : mFriends) {
            float[] results = new float[1];
            Location.distanceBetween(
                    mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),
                    f.lat, f.lng,
                    results);
            // 优化：用一个flag + hashset 来控制，使得toast只提醒一次
            boolean inRange = results[0] <= radius;
            if (inRange && !mFriendsInRange.contains(f.name)) {
                mFriendsInRange.add(f.name);
                String text = f.name + " 在范围内！距离约 " + (int) results[0] + " 米.";
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

            } else if (!inRange) {
                mFriendsInRange.remove(f.name);
            }
            Log.d("hashset", mFriendsInRange.toString());
//            if (results[0] <= radius) {
//                String text = f.name + " 在范围内！距离约 " + (int) results[0] + " 米.";
//                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        for (Friend f : getFriends()) {
            LatLng pos = new LatLng(f.lat, f.lng);
            String address = getAddressFromLatLng(pos);
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(f.name)
                    .snippet(f.mood)
                    .snippet(address)
                    .icon(createEmojiMarker(f.mood))).setTag(f);
        }
        // 给marker添加点击时间，给出我到朋友的建议路线
        mMap.setOnMarkerClickListener(marker -> {
            Friend friend = (Friend) marker.getTag();
            if (friend != null) {
                showRouteToFriend(friend);
            }
            return false;
        });
    }

    /**
     * 谷歌地图显示到朋友位置的路线
     */
    private void showRouteToFriend(Friend friend) {
        if (mLastKnownLocation == null) {
            return;
        }
        String uri = "http://maps.google.com/maps?saddr="
                + mLastKnownLocation.getLatitude() + ","
                + mLastKnownLocation.getLongitude()
                + "&daddr="
                + friend.lat + "," + friend.lng;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "请安装 Google Maps", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取当前位置，设置镜头居中
     */
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        LatLng myLatLng = new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude());
                        String address = getAddressFromLatLng(myLatLng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions()
                                .position(myLatLng)
                                .title("我的位置")
                                .snippet(address)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).showInfoWindow();
                    } else {
                        Log.d(TAG, "Current location is null, using default.");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions().position(DEFAULT_LOCATION).title("默认位置"));
                    }
                });
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: " + e.getMessage(), e);
        }
    }

    /**
     * 经纬度 -> 地址
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
            Toast.makeText(this, "无法获取地址", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 申请权限
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    /**
     * 打开图层和定位控件
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private List<Friend> getFriends() {
        List<Friend> list = new ArrayList<>();
        list.add(new Friend("A", 31.95, 118.75, "😊"));
        list.add(new Friend("B", 31.97, 118.75, "😢"));
        list.add(new Friend("C", 31.97, 118.76, "😎"));
        list.add(new Friend("D", 31.975, 118.757, "😒"));
        list.add(new Friend("E", 31.975, 118.750, "😶"));
        return list;
    }

    /**
     * emoji
     */
    private BitmapDescriptor createEmojiMarker(String emoji) {
        int size = 120; // Marker 大小
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // 白色圆形背景
        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, circlePaint);
        // 绘制 Emoji
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(70f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        // 垂直居中
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float x = size / 2f;
        float y = size / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f;
        canvas.drawText(emoji, x, y, textPaint);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}