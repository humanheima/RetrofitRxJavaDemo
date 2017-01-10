package com.hm.retrofitrxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.model.HttpResult;
import com.hm.retrofitrxjavademo.model.MovieEntity;
import com.hm.retrofitrxjavademo.model.NowWeatherBean;
import com.hm.retrofitrxjavademo.network.NetWork;
import com.hm.retrofitrxjavademo.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        ButterKnife.bind(this);
        loadingDialog = new LoadingDialog(this);

    }

    @OnClick(R.id.btn_now_weather)
    public void onClick() {
        getMovie();
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
}
