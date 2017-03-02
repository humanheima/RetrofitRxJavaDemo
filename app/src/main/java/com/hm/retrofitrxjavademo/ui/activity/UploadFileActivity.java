package com.hm.retrofitrxjavademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.network.NetWork;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;

import java.io.File;

import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UploadFileActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    public static void launch(Context context) {
        Intent starter = new Intent(context, UploadFileActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_upload_file;
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_upload_single, R.id.btn_upload_multi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload_single:
                break;
            case R.id.btn_upload_multi:
                break;
        }
    }

    private void upLoadFile(String path) {
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        NetWork.getUpLoadFileApi().uploadFile(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "upLoadFile success" + s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "upLoadFile error:" + throwable.getMessage());
                    }
                });
    }
}
