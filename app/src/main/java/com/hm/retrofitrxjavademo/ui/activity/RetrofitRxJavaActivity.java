package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityRetrofitRxJavaBinding;
import com.hm.retrofitrxjavademo.download.DownloadCallback;
import com.hm.retrofitrxjavademo.download.DownloadUtil;
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
import com.hm.retrofitrxjavademo.util.ToastUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RetrofitRxJavaActivity extends BaseActivity<ActivityRetrofitRxJavaBinding> {

    private static final String TAG = "RetrofitRxJavaActivity";

    private String downLoadUrl = "http://imtt.dd.qq.com/16891/7595C75AAF71D6B65596B3A99956062C.apk?fsname=com.snda.wifilocating_4.2.53_3183.apk";

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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                downloadApk(downLoadUrl);
                break;
            default:
                break;
        }
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

    private void downloadApk(String url) {
        DownloadUtil.newInstance(this, new DownloadCallback() {
            @Override
            public void onSuccess(File file) {
                installApk(file);
            }

            @Override
            public void onFailed() {
                ToastUtil.toast("downloadFiled");
            }
        }).download(url);
    }

    private void installApk(File file) {
        Uri uri;
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

}
