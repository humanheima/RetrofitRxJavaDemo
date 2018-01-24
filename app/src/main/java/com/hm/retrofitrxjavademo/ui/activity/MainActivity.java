package com.hm.retrofitrxjavademo.ui.activity;

import android.view.View;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_upload_file_activity, R.id.btn_upload_okhttp_activity,
            R.id.btn_user_DownloadManager, R.id.btn_only_retrofit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload_file_activity:
                break;
            case R.id.btn_upload_okhttp_activity:
                break;
            case R.id.btn_user_DownloadManager:
                DownloadManagerActivity.launch(this);
                break;
            case R.id.btn_only_retrofit:
                OnlyRetrofitActivity.launch(this);
                break;
            default:
                break;
        }

    }

    public void launchRxJava2(View view) {
        RxJava2Activity.launch(this);
    }
}
