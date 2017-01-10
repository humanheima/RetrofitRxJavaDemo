package com.hm.retrofitrxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.model.NowWeatherBean;
import com.hm.retrofitrxjavademo.network.NetWork;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitActivity extends AppCompatActivity {

    private String tag = getClass().getSimpleName();
    @BindView(R.id.btn_now_weather)
    Button btnNowWeather;
    @BindView(R.id.text_result)
    TextView textResult;
    private HashMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        ButterKnife.bind(this);
        map = new HashMap();
        //"http://api.k780.com:88/?app=weather.history&weaid=1&date=2015-07-20&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
        map.put("app", "weather.today");
        map.put("weaid", 1);
        map.put("appkey", "15732");
        map.put("sign", "bf10378fb5e93259d0a94f2423fa81e5");
    }

    @OnClick(R.id.btn_now_weather)
    public void onClick() {
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
}
