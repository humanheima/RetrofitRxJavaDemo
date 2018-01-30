package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityOkHttp3DownloadBinding;
import com.hm.retrofitrxjavademo.okhttpdownload.DownLoadObserver;
import com.hm.retrofitrxjavademo.okhttpdownload.DownloadInfo;
import com.hm.retrofitrxjavademo.okhttpdownload.DownloadManager;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.io.File;


public class OkHttp3DownloadActivity extends BaseActivity<ActivityOkHttp3DownloadBinding> {

    private String wifiUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/DF6B2FB4A4628C2870C710046C231348.apk?mkey=58d4b294acc7802a&f=8e5d&c=0&fsname=com.snda.wifilocating_4.1.88_3108.apk&csr=1bbd&p=.apk";
    private String bookUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/88CB555BDF39D8731913D084F9A9C848.apk?mkey=58d4b256acc7802a&f=ac0b&c=0&fsname=com.qq.reader_6.3.9.888_96.apk&csr=1bbd&p=.apk";
    private String weatherUrl = "http://112.65.222.35/imtt.dd.qq.com/16891/A46129A45C18D0F7D8C833904621CADD.apk?mkey=58d4b3d0acc7802a&f=ac0b&c=0&fsname=com.moji.mjweather_6.0209.02_6020902.apk&csr=1bbd&p=.apk";

    public static void launch(Context context) {
        Intent intent = new Intent(context, OkHttp3DownloadActivity.class);
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
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(OkHttp3DownloadActivity.this,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
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
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(OkHttp3DownloadActivity.this,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                            installApk(new File(downloadInfo.getFileName()));
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
                    public void onComplete() {
                        if (downloadInfo != null) {
                            Toast.makeText(OkHttp3DownloadActivity.this,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_cancel3:
                DownloadManager.getInstance().cancel(weatherUrl);
                break;
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
