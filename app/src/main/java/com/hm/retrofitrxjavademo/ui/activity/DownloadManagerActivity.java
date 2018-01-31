package com.hm.retrofitrxjavademo.ui.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityDownloadManagerBinding;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import com.hm.retrofitrxjavademo.util.ToastUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 使用系统自带的DownloadManager下载
 */
public class DownloadManagerActivity extends BaseActivity<ActivityDownloadManagerBinding> implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "DownloadManagerActivity";
    public static final int WHAT_PROGRESS = -100;
    public static final int WHAT_REASON = -200;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_CODE = 14;
    //private String apkUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/DF6B2FB4A4628C2870C710046C231348.apk?mkey=58d4b294acc7802a&f=8e5d&c=0&fsname=com.snda.wifilocating_4.1.88_3108.apk&csr=1bbd&p=.apk";
    private String apkUrl = "http://p.gdown.baidu.com/eec51f59e7b34aae92c1e42e581c433b8a12e4c60386d66e38586bf760dc53b710d39f33cb476e248b2933c7b83b9f5afbaf71a8640b354dedb03e0035c42709e9c310f11f22164d1c5e2a3b86e41b5b886d8944b1913c5fe7889588f9e4d1a01e7adf108a23fbb247e63fc7845ae0adcc2322844d8f5b0834ceb9ff0a0b551c4ae0d4c361981e9337aa66d2414be0e0865aece920281684edd3a99195dfc6a01cef016a5343ace4d4daa98d98d3bd29d2ae878fba53b91c5c079ac3c82a2b59591703bf478ec3964b5526aedf6a4b601538926fee90cd0042aa974be383098ba185da56e72c3876aacccad035bb7709360433b7d27d7521417fa6e03333357cbbe6e7eb5befd5045220a14cdfeb973d6c30ce662d995b5da8243f2f9b232c7e";
    private long id;
    private DownloadManager downloadManager;
    private DownloadManager.Query query;

    private String downloadPath;
    private Timer timer;
    private MyHandler handler;


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
        viewBind.progressBar.setMax(100);
        handler = new MyHandler(this);
        query = new DownloadManager.Query();
    }

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
                break;
        }
    }

    private void startDownLoad() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                queryProgress();
            }
        };
        viewBind.btnStart.setClickable(false);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "wifi.apk");
        downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "wifi.apk";
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.allowScanningByMediaScanner();
        request.setTitle("下载");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置下载文件类型
        request.setMimeType("application/vnd.android.package-archive");
        id = downloadManager.enqueue(request);
        timer.schedule(timerTask, 0, 1000);
    }

    private void cancelDownload() {
        if (id != 0) {
            downloadManager.remove(id);
        }
        viewBind.btnStart.setClickable(true);
        timer.cancel();
        viewBind.textProgress.setText("");
        viewBind.progressBar.setProgress(0);
    }

    private void queryProgress() {
        Log.e(TAG, "queryProgress: ");
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
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status != DownloadManager.STATUS_FAILED) {
                    int progress = bytesDownload * 100 / bytesTotal;
                    Message message = Message.obtain(handler, WHAT_PROGRESS);
                    message.arg1 = progress;
                    message.sendToTarget();
                } else {
                    int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                    Message message = Message.obtain(handler, WHAT_REASON);
                    message.arg1 = reason;
                    message.sendToTarget();
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void setDownloadProgress(int progress) {
        viewBind.progressBar.setProgress(progress);
        viewBind.textProgress.setText(String.valueOf(progress) + "%");
        if (progress == 100) {
            timer.cancel();
            install(downloadPath);
        }
    }

    public void toastError(int reason) {
        ToastUtil.toast("Download error:" + reason);
        cancelDownload();
    }

    private void install(String path) {
        Uri uri;
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //如果是7.0以上的系统，要使用FileProvider的方式构建Uri
            uri = FileProvider.getUriForFile(this, "com.hm.retrofitrxjavademo.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
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
                Toast.makeText(this, "没有相应的权限，无法进行下载", Toast.LENGTH_SHORT).show();
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
        if (timer != null)
            timer.cancel();
    }

    /**
     * 自定义静态内部类
     */
    private static class MyHandler extends Handler {

        WeakReference<DownloadManagerActivity> weakReference;

        public MyHandler(DownloadManagerActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DownloadManagerActivity activity = weakReference.get();

            switch (msg.what) {
                case WHAT_PROGRESS:
                    activity.setDownloadProgress(msg.arg1);
                    break;
                case WHAT_REASON:
                    activity.toastError(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    }

}
