package com.hm.retrofitrxjavademo.ui.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hm.retrofitrxjavademo.util.ToastUtil;
import com.hm.retrofitrxjavademo.widget.LoadingDialog;

import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by dumingwei on 2017/3/2.
 */
public abstract class BaseActivity<V extends ViewDataBinding> extends AppCompatActivity {

    protected V viewBind;
    private LoadingDialog loadingDialog;
    protected String TAG = getClass().getName();
    //用来取消订阅
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

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

    protected final void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
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
                Log.e(TAG, "onError: " + e.getMessage());
                hideLoading();
                ToastUtil.toast(e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
                hideLoading();
            }
        };
    }
}
