package com.hm.retrofitrxjavademo.ui.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hm.retrofitrxjavademo.network.APIException;
import com.hm.retrofitrxjavademo.util.ToastUtil;
import com.hm.retrofitrxjavademo.widget.LoadingDialog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by dumingwei on 2017/3/2.
 */
public abstract class BaseActivity<V extends ViewDataBinding> extends AppCompatActivity {

    protected V viewBind;
    protected String TAG = getClass().getName();
    //用来取消订阅
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = DataBindingUtil.setContentView(this, bindLayout());
        initData();
    }

    protected abstract int bindLayout();

    protected abstract void initData();

    protected final void showLoading() {
        if (null == loadingDialog) {
            loadingDialog = new LoadingDialog(this);
        }
        if (loadingDialog.isShowing()) {
            return;
        }
        loadingDialog.show();
    }

    protected <T> DisposableObserver<T> newObserver(final Consumer<T> onNext) {
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                try {
                    onNext.accept(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                int code = ((APIException) e).getCode();
                String message = e.getMessage();
                Log.e(TAG, "onError: code" + code + ",message:" + message);
                hideLoading();
                ToastUtil.toast(message);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
                hideLoading();
            }
        };
    }

    protected final void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
