package com.hm.retrofitrxjavademo.okhttpdownload;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by dumingwei on 2017/3/24.
 */
public abstract class DownLoadObserver implements Observer<DownloadInfo> {

    private static final String TAG = "DownLoadObserver";
    //用于取消注册的监听者
    protected Disposable d;
    protected DownloadInfo downloadInfo;

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onNext(DownloadInfo value) {
        this.downloadInfo = value;
    }

}
