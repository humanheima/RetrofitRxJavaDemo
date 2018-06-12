package com.hm.retrofitrxjavademo.network;

import android.text.TextUtils;
import android.util.Log;

import com.hm.retrofitrxjavademo.App;
import com.hm.retrofitrxjavademo.network.api_entity.BaseEntity;
import com.hm.retrofitrxjavademo.util.JsonUtil;
import com.hm.retrofitrxjavademo.util.NetWorkUtil;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by dumingwei on 2016/9/9.
 */
public class NetWork {

    public static final String EMPTY_JSON_ARRAY = "[]";
    private static final long CACHE_SIZE = 100 * 1024 * 1024;
    private static final String TAG = "NetWork";
    private static final String BASE_URL = "http://api.k780.com:88";
    private static final String UPLOAD_FILE_BASE_URL = "http://api.k780.com:88";
    //实例化一个ObservableTransformer 不用每次都创建一个实例
    @SuppressWarnings("unchecked")
    private static final ObservableTransformer transform = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Function() {
                        @Override
                        public Object apply(Object response) {
                            return flatResponse(((HttpResult<Object>) response));
                        }
                    });
        }
    };
    //private static final String BASE_URL = "https://api.heweather.com/x3/";
    private static API api;
    private static UpLoadFileApi upLoadFileApi;
    private static OkHttpClient okHttpClient;

    public static UpLoadFileApi getUpLoadFileApi() {
        if (upLoadFileApi == null) {
            initClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            upLoadFileApi = retrofit.create(UpLoadFileApi.class);
        }
        return upLoadFileApi;
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
    private static <T> Observable<T> flatResponse(final HttpResult<T> response) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) {
                if (response.getSuccess() == 1) {
                    e.onNext(response.data);
                    e.onComplete();
                } else {
                    Log.d(TAG, "flatResponse subscribe: ");
                    e.onError(new APIException(response.getSuccess(), response.getResultMessage()));
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable<T> getData(BaseEntity entity, Class<T> classType) {
        return getApi()
                .getData(entity.getUrl(), entity.getParams())
                .compose(applySchedulers())
                .map(new Function<Object, T>() {
                    @Override
                    public T apply(Object o) {
                        String json = JsonUtil.getInstance().toJson(o);
                        return JsonUtil.getInstance().toObject(json, classType);
                    }
                })
                .onErrorResumeNext(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static API getApi() {
        if (api == null) {
            initClient();
            api = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build().create(API.class);
        }
        return api;
    }

    /**
     * 使用ObservableTransformer 来复用操作符
     * <p>
     * .subscribeOn(Schedulers.io())
     * .observeOn(AndroidSchedulers.mainThread())
     *
     * @param <T> 要返回的数据类型
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<HttpResult<T>, T> applySchedulers() {
        /*return new ObservableTransformer<HttpResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<HttpResult<T>> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Function<HttpResult<T>, ObservableSource<T>>() {
                            @Override
                            public ObservableSource<T> apply(HttpResult<T> tHttpResult) throws Exception {
                                return flatResponse(tHttpResult);
                            }
                        });
            }
        };*/
        return ((ObservableTransformer<HttpResult<T>, T>) transform);
    }

    /**
     * 获取的数据结构为 JsonArray
     *
     * @param entity    请求参数
     * @param classType 返回的数据结构类型
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable<List<T>> getDataList(BaseEntity entity, Class<T> classType) {
        return getApi()
                .getData(entity.getUrl(), entity.getParams())
                .compose(applySchedulers())
                .map(new Function<Object, List<T>>() {
                    @Override
                    public List<T> apply(Object o) throws Exception {
                        String json = JsonUtil.getInstance().toJson(o);
                        Log.e(TAG, "getDataList json=:" + json);
                        if (TextUtils.isEmpty(json) || EMPTY_JSON_ARRAY.equals(json)) {
                            return new ArrayList<>(0);
                        }
                        ArrayList<T> list = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(JsonUtil.getInstance().toObject(jsonArray.get(i).toString(), classType));
                        }
                        return list;
                    }
                });
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
            Log.e(TAG, "request path-->" + request.url());
            Log.e(TAG, "request query-->" + query);
            Log.e(TAG, "request body" + buffer.readUtf8());
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
            Log.e(TAG, "response" + source.buffer().clone().readUtf8());
            // Log.e(TAG, "request response" + originalResponse.body().string());
            Response response;
            if (NetWorkUtil.isConnected()) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置(注掉部分)
                String cacheControl = request.cacheControl().toString();
                response = originalResponse.newBuilder()
                        //.header("Cache-Control", cacheControl)//这是从接口的注解@Headers上读到的head信息
                        .header("Cache-Control", "max-age=30")//有网络的时候请求结果保存30秒
                        .removeHeader("Pragma")
                        .build();
                //Log.e(TAG, "request response head" + response.headers());
                return response;
            } else {
                //没网络的时候保存6分钟
                int maxAge = 60 * 60;
                response = originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-age=" + maxAge)//only-if-cached:(仅为请求标头)请求:告知缓存者,我希望内容来自缓存，我并不关心被缓存响应,是否是新鲜的.
                        .removeHeader("Pragma")//移除pragma消息头，移除它的原因是因为pragma也是控制缓存的一个消息头属性
                        .build();
                //Log.e(TAG, "request response head" + response.headers());
                return response;
            }
        }
    }

    public static class HttpResultFunc<T> implements Function<Throwable, Observable<T>> {

        @Override
        public Observable<T> apply(Throwable throwable) {
            Log.d(TAG, "apply: onErrorResumeNext:" + throwable.getMessage());
            return Observable.error(ExceptionHandler.handleException(throwable));
        }
    }
}
