package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityOkHttp3DownloadBinding;
import com.hm.retrofitrxjavademo.okhttpdownload.DownLoadObserver;
import com.hm.retrofitrxjavademo.okhttpdownload.DownloadInfo;
import com.hm.retrofitrxjavademo.okhttpdownload.DownloadManager;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import com.hm.retrofitrxjavademo.util.NetSpeedUtils;
import com.hm.retrofitrxjavademo.util.NetSpeedUtils.NetSpeedCallback;
import com.hm.retrofitrxjavademo.util.ToastUtil;
import java.io.File;

/**
 * Created by p_dmweidu on 2023/9/20
 * Desc: 下载文件
 */
public class OkHttpDownloadActivity extends BaseActivity<ActivityOkHttp3DownloadBinding> {


    private TextView tv_speed = null;

    private String wifiUrl = "https://downloadxx.yuewen.com/xiaoxiang/apknew/source/300000010.apk";
    private String bookUrl = "https://downloadxx.yuewen.com/xiaoxiang/apknew/source/300000010.apk";
    private String weatherUrl = "https://downloadxx.yuewen.com/xiaoxiang/apknew/source/300000010.apk";

    public static void launch(Context context) {
        Intent intent = new Intent(context, OkHttpDownloadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_ok_http3_download;
    }

    @Override
    protected void initData() {
        tv_speed = findViewById(R.id.tv_speed);

        NetSpeedUtils.INSTANCE.setNetSpeedCallback(new NetSpeedCallback() {
            @Override
            public void onNetSpeedChange(@NonNull String downloadSpeed, @NonNull String uploadSpeed) {
                tv_speed.setText("下载速度：" + downloadSpeed + "，上传速度：" + uploadSpeed);
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                NetSpeedUtils.INSTANCE.startMeasuringNetSpeed(this);
                break;

            case R.id.btn_stop:
                NetSpeedUtils.INSTANCE.stopMeasuringNetSpeed();
                break;

            case R.id.main_btn_down1:
                DownloadManager.getInstance().downLoad(wifiUrl, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        long total = value.getTotal();
                        viewBind.mainProgress1.setMax((int) total);
                        long progress = value.getProgress();
                        viewBind.mainProgress1.setProgress((int) progress);

                        long left = (total - progress) / 1024;
                        long downloadSpeed = NetSpeedUtils.INSTANCE.getDownloadSpeed();
                        if (downloadSpeed > 0) {
                            long time = left / downloadSpeed;
                            Log.d(TAG, "onNext:" + convertSecondsToHMS(time));
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        ToastUtil.toast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(OkHttpDownloadActivity.this,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                            if (downloadInfo.getFullPath().endsWith(".apk")) {
                                installApk(downloadInfo.getFullPath());
                            }
                        }
                    }
                });
                break;
            case R.id.main_btn_cancel1:
                DownloadManager.getInstance().cancel(wifiUrl);
                break;
            case R.id.main_btn_down2:
                DownloadManager.getInstance().downLoad(bookUrl, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        viewBind.mainProgress2.setMax((int) value.getTotal());
                        viewBind.mainProgress2.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        ToastUtil.toast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(OkHttpDownloadActivity.this,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                            if (downloadInfo.getFullPath().endsWith(".apk")) {
                                installApk(downloadInfo.getFullPath());
                            }
                        }
                    }
                });
                break;
            case R.id.main_btn_cancel2:
                DownloadManager.getInstance().cancel(bookUrl);
                break;
            case R.id.main_btn_down3:
                DownloadManager.getInstance().downLoad(weatherUrl, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        viewBind.mainProgress3.setMax((int) value.getTotal());
                        viewBind.mainProgress3.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                        ToastUtil.toast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(OkHttpDownloadActivity.this,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                            if (downloadInfo.getFullPath().endsWith(".apk")) {
                                installApk(downloadInfo.getFullPath());
                            }
                        }
                    }
                });
                break;
            case R.id.main_btn_cancel3:
                DownloadManager.getInstance().cancel(weatherUrl);
                break;
            default:
                break;
        }
    }

    private void installApk(String path) {
        Log.e(TAG, "installApk: path=" + path);
        Uri uri;
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //如果是7.0以上的系统，要使用FileProvider的方式构建Uri
            uri = FileProvider.getUriForFile(this, "com.hm.retrofitrxjavademo.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            Log.e(TAG, "installApk: Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    private String convertSecondsToHMS(long seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int remainingSeconds = (int) (seconds % 60);

        return String.format("剩余%02d小时%02d分钟%02d秒", hours, minutes, remainingSeconds);
    }

}
