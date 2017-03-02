package com.hm.retrofitrxjavademo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.download.DownLoadProgressListener;
import com.hm.retrofitrxjavademo.download.DownloadApi;
import com.hm.retrofitrxjavademo.download.ProgressBean;
import com.hm.retrofitrxjavademo.download.ProgressHandler;
import com.hm.retrofitrxjavademo.download.ProgressResponseBody;
import com.hm.retrofitrxjavademo.network.HttpResult;
import com.hm.retrofitrxjavademo.model.MovieEntity;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;
import com.hm.retrofitrxjavademo.network.NetWork;
import com.hm.retrofitrxjavademo.widget.LoadingDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RetrofitActivity extends AppCompatActivity {

    @BindView(R.id.text_movie_result)
    TextView textMovieResult;
    @BindView(R.id.activity_retrofit)
    ScrollView activityRetrofit;
    private String tag = getClass().getSimpleName();
    @BindView(R.id.btn_now_weather)
    Button btnNowWeather;
    @BindView(R.id.text_result)
    TextView textResult;
    private Map<String, Object> map;
    private LoadingDialog loadingDialog;
    private String downLoadUrl = "http://140.207.247.205/imtt.dd.qq.com/16891/20AD322F5D49B9F649A70C4A3083D8D2.apk?mkey=58758c694bc7812a&f=d588&c=0&fsname=com.xunao.wanfeng_1.1_4.apk&csr=4d5s&p=.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        ButterKnife.bind(this);
        loadingDialog = new LoadingDialog(this);

    }

    @OnClick(R.id.btn_now_weather)
    public void onClick() {
        //getMovie();
        //downLoadWeChat(downLoadUrl);
        retrofitDownload();
    }

    private void getWeather() {
        map = new HashMap();
        //"http://api.k780.com:88/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        map.put("app", "weather.today");
        map.put("weaid", 1);
        map.put("appkey", "15732");
        map.put("sign", "bf10378fb5e93259d0a94f2423fa81e5");
        NetWork.getApi().getNowWeather(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NowWeatherBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(RetrofitActivity.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(NowWeatherBean nowWeatherBean) {
                        textResult.setText(nowWeatherBean.getResult().getCitynm());
                        Log.e(tag, nowWeatherBean.getResult().getCitynm());
                    }
                });
    }

    private void test() {
        NetWork.getApi().testNowWeather(map)
                .flatMap(new Func1<HttpResult<NowWeatherBean>, Observable<NowWeatherBean>>() {
                    @Override
                    public Observable<NowWeatherBean> call(HttpResult<NowWeatherBean> result) {
                        return NetWork.flatResponse(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NowWeatherBean>() {
                    @Override
                    public void call(NowWeatherBean nowWeatherBean) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    //进行网络请求
    private void getMovie() {

        NetWork.getApi().getTopMovie(0, 2)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        loadingDialog.show();
                    }
                })
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        loadingDialog.dismiss();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MovieEntity>() {
                    @Override
                    public void call(MovieEntity movieEntity) {
                        textMovieResult.setText(movieEntity.getTitle());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        textMovieResult.setText(throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(RetrofitActivity.this, "Get Top Movie Completed", Toast.LENGTH_SHORT).show();
                    }
                });
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
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });


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
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });


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
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });


    }


    private void downLoadWeChat(String downLoadUrl) {
        NetWork.getApi().downloadFile(downLoadUrl)
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, Boolean>() {
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
                            Log.e(tag, "保存成功");
                        } else {
                            Log.e(tag, "保存失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(tag, "保存失败" + throwable.getMessage());
                        Toast.makeText(RetrofitActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private ProgressDialog dialog;
    private ProgressBean progressBean;
    private ProgressHandler mProgressHandler;
    private DownLoadProgressListener progressListener;
    private File downLoadFile;

    private void retrofitDownload() {
        downLoadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "wanfeng.apk");
        dialog = new ProgressDialog(this);
        dialog.setProgressNumberFormat("%1d KB %2d KB");
        dialog.setTitle("下载");
        dialog.setMessage("正在下载，请稍后...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);


        progressBean = new ProgressBean();

        mProgressHandler = new ProgressHandler(Looper.getMainLooper()) {
            @Override
            protected void handleProgressMessage(long bytesRead, long contentLength, boolean done) {
                Log.e("是否在主线程中运行", String.valueOf(Looper.getMainLooper() == Looper.myLooper()));
                Log.e("handleProgressMessage", String.format("%d%% done\n", (100 * bytesRead) / contentLength));
                Log.e("done", "--->" + String.valueOf(done));
                dialog.setMax((int) (contentLength / 1024));
                dialog.setProgress((int) (bytesRead / 1024));
                if (done) {
                    //dialog.dismiss();
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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://msoftdl.360.cn")
                .build()
                .create(DownloadApi.class);

        dialog.show();
        downloadApi.downLoad()
                .subscribeOn(Schedulers.io())//不能在主线程下载
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(tag, "downLoad error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        if (saveToDisk(response)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    installApk(downLoadFile);
                                }
                            });
                        } else {
                            Log.e(tag, "保存文件失败");
                        }
                    }
                });
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

    /**
     * 把下载的文件保存到本地
     *
     * @param responseBody
     * @return
     */
    private Boolean saveToDisk(ResponseBody responseBody) {

        OutputStream out;
        InputStream in;
        BufferedInputStream bis = null;
        BufferedOutputStream bo = null;
        long totalLength;

        try {
            totalLength = responseBody.contentLength();
            Log.e(tag, "totalLength=" + totalLength);
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bo != null) {
                    bo.close();
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

}