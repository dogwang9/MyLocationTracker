package com.example.mylocationtracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mylocationtracker.fragment.BottomSheetFragment;
import com.example.mylocationtracker.utils.MapUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class NewAreaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "NewAreaActivity";
    private GoogleMap mMap;
    private EditText mSerchEditText;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private static final int DEFAULT_ZOOM = 15;
    private static final LatLng DEFAULT_LOCATION = new LatLng(35.6895, 139.6917);
    private Location mLastKnownLocation;

    private GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_area);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requestLocationPermission();
        requestNotificationPermission();
        initView();
    }

    private void initView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mSerchEditText = findViewById(R.id.et_search);
        findViewById(R.id.bt_next).setOnClickListener(v -> {
            BottomSheetFragment bottomSheet = new BottomSheetFragment();
            bottomSheet.show(getSupportFragmentManager(), "NextBottomSheet");
        });

        mGeofencingClient = LocationServices.getGeofencingClient(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng center, float radius) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "=====Without location permission, geofence cannot be added");
            return;
        }

        Geofence geofence = new Geofence.Builder()
                .setRequestId("MyGeofence")
                .setCircularRegion(center.latitude, center.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        try {
            mGeofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "=====Geofence added successfully");
                        drawGeofenceCircle(center, radius);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "添加失败: " + e.getMessage()));
        } catch (SecurityException e) {
            Log.e(TAG, "=====Missing permission to add geofence: ", e);
        }
    }


    private void drawGeofenceCircle(LatLng center, float radius) {
        if (mMap == null) return;

        mMap.addCircle(new com.google.android.gms.maps.model.CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(0xFF0000FF)
                .strokeWidth(4f)
                .fillColor(0x220000FF));
    }


    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        return mGeofencePendingIntent;
    }


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                } else {
                    mLocationPermissionGranted = false;
                    Log.w(TAG, "======Location permission denied");
                }
            });

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

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

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocation = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocation = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocation != null && fineLocation) {
                    Log.d(TAG, "=====Foreground location permission has been granted");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestBackgroundLocationPermission();
                    } else {
                        updateLocationUI();
                        getDeviceLocation();
                    }
                } else if (coarseLocation != null && coarseLocation) {
                    Log.d(TAG, "=====Only coarse location permission");
                } else {
                    Log.w(TAG, "======Location permission denied");
                }
            });

    private void requestLocationPermission() {
        locationPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }


    private final ActivityResultLauncher<String> backgroundLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "=====Background location permission has been granted");
                    updateLocationUI();
                    getDeviceLocation();
                } else {
                    Log.w(TAG, "=====Background location permission denied, geofencing may not work properly");
                }
            });

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "=====Notification permission granted");
                } else {
                    Log.w(TAG, "=====Notification permission is denied, reminders will not be displayed");
                }
            });


    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        LatLng myLatLng = new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude());
                        addGeofence(myLatLng, 200f);
                        String myAddress = MapUtils.getAddressFromLatLng(this, myLatLng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions()
                                .position(myLatLng)
                                .title("my location")
                                .snippet(myAddress)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//                                .showInfoWindow();
                    } else {
                        Log.d(TAG, "Current location is null, using default.");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions().position(DEFAULT_LOCATION).title("default location"));
                    }
                });
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: " + e.getMessage(), e);
        }
    }
}