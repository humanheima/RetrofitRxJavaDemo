package com.hm.retrofitrxjavademo.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hm.retrofitrxjavademo.R;

/**
 * 使用系统自带的DownloadManager下载
 */
public class DownloadManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
    }
}
