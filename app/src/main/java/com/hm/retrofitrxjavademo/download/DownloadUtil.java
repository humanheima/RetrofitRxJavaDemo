package com.hm.retrofitrxjavademo.download;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.util.IOUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dumingwei on 2018/2/1 0001.
 */

public class DownloadUtil implements Handler.Callback {

    private static final int DOWNLOAD_PROGRESS = 2018;
    private final String TAG = getClass().getName();
    private ProgressBean progressBean;
    private DownLoadProgressListener progressListener;
    private Handler mHandler;
    private DownloadApi downloadApi;
    private File downLoadFile;
    private ProgressDialog dialog;
    private Context context;
    private DownloadCallback callback;
    //用于取消订阅
    private Disposable disposable;

    public static DownloadUtil newInstance(Activity context, DownloadCallback callback) {
        return new DownloadUtil(context, callback);
    }

    private DownloadUtil(Context context, DownloadCallback callback) {
        this.context = context;
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        this.callback = callback;
        createDialog(this.context);
        progressBean = new ProgressBean();
        if (null == downloadApi) {
            createApi();
        }
    }

    private void createDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setProgressNumberFormat(null);
        dialog.setTitle(context.getString(R.string.download));
        dialog.setMessage(context.getString(R.string.wait_download_finish));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel_download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (null != disposable && !disposable.isDisposed()) {
                    disposable.dispose();
                }
            }
        });
    }

    private void createApi() {
        progressListener = new DownLoadProgressListener() {
            //这个方法在子线程中运行
            @Override
            public void onProgress(long progress, long total, boolean done) {
                Log.d("progress:", String.format("%d%% done\n", (100 * progress) / total));
                progressBean.setBytesRead(progress);
                progressBean.setContentLength(total);
                progressBean.setDone(done);
                mHandler.obtainMessage(DOWNLOAD_PROGRESS, progressBean).sendToTarget();
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originResponse = chain.proceed(chain.request());
                        return originResponse.newBuilder()
                                .body(new ProgressResponseBody(progressListener, originResponse.body()))
                                .build();
                    }
                }).build();
        downloadApi = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://msoftdl.360.cn")
                .build()
                .create(DownloadApi.class);
    }

    public void download(String downLoadUrl) {
        downLoadFile = createDownloadFile();
        dialog.show();
        downloadApi.downLoad(downLoadUrl)
                .map(new Function<ResponseBody, Boolean>() {
                    @Override
                    public Boolean apply(ResponseBody responseBody) throws Exception {
                        return saveToDisk(responseBody);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Boolean saveSucceed) {
                        dialog.dismiss();
                        if (saveSucceed) {
                            callback.onSuccess(downLoadFile);
                        } else {
                            callback.onFailed();
                            Log.e(TAG, "保存文件失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: " + e);
                        callback.onFailed();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    private File createDownloadFile() {
        File file = null;
        File dir;
        try {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = File.createTempFile("download", ".apk", dir);
            Log.e("createDownloadFile", file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("createDownloadFile", e.getMessage());
        }
        return file;
    }

    /**
     * 把下载的文件保存到本地
     *
     * @param responseBody
     * @return
     */
    private Boolean saveToDisk(ResponseBody responseBody) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        long totalLength;
        try {
            totalLength = responseBody.contentLength();
            Log.e(TAG, "totalLength=" + totalLength);
            bis = new BufferedInputStream(responseBody.byteStream());
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            int byteRead;
            while ((byteRead = bis.read()) != -1) {
                bos.write(byteRead);
            }
            bos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("saveToDisk", "saveToDisk error" + e.getMessage());
            return false;
        } finally {
            IOUtil.closeAll(bis, bos);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.e(TAG, "handleMessage: Thread.currentThread is:" + Thread.currentThread().getName());
        switch (msg.what) {
            case DOWNLOAD_PROGRESS:
                ProgressBean bean = (ProgressBean) msg.obj;
                long bytesRead = bean.getBytesRead();
                long contentLength = bean.getContentLength();
                Log.e("handleProgressMessage", String.format("%d%% done\n", (100 * bytesRead) / contentLength));
                boolean done = bean.isDone();
                Log.e("done", "--->" + String.valueOf(done));
                dialog.setProgress((int) ((100 * bytesRead) / contentLength));
                if (done) {
                    dialog.setMessage("下载成功");
                }
                break;
            default:
                break;
        }
        return false;
    }

}
