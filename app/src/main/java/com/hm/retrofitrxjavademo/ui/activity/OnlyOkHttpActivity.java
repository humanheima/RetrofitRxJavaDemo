package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import com.hm.retrofitrxjavademo.BuildConfig;
import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityOnlyOkHttpBinding;
import com.hm.retrofitrxjavademo.intercepter.HttpLoggingInterceptor;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by dmw on 2018/8/31.
 * Desc:
 * todo 测试一下拦截器
 */
public class OnlyOkHttpActivity extends BaseActivity<ActivityOnlyOkHttpBinding> {

    private OkHttpClient client;

    private HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    private OkHttpClient.Builder builder;

    public static void launch(Context context) {
        Intent intent = new Intent(context, OnlyOkHttpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_only_ok_http;
    }

    @Override
    protected void initData() {

        Looper.getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {

            }
        });

        builder = new OkHttpClient.Builder()
                //.addInterceptor(new LoggingInterceptor())
                //.addNetworkInterceptor(new LoggingInterceptor())
                //.addInterceptor(interceptor)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(10000, TimeUnit.MILLISECONDS);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "instance initializer: ");
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        client = builder
                .build();

        viewBind.btnAppInterceptor.setOnClickListener(v -> {

                    Request request = new Request.Builder()
                            .url("http://www.publicobject.com/helloworld.txt")
                            .header("User-Agent", "OkHttp Example")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, "onResponse: " + response.message());
                        }
                    });
                }
        );

        viewBind.btnNetInterceptor.setOnClickListener(v -> {
            Request request = new Request.Builder()
                    .url("http://www.publicobject.com/helloworld.txt")
                    .header("User-Agent", "OkHttp Example")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "onResponse: " + response.message());
                }
            });
        });
    }

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());
                }
            }
        });
    }

    /**
     * 表单提交
     *
     * @throws Exception
     */
    public void postForm() throws Exception {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .post(new FormBody.Builder()
                        .add("name", "dumingwei")
                        .build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());
                }
            }
        });
    }

    /**
     * 上传文件
     *
     * @throws Exception
     */
    public void postFile() throws Exception {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");

        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .post(RequestBody.create(mediaType, new File("file path")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());
                }
            }
        });
    }

    /**
     * 上传Multipart文件
     *
     * @throws Exception
     */
    public void postMultipartFile() throws Exception {
        MediaType mediaType = MediaType.parse("image/png");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "dumingwei")
                .addFormDataPart("image", "image.png", RequestBody.create(mediaType, new File("image.png")))
                .build();
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    System.out.println(responseBody.string());
                }
            }
        });
    }
}
