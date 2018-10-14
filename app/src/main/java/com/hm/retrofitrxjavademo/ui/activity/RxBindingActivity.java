package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityRxBindingBinding;
import com.hm.retrofitrxjavademo.model.ValidationResult;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import com.hm.retrofitrxjavademo.util.ToastUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxBindingActivity extends BaseActivity<ActivityRxBindingBinding> {

    private final long MAX_TIME = 30;
    private ValidationResult validationResult;

    public static void launch(Context context) {
        Intent intent = new Intent(context, RxBindingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_rx_binding;
    }

    @Override
    protected void initData() {
        RxView.clicks(viewBind.btnClick)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ToastUtil.toast("click event");
                    }
                });

        RxView.longClicks(viewBind.btnLongClick)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ToastUtil.toast("long click event");
                    }
                });
        RxView.clicks(viewBind.btnPreventRepeatClick)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e(TAG, "accept: prevent repeat click event");
                        ToastUtil.toast("prevent repeat click event");
                    }
                });
        //开始刚进入的时候都会发射一个textChanges事件
        /*RxTextView.textChanges(viewBind.etPhone).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                Log.e(TAG, "accept:etPhone ");
            }
        });  RxTextView.textChanges(viewBind.etPassword).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                Log.e(TAG, "accept:etPassword ");
            }
        });*/

        validateForm();
        countDown();
    }

    /**
     * 登录表单验证
     */
    private void validateForm() {
        Observable<CharSequence> observablePhone = RxTextView.textChanges(viewBind.etPhone);
        Observable<CharSequence> observablePassword = RxTextView.textChanges(viewBind.etPassword);
        Observable.combineLatest(observablePhone, observablePassword, new BiFunction<CharSequence, CharSequence, ValidationResult>() {
            @Override
            public ValidationResult apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                Log.e(TAG, "apply:combineLatest ");
                if (charSequence.length() > 0 || charSequence2.length() > 0) {
                    viewBind.btnLogin.setEnabled(true);
                } else {
                    viewBind.btnLogin.setEnabled(false);
                }
                ValidationResult result = new ValidationResult();
                if (charSequence.length() == 0) {
                    result.setFlag(false);
                    result.setMessage("手机号码不能为空");
                } else if (charSequence.length() != 11) {
                    result.setFlag(false);
                    result.setMessage("手机号码需要11位");
                } else if (charSequence2.length() == 0) {
                    result.setFlag(false);
                    result.setMessage("密码不能为空");
                }
                return result;
            }
        }).subscribe(new Consumer<ValidationResult>() {
            @Override
            public void accept(ValidationResult result) throws Exception {
                validationResult = result;
            }
        });

        RxView.clicks(viewBind.btnLogin).throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (validationResult == null) {
                            return;
                        }
                        if (validationResult.getFlag()) {
                            ToastUtil.toast("模拟登陆成功");
                        } else {
                            ToastUtil.toast(validationResult.getMessage());
                        }
                    }
                });
    }

    private void countDown() {
        RxView.clicks(viewBind.btnGetVerifyCode)
                .throttleFirst(MAX_TIME, TimeUnit.SECONDS)
                .flatMap(new Function<Object, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Object o) throws Exception {
                        viewBind.btnGetVerifyCode.setEnabled(false);
                        viewBind.btnGetVerifyCode.setText("剩余" + MAX_TIME + "秒");
                        //调用接口获取验证码
                        return Observable.intervalRange(1, MAX_TIME, 0, 1, TimeUnit.SECONDS, Schedulers.io());
                    }
                }).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long aLong) throws Exception {
                return MAX_TIME - aLong;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (aLong == 0) {
                            viewBind.btnGetVerifyCode.setEnabled(false);
                            viewBind.btnGetVerifyCode.setText("获取验证码");
                        } else {
                            viewBind.btnGetVerifyCode.setText("剩余" + aLong + "秒");
                        }
                    }
                });
    }


    /*public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_click:
                break;
            default:
                break;
        }
    }*/
}
