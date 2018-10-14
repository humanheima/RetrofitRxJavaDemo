package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityEventBusBinding;
import com.hm.retrofitrxjavademo.event.ExceptionEvent;
import com.hm.retrofitrxjavademo.event.SimpleEvent;
import com.hm.retrofitrxjavademo.event.StickEvent;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import com.hm.retrofitrxjavademo.util.RxBus1;
import com.hm.retrofitrxjavademo.util.RxBus2;
import com.hm.retrofitrxjavademo.util.RxBus3;
import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.functions.Consumer;

public class EventBusActivity extends BaseActivity<ActivityEventBusBinding> {

    public static void launch(Context context) {
        Intent intent = new Intent(context, EventBusActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_event_bus;
    }

    @Override
    protected void initData() {
        RxView.clicks(viewBind.btnPostSimpleEvent)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        RxBus1.get().post(new SimpleEvent("simple event"));
                    }
                });
        RxView.clicks(viewBind.btnTestRelay)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        RxBus2.get().post(new ExceptionEvent("exception event"));
                    }
                });
        RxView.clicks(viewBind.btnTestStickyEvent)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        RxBus3.get().postSticky(new StickEvent("stick event"));
                    }
                });
    }
}
