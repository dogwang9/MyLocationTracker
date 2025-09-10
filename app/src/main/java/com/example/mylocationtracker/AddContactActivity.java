package com.example.mylocationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddContactActivity extends AppCompatActivity {

    private EditText mEnterCodeEditText;

    private final ActivityResultLauncher<Intent> qrLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String qrCode = result.getData().getStringExtra("qr_result");
                    if (qrCode != null) {
                        mEnterCodeEditText.setText(qrCode);
                    }
                } else {
                    Toast.makeText(this, "Scan code to cancel or fail!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        mEnterCodeEditText = findViewById(R.id.et_enter_code);

        findViewById(R.id.bt_qr_scan).setOnClickListener(v -> {
            Intent intent = new Intent(AddContactActivity.this, QrScanActivity.class);
            qrLauncher.launch(intent);
        });

        findViewById(R.id.bt_next).setOnClickListener(v -> {
            String code = mEnterCodeEditText.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Please enter or scan the QR code!", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO 扫描二维码成功后自动粘贴文本框，点击next下一步应该调到添加设置界面
            Intent intent = new Intent(this, GeocodingActivity.class);
            intent.putExtra("code", code);
            startActivity(intent);
        });
    }
}