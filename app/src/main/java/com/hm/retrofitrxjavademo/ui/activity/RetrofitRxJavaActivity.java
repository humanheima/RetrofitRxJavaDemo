package com.hm.retrofitrxjavademo.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityRetrofitRxJavaBinding;
import com.hm.retrofitrxjavademo.download.DownLoadProgressListener;
import com.hm.retrofitrxjavademo.download.DownloadApi;
import com.hm.retrofitrxjavademo.download.ProgressBean;
import com.hm.retrofitrxjavademo.download.ProgressHandler;
import com.hm.retrofitrxjavademo.download.ProgressResponseBody;
import com.hm.retrofitrxjavademo.model.HistoryWeatherBean;
import com.hm.retrofitrxjavademo.model.MovieBean;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;
import com.hm.retrofitrxjavademo.model.PM25;
import com.hm.retrofitrxjavademo.network.NetWork;
import com.hm.retrofitrxjavademo.network.api_entity.HistoryWeatherEntity;
import com.hm.retrofitrxjavademo.network.api_entity.MovieEntity;
import com.hm.retrofitrxjavademo.network.api_entity.NowWeatherEntity;
import com.hm.retrofitrxjavademo.network.api_entity.PM25Entity;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRxJavaActivity extends BaseActivity<ActivityRetrofitRxJavaBinding> {

    private HashMap<String, Object> map;
    // private String downLoadUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/20AD322F5D49B9F649A70C4A3083D8D2.apk?mkey=58758c694bc7812a&f=d588&c=0&fsname=com.xunao.wanfeng_1.1_4.apk&csr=4d5s&p=.apk";
    private String downLoadUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/5D7CD21498D9433BD2F362BF06068C07.apk?mkey=58d2100bacc7802a&f=e381&c=0&fsname=com.moji.mjweather_6.0209.02_6020902.apk&csr=1bbd&p=.apk";

    public static void launch(Context context) {
        Intent intent = new Intent(context, RetrofitRxJavaActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_retrofit_rx_java;
    }

    @Override
    protected void initData() {

    }

    public void onClick() {
        //downLoadWeChat(downLoadUrl);
        //retrofitDownload();
    }

    public void getMovie(View view) {
        showLoading();
        compositeDisposable.add(NetWork.getDataList(new MovieEntity(1, 1), MovieBean.class)
                .subscribeWith(newObserver(new Consumer<List<MovieBean>>() {
                    @Override
                    public void accept(List<MovieBean> movieBeans) throws Exception {
                       viewBind.textMovieResult.setText(movieBeans.toString());
                    }
                })));
    }

    /**
     * 使用compose复用操作符的例子
     * {@link NetWork#applySchedulers()}
     */
    public void getNowWeather(View view) {
        //"http://api.k780.com:88/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        showLoading();
        DisposableObserver<NowWeatherBean> observer = newObserver(new Consumer<NowWeatherBean>() {
            @Override
            public void accept(NowWeatherBean bean) throws Exception {
                viewBind.textWeatherResult.setText(bean.toString());
            }
        });
        NetWork.getData(new NowWeatherEntity("weather.today", 1,
                "10003", "b59bc3ef6191eb9f747dd4e83c99f2a4", "json"), NowWeatherBean.class)
                .subscribe(observer);
        compositeDisposable.add(observer);
    }

    public void getPM(View view) {
        //http://api.k780.com:88/?app=weather.pm25&weaid=1&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json
        showLoading();
        DisposableObserver<PM25> observer = newObserver(new Consumer<PM25>() {
            @Override
            public void accept(PM25 bean) throws Exception {
                viewBind.textPmResult.setText(bean.toString());
            }
        });
        NetWork.getData(new PM25Entity("weather.pm25", 1,
                "10003", "b59bc3ef6191eb9f747dd4e83c99f2a4", "json"), PM25.class)
                .subscribe(observer);
        compositeDisposable.add(observer);
    }

    public void getHistoryWeather(View view) {
        showLoading();
        compositeDisposable.add(
                NetWork.getDataList(new HistoryWeatherEntity("weather.history",
                        100, "2018-01-30", "10003", "b59bc3ef6191eb9f747dd4e83c99f2a4",
                        "json"), HistoryWeatherBean.class)
                        .subscribeWith(newObserver(new Consumer<List<HistoryWeatherBean>>() {
                            @Override
                            public void accept(List<HistoryWeatherBean> beans) throws Exception {
                                viewBind.textHistoryWeatherResult.setText(beans.toString());
                            }
                        }))
        );
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void uploadSingleFile(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        // 添加描述
        String descriptionString = "hello, 这是文件描述";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        NetWork.getUpLoadFileApi().uploadSingleFile(description, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.e(TAG, s),
                        e -> Log.e(TAG, e.getMessage()));
    }

    private void uploadMulFile(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        // 添加描述
        String descriptionString = "hello, 这是文件描述";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        NetWork.getUpLoadFileApi().uploadSingleFile(description, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.e(TAG, s),
                        e -> Log.e(TAG, e.getMessage()));
    }

    private void uploadManyFlie(File file1, File file2) {
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);

        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("file1", requestBody1);
        requestBodyMap.put("file2", requestBody2);

        NetWork.getUpLoadFileApi().uploadManyFile(requestBodyMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.e(TAG, s),
                        e -> Log.e(TAG, e.getMessage()));
    }

    private static final String TAG = "RetrofitRxJavaActivity";

    private void downLoadWeChat(String downLoadUrl) {
        NetWork.getApi().downloadFile(downLoadUrl)
                .subscribeOn(Schedulers.io())
                .map(this::saveToDisk)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(b -> {
                    if (b) {
                        Log.e(TAG, "保存成功");
                    } else {
                        Log.e(TAG, "保存失败");
                    }
                }, e -> Log.e(TAG, e.getMessage()));
               /* .map(new Func1<ResponseBody, Boolean>() {
                    @Override
                    public Boolean call(ResponseBody responseBody) {
                        //保存到本地
                        return saveToDisk(responseBody);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            Log.e(TAG, "保存成功");
                        } else {
                            Log.e(TAG, "保存失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "保存失败" + throwable.getMessage());
                        Toast.makeText(RetrofitRxJavaActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                });*/

    }

    private ProgressDialog dialog;
    private ProgressBean progressBean;
    private ProgressHandler mProgressHandler;
    private DownLoadProgressListener progressListener;
    private File downLoadFile;

    private void retrofitDownload() {
        downLoadFile = createImageFile();
        dialog = new ProgressDialog(this);
        dialog.setProgressNumberFormat("%1d KB %2d KB");
        dialog.setTitle("下载");
        dialog.setMessage("正在下载，请稍后...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (dialog, which) -> dialog.dismiss());

        progressBean = new ProgressBean();

        mProgressHandler = new ProgressHandler(Looper.getMainLooper()) {
            @Override
            protected void handleProgressMessage(long bytesRead, long contentLength, boolean done) {
                Log.e("handleProgressMessage", String.format("%d%% done\n", (100 * bytesRead) / contentLength));
                Log.e("done", "--->" + String.valueOf(done));
                dialog.setMax((int) (contentLength / 1024));
                dialog.setProgress((int) (bytesRead / 1024));
                if (done) {
                    dialog.setMessage("下载成功");
                }
            }
        };

        progressListener = new DownLoadProgressListener() {

            //这个方法在子线程中运行
            @Override
            public void onProgress(long progress, long total, boolean done) {
                Log.d("progress:", String.format("%d%% done\n", (100 * progress) / total));
                progressBean.setBytesRead(progress);
                progressBean.setContentLength(total);
                progressBean.setDone(done);
                mProgressHandler.sendMessage(progressBean);
            }
        };


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originResponse = chain.proceed(chain.request());
                return originResponse.newBuilder()
                        .body(new ProgressResponseBody(progressListener, originResponse.body()))
                        .build();
            }
        }).build();

        DownloadApi downloadApi = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://msoftdl.360.cn")
                .build()
                .create(DownloadApi.class);

        dialog.show();
        downloadApi.downLoad(downLoadUrl)
                .subscribeOn(Schedulers.io())//不能在主线程下载
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        if (saveToDisk(responseBody)) {
                            runOnUiThread(() -> {
                                dialog.dismiss();
                                installApk(downLoadFile);
                            });
                        } else {
                            Log.e(TAG, "保存文件失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: error:" + throwable);
                        dialog.dismiss();
                    }
                });
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

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * 把下载的文件保存到本地
     *
     * @param responseBody
     * @return
     */
    private Boolean saveToDisk(ResponseBody responseBody) {

        OutputStream out;
        InputStream in;
        BufferedInputStream bis;
        BufferedOutputStream bo;
        long totalLength;

        try {
            totalLength = responseBody.contentLength();
            Log.e(TAG, "totalLength=" + totalLength);
            in = responseBody.byteStream();
            bis = new BufferedInputStream(in);
            out = new FileOutputStream(downLoadFile);
            bo = new BufferedOutputStream(out);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bo.write(buffer, 0, len);
                bo.flush();
            }
            bis.close();
            bo.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("saveToDisk", "saveToDisk error" + e.getMessage());
            return false;
        }
    }
}
