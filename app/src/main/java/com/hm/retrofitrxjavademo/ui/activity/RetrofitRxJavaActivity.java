package com.hm.retrofitrxjavademo.ui.activity;

import android.annotation.SuppressLint;
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
import com.hm.retrofitrxjavademo.model.NowWeather;
import com.hm.retrofitrxjavademo.model.NowWeatherPM25;
import com.hm.retrofitrxjavademo.model.PM25;
import com.hm.retrofitrxjavademo.network.NetWork;
import com.hm.retrofitrxjavademo.network.api_entity.HistoryWeatherEntity;
import com.hm.retrofitrxjavademo.network.api_entity.NowWeatherEntity;
import com.hm.retrofitrxjavademo.network.api_entity.PM25Entity;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import com.hm.retrofitrxjavademo.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@SuppressLint("CheckResult")

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

    /**
     * 使用compose复用操作符的例子
     * {@link NetWork#applySchedulers()}
     */
    public void getNowWeather(View view) {
        //"http://api.k780.com:88/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        showLoading();
        DisposableObserver<NowWeather> observer = newObserver(new Consumer<NowWeather>() {

            @Override
            public void accept(NowWeather bean) {
                viewBind.textWeatherResult.setText(bean.getCitynm());
            }
        });
        NetWork.getData(new NowWeatherEntity("weather.today", 1,
                "10003", "b59bc3ef6191eb9f747dd4e83c99f2a4", "json"), NowWeather.class)
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
                        100, "2018-06-12", "10003", "b59bc3ef6191eb9f747dd4e83c99f2a4",
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

    private void uploadFile(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        NetWork.getUpLoadFileApi().uploadFile(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        Log.d(TAG, "accept: result:" + result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void uploadFileWithRealName(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        NetWork.getUpLoadFileApi().uploadFileWithRealName(part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        Log.d(TAG, "accept: result:" + result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void uploadSingleFileWithDescription(File file) {
        // MultipartBody.Part is used to send also the actual file name
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
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


    /**
     * @param files
     */
    private void uploadMultiFile(List<File> files) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        for (File file : files) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
        }

        NetWork.getUpLoadFileApi().uploadMultiFile(partList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.e(TAG, s),
                        e -> Log.e(TAG, e.getMessage()));
    }


    private static final String BASE_URL = "http://api.k780.com";


    /**
     * 测试合并多个网络请求
     *
     * @param view
     */

    public void testZipMultiNetworkResponse(View view) {

        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onError(new NullPointerException("Throw NullPointerException"));
            }
        });
        //"http://api.k780.com:88/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        Observable<NowWeather> observable1 = NetWork.getData(new NowWeatherEntity("weather.today", 1,
                "10003", "b59bc3ef6191eb9f747dd4e83c99f2a4", "json"), NowWeather.class);
        //http://api.k780.com:88/?app=weather.pm25&weaid=1&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json
        Observable<PM25> observable2 = NetWork.getData(new PM25Entity("weather.pm25", 1,
                "10003", "b59bc3ef6191eb9f747dd4e83c99f2a4", "json"), PM25.class);

        /*Observable.zip(observable1, observable2,
                new BiFunction<NowWeather, PM25, NowWeatherPM25>() {
                    @Override
                    public NowWeatherPM25 apply(NowWeather nowWeather, PM25 pm25) throws Exception {
                        return new NowWeatherPM25(nowWeather, pm25);
                    }
                }).subscribe(new Observer<NowWeatherPM25>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NowWeatherPM25 nowWeatherPM25) {
                Log.d(TAG, "onNext: " + nowWeatherPM25.getNowWeather().getCitynm());
                Log.d(TAG, "onNext: " + nowWeatherPM25.getPm25().getAqi());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });*/

        Observable.zip(observable, observable1, observable2,
                new Function3<Integer, NowWeather, PM25, NowWeatherPM25>() {
                    @Override
                    public NowWeatherPM25 apply(Integer integer, NowWeather nowWeather, PM25 pm25) throws Exception {
                        return new NowWeatherPM25(nowWeather, pm25);
                    }
                }).subscribe(new Observer<NowWeatherPM25>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(NowWeatherPM25 nowWeatherPM25) {
                Log.d(TAG, "onNext: " + nowWeatherPM25.getNowWeather().getCitynm());
                Log.d(TAG, "onNext: " + nowWeatherPM25.getPm25().getAqi());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });

    }

}
