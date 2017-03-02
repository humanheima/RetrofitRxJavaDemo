package com.hm.retrofitrxjavademo.ui.activity;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.btn_upload_file_activity)
    public void onClick() {
        UploadFileActivity.launch(this);
    }
}
