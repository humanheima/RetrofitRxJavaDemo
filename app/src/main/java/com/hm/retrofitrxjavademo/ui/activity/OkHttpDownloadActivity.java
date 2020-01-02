package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityOkHttp3DownloadBinding;
import com.hm.retrofitrxjavademo.okhttpdownload.DownLoadObserver;
import com.hm.retrofitrxjavademo.okhttpdownload.DownloadInfo;
import com.hm.retrofitrxjavademo.okhttpdownload.DownloadManager;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import com.hm.retrofitrxjavademo.util.ToastUtil;

import java.io.File;


public class OkHttpDownloadActivity extends BaseActivity<ActivityOkHttp3DownloadBinding> {

    private String wifiUrl = "http://imtt.dd.qq.com/16891/7595C75AAF71D6B65596B3A99956062C.apk?fsname=com.snda.wifilocating_4.2.53_3183.apk";
    private String bookUrl = "http://imtt.dd.qq.com/16891/11963AA5E2A9C91F41D2F09C1FD3496C.apk?fsname=com.ss.android.article.news_6.5.7_657.apk";
    private String weatherUrl = "http://imtt.dd.qq.com/16891/BCF8513AC8C0F123EEF46B91F46004CA.apk?fsname=com.baidu.news_7.0.3.0_7030.apk";

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
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_btn_down1:
                DownloadManager.getInstance().downLoad(wifiUrl, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        viewBind.mainProgress1.setMax((int) value.getTotal());
                        viewBind.mainProgress1.setProgress((int) value.getProgress());
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

}
