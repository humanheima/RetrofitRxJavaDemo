package com.hm.retrofitrxjavademo.network;

import android.util.Log;

import com.hm.retrofitrxjavademo.App;
import com.hm.retrofitrxjavademo.model.HttpResult;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;

import static com.hm.retrofitrxjavademo.RxJavaActivity.tag;

/**
 * Created by Administrator on 2016/9/9.
 */
public class NetWork {

    private static final long CACHE_SIZE = 100 * 1024 * 1024;
    private static API api;
    private static OkHttpClient okHttpClient;
    private static OkHttpClient dpwnLoadHttpClient;

    //private static final String BASE_URL = "https://api.heweather.com/x3/";

    private static final String BASE_URL = "http://api.k780.com:88";

    public static API getApi() {
        if (api == null) {
            initClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            api = retrofit.create(API.class);
        }
        return api;
    }

    /**
     * 构建OkHttpClient实例（配置一些请求的全局参数）
     */
    private static void initClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        File cacheFile = new File(App.getInstance().getExternalCacheDir(), "okHttpCache");
        Cache cache = new Cache(cacheFile, CACHE_SIZE);
        builder.cache(cache);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        if (builder.interceptors() != null) {
            builder.interceptors().clear();
        }
        //处理拦截器，主要是做了个header和连接超时、读取超时设置，
        builder.addInterceptor(new CacheInterceptor());
        //.addNetworkInterceptor(new CacheInterceptor());
        builder.connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        //通过build模式构建实例
        okHttpClient = builder
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
     */
    public static <T> Observable<T> flatResponse(final HttpResult<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response.isSuccess()) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response.data);
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException(response.resultCode, response.resultMessage));
                    }
                    return;
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }


    /**
     * 自定义异常，当接口返回的{link Response#code}不为{link Constant#OK}时，需要跑出此异常
     * eg：登陆时验证码错误；参数为传递等
     */
    public static class APIException extends Exception {
        public int code;
        public String message;

        public APIException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    private static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Buffer buffer = new Buffer();
            if (request.body() != null) {
                request.body().writeTo(buffer);
            }
            String query = request.url().query();
            Log.e(tag, "request path-->" + request.url());
            Log.e(tag, "request query-->" + query);
            Log.e(tag, "request body" + buffer.readUtf8());
            //没有网络就读取本地缓存的数据
            if (!NetWorkUtil.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            } else {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            }
            //请求结果
            Response originalResponse = chain.proceed(request);
            BufferedSource source = originalResponse.body().source();
            source.request(Long.MAX_VALUE);//不加这句打印不出来
//            Log.e(tag, "response" + source.buffer().clone().readUtf8());
            // Log.e(tag, "request response" + originalResponse.body().string());
            Response response;
            if (NetWorkUtil.isConnected()) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置(注掉部分)
                String cacheControl = request.cacheControl().toString();
                response = originalResponse.newBuilder()
                        //.header("Cache-Control", cacheControl)//这是从接口的注解@Headers上读到的head信息
                        .header("Cache-Control", "max-age=30")//有网络的时候请求结果保存30秒
                        .removeHeader("Pragma")
                        .build();

                Log.e(tag, "request response head" + response.headers());
                return response;
            } else {
                //没网络的时候保存6分钟
                int maxAge = 60 * 60;
                response = originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-age=" + maxAge)//only-if-cached:(仅为请求标头)请求:告知缓存者,我希望内容来自缓存，我并不关心被缓存响应,是否是新鲜的.
                        .removeHeader("Pragma")//移除pragma消息头，移除它的原因是因为pragma也是控制缓存的一个消息头属性
                        .build();
                Log.e(tag, "request response head" + response.headers());
                return response;
            }
        }
    }
}
