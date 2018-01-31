package com.hm.retrofitrxjavademo.ui.activity;

import android.Manifest;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityMainBinding;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        requestPermission();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test_rxjava_operator:
                RxJavaOperatorActivity.launch(this);
                break;
            case R.id.btn_retrofit_rxjava:
                RetrofitRxJavaActivity.launch(this);
                break;
            case R.id.btn_upload_okhttp_activity:
                OkHttpDownloadActivity.launch(this);
                break;
            case R.id.btn_user_DownloadManager:
                DownloadManagerActivity.launch(this);
                break;
            case R.id.btn_only_retrofit:
                OnlyRetrofitActivity.launch(this);
                break;
            case R.id.btn_source_code:
                RxJava2Activity.launch(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestPermission() {
        String[] prems = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};
        if (EasyPermissions.hasPermissions(this, prems)) {
            Toast.makeText(this, "have got permission", Toast.LENGTH_LONG).show();
        } else {
            EasyPermissions.requestPermissions(MainActivity.this, "Request WRITE_EXTERNAL_STORAGE permission", REQUEST_PERMISSION, prems);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
