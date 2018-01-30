package com.hm.retrofitrxjavademo.ui.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by dumingwei on 2017/3/2.
 */
public abstract class BaseActivity<V extends ViewDataBinding> extends AppCompatActivity {


    protected V viewBind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        ButterKnife.bind(this);
        initData();
    }

    protected abstract int bindLayout();

    protected abstract void initData();

}
