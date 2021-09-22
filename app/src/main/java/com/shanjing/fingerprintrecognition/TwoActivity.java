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
import android.widget.CompoundButton;
import android.widget.Toast;

import com.shanjing.fingerprint.FingerprintCallback;
import com.shanjing.fingerprint.FingerprintVerifyManager;
import com.shanjing.fingerprintrecognition.databinding.ActivityTwoBinding;
import com.tencent.mmkv.MMKV;

public class TwoActivity extends AppCompatActivity {

    private ActivityTwoBinding binding;
    private MMKV kv = MMKV.defaultMMKV();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTwoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (kv.decodeBool("isFing")) {
            binding.sc.setChecked(true);
        } else {
            binding.sc.setChecked(false);
        }

        binding.sc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                initView();
            } else {
                kv.encode("isFing", false);
            }
        });
    }

    private void initView() {
        fingerprint();
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
            Toast.makeText(TwoActivity.this, getString(R.string.biometricprompt_verify_success), Toast.LENGTH_SHORT).show();
            kv.encode("isFing", true);
        }

        @Override
        public void onFailed() {
            Toast.makeText(TwoActivity.this, getString(R.string.biometricprompt_verify_failed), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUsepwd() {
            Toast.makeText(TwoActivity.this, getString(R.string.fingerprint_usepwd), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(TwoActivity.this, getString(R.string.fingerprint_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onHwUnavailable() {
            Toast.makeText(TwoActivity.this, getString(R.string.biometricprompt_finger_hw_unavailable), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNoneEnrolled() {
            //弹出提示框，跳转指纹添加页面
            AlertDialog.Builder lertDialogBuilder = new AlertDialog.Builder(TwoActivity.this);
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
