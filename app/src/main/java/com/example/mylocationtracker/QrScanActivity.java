package com.example.mylocationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

public class QrScanActivity extends AppCompatActivity {

    private static final String TAG = "QrScanActivity";
    private static final int CAMERA_PERMISSION_CODE = 101;

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            startQrScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQrScan();
            } else {
                Toast.makeText(this, "请授予相机权限才能扫码", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkCameraPermission();
    }

    private void startQrScan() {
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this);

        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String rawValue = barcode.getRawValue();
                    if (rawValue != null) {
                        Intent data = new Intent();
                        data.putExtra("qr_result", rawValue);
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        Log.d(TAG,"Scan failed!");
                        Toast.makeText(this, "Scan failed!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .addOnCanceledListener(() -> {
                    Log.d(TAG,"Scan canceled!");
                    Toast.makeText(this, "Scan canceled!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG,"Scan failed!");
                    Toast.makeText(this, "Scan failed!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                    finish();
                });
    }
}
