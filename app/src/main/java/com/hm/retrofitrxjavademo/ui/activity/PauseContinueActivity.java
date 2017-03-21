package com.hm.retrofitrxjavademo.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.pausecontinuedownload.ProgressDownloader;
import com.hm.retrofitrxjavademo.pausecontinuedownload.ProgressListener;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PauseContinueActivity extends AppCompatActivity implements ProgressListener {

    private final String TAG = getClass().getSimpleName();
    private String downLoadUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/20AD322F5D49B9F649A70C4A3083D8D2.apk?mkey=58758c694bc7812a&f=d588&c=0&fsname=com.xunao.wanfeng_1.1_4.apk&csr=4d5s&p=.apk";
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private long breakPoints;
    private ProgressDownloader downloader;
    private File file;
    private long totalBytes;
    private long contentLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_continue);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_start, R.id.btn_pause, R.id.btn_continue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                // 新下载前清空断点信息
                breakPoints = 0L;
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sample.apk");
                downloader = new ProgressDownloader(downLoadUrl, file, this);
                downloader.download(0L);
                break;
            case R.id.btn_pause:
                downloader.pause();
                Toast.makeText(this, "下载暂停", Toast.LENGTH_SHORT).show();
                // 存储此时的totalBytes，即断点位置。
                breakPoints = totalBytes;
                break;
            case R.id.btn_continue:
                downloader.download(breakPoints);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPreExecute(long contentLength) {
        // 文件总长只需记录一次，要注意断点续传后的contentLength只是剩余部分的长度
        if (this.contentLength == 0L) {
            this.contentLength = contentLength;
            progressBar.setMax((int) (contentLength / 1024));
        }
    }

    @Override
    public void update(long totalBytes, boolean done) {
        // 注意加上断点的长度
        this.totalBytes = totalBytes + breakPoints;
        progressBar.setProgress((int) (totalBytes + breakPoints) / 1024);
        if (done) {
            // 切换到主线程
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PauseContinueActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public File createImageFile() {
        File file = null;
        File dir;
        try {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
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
}
