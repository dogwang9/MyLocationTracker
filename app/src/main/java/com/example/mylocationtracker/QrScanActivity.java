package com.example.mylocationtracker;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrScanActivity extends AppCompatActivity {
    private PreviewView mPreviewView;
    private ExecutorService mCameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        mPreviewView = findViewById(R.id.previewView);
        mCameraExecutor = Executors.newSingleThreadExecutor();
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this);
        scanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Task completed successfully
                            String rawValue = barcode.getRawValue();
                            Toast.makeText(this, rawValue, Toast.LENGTH_SHORT).show();
                        })
                .addOnCanceledListener(
                        () -> {
                            // Task canceled
                            Toast.makeText(this, "取消扫码", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                            Toast.makeText(this, "扫码失败", Toast.LENGTH_SHORT).show();
                            finish();
                        });
    }
}