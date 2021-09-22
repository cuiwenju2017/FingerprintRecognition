package com.shanjing.fingerprintrecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.shanjing.fingerprint.FingerprintCallback;
import com.shanjing.fingerprint.FingerprintVerifyManager;
import com.shanjing.fingerprintrecognition.databinding.ActivityMainBinding;
import com.tencent.mmkv.MMKV;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MMKV kv = MMKV.defaultMMKV();
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
    }

    private void initView() {
        if (kv.decodeBool("isFing")) {
            fingerprint();
            binding.tvFing.setVisibility(View.VISIBLE);
        } else {
            binding.tvFing.setVisibility(View.GONE);
        }

        binding.tvFing.setOnClickListener(v -> {
            fingerprint();
        });

        binding.btnLogin.setOnClickListener(v -> {
            username = binding.etUsername.getText().toString().trim();
            password = binding.etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            } else {
                kv.encode("username", username);
                kv.encode("password", password);
                startActivity(new Intent(MainActivity.this, TwoActivity.class));
                finish();
            }
        });
    }

    private void fingerprint() {
        FingerprintVerifyManager.Builder builder = new FingerprintVerifyManager.Builder(this);
        builder.callback(fingerprintCallback)
                .fingerprintColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .build();
    }

    private FingerprintCallback fingerprintCallback = new FingerprintCallback() {
        @Override
        public void onSucceeded() {
            Toast.makeText(MainActivity.this, getString(R.string.biometricprompt_verify_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, TwoActivity.class));
            finish();
        }

        @Override
        public void onFailed() {
            Toast.makeText(MainActivity.this, getString(R.string.biometricprompt_verify_failed), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUsepwd() {
            Toast.makeText(MainActivity.this, getString(R.string.fingerprint_usepwd), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, getString(R.string.fingerprint_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onHwUnavailable() {
            Toast.makeText(MainActivity.this, getString(R.string.biometricprompt_finger_hw_unavailable), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNoneEnrolled() {
            //弹出提示框，跳转指纹添加页面
            AlertDialog.Builder lertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            lertDialogBuilder.setTitle(getString(R.string.biometricprompt_tip))
                    .setMessage(getString(R.string.biometricprompt_finger_add))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.biometricprompt_finger_add_confirm), ((DialogInterface dialog, int which) -> {
                        Intent intent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                        startActivity(intent);
                    }
                    ))
                    .setPositiveButton(getString(R.string.biometricprompt_cancel), ((DialogInterface dialog, int which) -> {
                        dialog.dismiss();
                    }
                    ))
                    .create().show();
        }

    };

}
