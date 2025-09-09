package com.example.mylocationtracker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrGenerateActivity extends AppCompatActivity {
    private ImageView mQRImageView;
    private TextView mQRCodeTextView;
    private Button mCopyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_generate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mQRImageView = findViewById(R.id.qrImageView);
        mQRCodeTextView = findViewById(R.id.qr_code);
        mCopyButton = findViewById(R.id.btn_copy_code);
        String uniqueCode = generateUniqueCode();
        mQRCodeTextView.setText(uniqueCode);
        generateQRCode(uniqueCode);
        mCopyButton.setOnClickListener(v -> {
            String code = mQRCodeTextView.getText().toString().trim();
            if (!code.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("QR Code", code);
                clipboard.setPrimaryClip(clip);
                //Toast.makeText(QrGenerateActivity.this, "已复制到剪贴板: " + code, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(QrGenerateActivity.this, "没有可复制的内容！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateQRCode(String text) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            mQRImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成15位唯一code：时间戳 + 随机数
     */
    private String generateUniqueCode() {
        long timestamp = System.currentTimeMillis() / 1000;
        int random = new java.util.Random().nextInt(100000);
        return String.format("%010d%05d", timestamp, random);
    }
}