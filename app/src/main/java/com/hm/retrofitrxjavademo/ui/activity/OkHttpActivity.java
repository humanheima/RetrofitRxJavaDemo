package com.hm.retrofitrxjavademo.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hm.retrofitrxjavademo.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

public class OkHttpActivity extends AppCompatActivity {

    private String tag = getClass().getSimpleName();
    private static String url = "http://api.k780.com:88/?app=weather.today&weaid=1&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
    private static String imgUrl = "http://img4.cache.netease.com/photo/0026/2015-05-19/APVC513454A40026.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);
        //testDownload();
        enquue();
    }

    private void enquue() {

       /* OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(new LoggingInterceptor())//添加应用拦截器
                .build();*/
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new LoggingInterceptor())//添加网络拦截器
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(tag, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(tag, response.body().string());
            }
        });
    }

    /**
     * 下载图片
     */
    private void testDownload() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(imgUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(tag, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (saveImg(response)) {
                    Log.e(tag, "下载图片成功");
                }
            }
        });
    }

    private boolean saveImg(Response response) {
        //图片下载时保存的地址
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "dumingwei.jpg");
        InputStream in = response.body().byteStream();
        FileOutputStream out = null;
        BufferedOutputStream bo = null;
        try {
            out = new FileOutputStream(file);
            bo = new BufferedOutputStream(out);
            int b;
            while ((b = in.read()) != -1) {
                bo.write(b);
            }
            bo.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }

                if (bo != null) {
                    bo.close();
                }
                response.body().close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * 上传文件
     */
    private void testupLoad() {
        //要上传的文件地址
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "dumingwei.jpg");
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody fileBody = RequestBody.create(mediaType, file);
        MediaType textType = MediaType.parse("text/plain ");
        RequestBody textBody = RequestBody.create(textType, new String("上传的文本"));
        //创建表单实体
        RequestBody requestBody = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file[]", "test.png", fileBody)
                .addFormDataPart("text", "text", textBody)
                //.addPart(Headers.of("Content-Disposition", "form-data;name=\"file\";" + "filename=\"test.png\""), fileBody)
                //.addPart(Headers.of("Content-Disposition", "form-data;name=\"text\";" + "textname=\"text\""), textBody)
                .build();
        Request request = new Request.Builder()
                .url("上传地址")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });

    }

    /**
     * 拦截器
     */
    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.e(tag, "intercept: " + String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.e(tag, "intercept: " + String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }

    class GzipRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            Request compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), gzip(originalRequest.body()))
                    .build();
            return chain.proceed(compressedRequest);
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }

}
