package com.hm.retrofitrxjavademo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.pausecontinuedownload.ProgressDownloader;
import com.hm.retrofitrxjavademo.pausecontinuedownload.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PauseContinueActivity extends AppCompatActivity implements ProgressListener {

    private final String TAG = getClass().getSimpleName();
    private String downLoadUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/5D7CD21498D9433BD2F362BF06068C07.apk?mkey=58d2100bacc7802a&f=e381&c=0&fsname=com.moji.mjweather_6.0209.02_6020902.apk&csr=1bbd&p=.apk";
    private long breakPoints;
    private ProgressDownloader downloader;
    private File file;
    private long totalBytes;
    private long contentLength;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_continue);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        dialog.setProgressNumberFormat("%1d KB %2d KB");
        dialog.setTitle("下载");
        dialog.setMessage("正在下载，请稍后...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.btn_start, R.id.btn_pause, R.id.btn_continue, R.id.btn_randomAccessFile})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                // 新下载前清空断点信息
                breakPoints = 0L;
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sample.apk");
                downloader = new ProgressDownloader(downLoadUrl, file, this);
                downloader.download(0L);
                dialog.show();
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
            case R.id.btn_randomAccessFile:
                testRandomAccessFile();
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
            dialog.setMax((int) (contentLength / 1024));
        }
    }

    @Override
    public void update(long totalBytes, boolean done) {
        // 注意加上断点的长度
        this.totalBytes = totalBytes + breakPoints;
        dialog.setProgress((int) (totalBytes + breakPoints) / 1024);
        if (done) {
            // 切换到主线程
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    installApk(file);
                    Toast.makeText(PauseContinueActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);

    }

    public File createImageFile() {
        File file = null;
        File dir;
        try {
            dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).getPath());
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

    public void testRandomAccessFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "testRandomAccess.txt");
        RandomAccessFile randomAccessFile;
        FileChannel channel;
        MappedByteBuffer mappedByteBuffer;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.writeUTF("h");
            Log.e(TAG, " randomAccessFile.getFilePointer()=" + randomAccessFile.getFilePointer());
            randomAccessFile.close();
           /* channel = randomAccessFile.getChannel();
            int size = 1024 * 8 * 8;
            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
            for (int i = 0; i < size; i++) {
                mappedByteBuffer.put((byte) 'h');
            }
            mappedByteBuffer.force();
            for (int i = size / 2; i < size / 2 + 6; i++) {
                Log.e(TAG, "VALUE=" + (char) mappedByteBuffer.get(i));
            }*/
            //channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
