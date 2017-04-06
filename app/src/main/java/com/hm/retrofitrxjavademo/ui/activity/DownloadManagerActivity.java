package com.hm.retrofitrxjavademo.ui.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 使用系统自带的DownloadManager下载
 */
public class DownloadManagerActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "DownloadManagerActivity";
    public static final String PROGRESS = "progress";
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_CODE = 14;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.textProgress)
    TextView textProgress;
    private String wifiUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/DF6B2FB4A4628C2870C710046C231348.apk?mkey=58d4b294acc7802a&f=8e5d&c=0&fsname=com.snda.wifilocating_4.1.88_3108.apk&csr=1bbd&p=.apk";
    private long id;
    private DownloadManager downloadManager;
    private DownloadManager.Query query;

    private String downloadPath;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int progress = bundle.getInt(PROGRESS);
            progressBar.setProgress(progress);
            textProgress.setText(String.valueOf(progress) + "%");
            if (progress == 100) {
                timer.cancel();
                install(downloadPath);
            }
        }
    };

    public static void launch(Context context) {
        Intent starter = new Intent(context, DownloadManagerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_download_manager;
    }

    @Override
    protected void initData() {
        progressBar.setMax(100);
        query = new DownloadManager.Query();
    }

    @OnClick({R.id.btn_start, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
                    startDownLoad();
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.rationale), REQUEST_CODE, PERMISSIONS);
                }
                break;
            case R.id.btn_cancel:
                cancelDownload();
                btnStart.setClickable(true);
                timer.cancel();
                textProgress.setText("");
                progressBar.setProgress(0);
                break;
        }
    }

    private void startDownLoad() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                queryProgress();
            }
        };
        btnStart.setClickable(false);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(wifiUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "wifi.apk");
        downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "wifi.apk";
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.allowScanningByMediaScanner();
        //定制Notification样式
        request.setTitle("下载");
        request.setDescription("正在下载,请稍后...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //设置下载文件类型
        request.setMimeType("application/vnd.android.package-archive");
        id = downloadManager.enqueue(request);

        timer.schedule(timerTask, 0, 1000);
    }

    private void cancelDownload() {
        if (id != 0) {
            downloadManager.remove(id);
        }
    }

    private void queryProgress() {
        if (downloadManager != null) {
            Cursor cursor = downloadManager.query(query.setFilterById(id));
            if (cursor != null && cursor.moveToFirst()) {
                String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                //已经下载的字节数
                int bytesDownload = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
                long downloadId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                int progress = bytesDownload * 100 / bytesTotal;
                Log.e(TAG, "progress=" + progress);
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt(PROGRESS, progress);
                message.setData(bundle);
                handler.sendMessage(message);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void install(String path) {
        Uri uri;
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, "com.hm.retrofitrxjavademo.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            Log.e(TAG, "install uri=" + uri);
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            Log.e(TAG, "Uri.fromFile(file)=" + Uri.fromFile(file));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//4.0以上系统弹出安装成功打开界面
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE) {
            if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
                startDownLoad();
            } else {
                Toast.makeText(this, "没有响应的权限，无法进行下载", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRationale("下载需要读写权限")
                    .setRequestCode(AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE)
                    .setTitle("请求权限")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消")
                    .build().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
