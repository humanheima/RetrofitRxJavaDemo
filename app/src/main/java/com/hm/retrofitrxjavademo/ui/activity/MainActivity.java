package com.hm.retrofitrxjavademo.ui.activity;

import android.Manifest;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.retrofitrxjavademo.R;
import com.hm.retrofitrxjavademo.databinding.ActivityMainBinding;
import com.hm.retrofitrxjavademo.event.ExceptionEvent;
import com.hm.retrofitrxjavademo.event.SimpleEvent;
import com.hm.retrofitrxjavademo.event.StickEvent;
import com.hm.retrofitrxjavademo.ui.base.BaseActivity;
import com.hm.retrofitrxjavademo.util.RxBus1;
import com.hm.retrofitrxjavademo.util.RxBus2;
import com.hm.retrofitrxjavademo.util.RxBus3;
import com.hm.retrofitrxjavademo.util.ToastUtil;

import java.util.List;

import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        requestPermission();
        registerEvents();
    }

    private void registerEvents() {
        compositeDisposable.add(RxBus1.get().toFlowable(SimpleEvent.class).subscribe(new Consumer<SimpleEvent>() {
            @Override
            public void accept(SimpleEvent simpleEvent) throws Exception {
                ToastUtil.toast("accept: SimpleEvent:" + simpleEvent.getMessage());
                Log.e(TAG, "accept: SimpleEvent:" + simpleEvent.getMessage());
                /**
                 * 如果在处理事件的时候出现了异常，会导致后续事件收不到，我们可以自己把异常抓住
                 */
                try {
                    String s = null;
                    s.substring(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, "registerEvents: error:" + throwable.getMessage());
            }
        }));

        compositeDisposable.add(RxBus2.get().register(ExceptionEvent.class,
                new Consumer<ExceptionEvent>() {
                    @Override
                    public void accept(ExceptionEvent event) throws Exception {
                        ToastUtil.toast("accept: ExceptionEvent:" + event.getMessage());
                        Log.e(TAG, "accept: ExceptionEvent:" + event.getMessage());
                        String s = null;
                        s.substring(0);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "ExceptionEvent: error:" + throwable.getMessage());
                    }
                }));

        //先post一个StickEvent 再订阅
        RxBus3.get().postSticky(new StickEvent("stick event"));

        compositeDisposable.add(RxBus3.get().registerSticky(StickEvent.class,
                new Consumer<StickEvent>() {
                    @Override
                    public void accept(StickEvent event) throws Exception {
                        ToastUtil.toast("accept: StickEvent:" + event.getMessage());
                        Log.e(TAG, "accept: StickEvent:" + event.getMessage());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "StickEvent: error:" + throwable.getMessage());
                    }
                }));
    }

    private void requestPermission() {
        String[] prems = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};
        if (EasyPermissions.hasPermissions(this, prems)) {
            Toast.makeText(this, "have got permission", Toast.LENGTH_LONG).show();
            testStorePath();
        } else {
            EasyPermissions.requestPermissions(MainActivity.this, "Request WRITE_EXTERNAL_STORAGE permission", REQUEST_PERMISSION, prems);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test_rxbus:
                EventBusActivity.launch(this);
                break;
            case R.id.btn_test_rxjava_operator:
                RxJavaOperatorActivity.launch(this);
                break;
            case R.id.btn_test_rxbinding:
                RxBindingActivity.launch(this);
                break;
            case R.id.btn_retrofit_rxjava:
                RetrofitRxJavaActivity.launch(this);
                break;
            case R.id.btn_upload_okhttp_activity:
                OkHttpDownloadActivity.launch(this);
                break;
            case R.id.btn_user_DownloadManager:
                DownloadManagerActivity.launch(this);
                break;
            case R.id.btn_only_retrofit:
                OnlyRetrofitActivity.launch(this);
                break;
            case R.id.btn_source_code:
                RxJava2Activity.launch(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        testStorePath();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    private void testStorePath() {
        Log.d(TAG, "testStorePath: getFilesDir().getAbsolutePath()=" + getFilesDir().getAbsolutePath());
        Log.d(TAG, "testStorePath: getFilesDir().getPath()=" + getFilesDir().getPath());
        Log.d(TAG, "testStorePath: getExternalCacheDir().getPath()=" + getExternalCacheDir().getPath());
        Log.d(TAG, "testStorePath: getExternalCacheDir()." +
                "getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()=" + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
        if (Environment.isExternalStorageEmulated()) {
            Log.d(TAG, "testStorePath:getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath():"
                    + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        }
    }
}
