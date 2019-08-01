package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hm.retrofitrxjavademo.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxJavaSourceCodeActivity extends AppCompatActivity {

    private static final String TAG = "RxJavaSourceCodeActivit";

    private Button btnFlatMap;

    public static void launch(Context context) {
        Intent intent = new Intent(context, RxJavaSourceCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_source_code);

        btnFlatMap = findViewById(R.id.btnFlatMap);
        btnFlatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFlatMap();
            }
        });
        test();


    }


    /**
     * ObservableCreate
     * ObservableSubscribeOn
     * ObservableObserveOn
     */
    private void test() {
        Log.d(TAG, "test: " + Schedulers.computation().toString());
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();

            }
        })
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    private void testFlatMap() {
        Observable.create(new ObservableOnSubscribe<List<Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Integer>> emitter) throws Exception {
                List<Integer> list1 = new ArrayList<>();
                list1.add(1);
                list1.add(2);
                list1.add(3);
                List<Integer> list2 = new ArrayList<>();
                list2.add(4);
                list2.add(5);
                list2.add(6);

                emitter.onNext(list1);
                emitter.onNext(list2);

                emitter.onComplete();

            }
        }).flatMap(new Function<List<Integer>, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(List<Integer> integers) throws Exception {
                //注释1处，返回的是一个ObservableFromIterable对象
                return Observable.fromIterable(integers);
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        });
    }

}
