package com.hm.retrofitrxjavademo.ui.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownLoadManagerActivity extends AppCompatActivity {

    private static final String TAG = "DownLoadManagerActivity";
    @BindView(R.id.btn_download)
    Button btnDownload;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private long downLoadId;
    private Uri uri;

    private DownLoadReceiver downLoadReceiver;
    private String downLoadUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/20AD322F5D49B9F649A70C4A3083D8D2.apk?mkey=58758c694bc7812a&f=d588&c=0&fsname=com.xunao.wanfeng_1.1_4.apk&csr=4d5s&p=.apk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_manager);
        ButterKnife.bind(this);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request = new DownloadManager.Request(Uri.parse(downLoadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setDestinationUri();
    }

    public File createImageFile() {
        File file = null;
        File dir;
        try {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = File.createTempFile("download", ".apk", dir);
            Log.e("createImageFile", file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("createImageFile", e.getMessage());
        }
        return file;
    }

    @OnClick({R.id.btn_download, R.id.btn_cancel, R.id.btn_query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                downLoadId = downloadManager.enqueue(request);
                downLoadReceiver = new DownLoadReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
                intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(downLoadReceiver, intentFilter);
                break;
            case R.id.btn_cancel:
                downloadManager.remove(downLoadId);
                break;
            case R.id.btn_query:
                queryDownloadInfo();
                break;
            default:
                break;
        }
    }

    private void queryDownloadInfo() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downLoadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.e(TAG, cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
                Log.e(TAG, cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
                Log.e(TAG, cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)));

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downLoadReceiver);
    }

    class DownLoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Toast.makeText(DownLoadManagerActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                Toast.makeText(DownLoadManagerActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
