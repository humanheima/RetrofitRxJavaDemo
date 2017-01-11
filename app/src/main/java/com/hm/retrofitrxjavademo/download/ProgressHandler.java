package com.hm.retrofitrxjavademo.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Administrator on 2017/1/11.
 */
public abstract class ProgressHandler extends Handler {

    private static final int DOWNLOAD_PROGRESS = 1;

    public ProgressHandler(Looper looper) {
        super(looper);
    }

    public void sendMessage(ProgressBean progressBean) {

        obtainMessage(DOWNLOAD_PROGRESS, progressBean).sendToTarget();
    }
    //这个方法应该在ui线程调用
    protected abstract void handleProgressMessage(long progress, long total, boolean done);

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case DOWNLOAD_PROGRESS:
                ProgressBean progressBean = (ProgressBean) msg.obj;
                handleProgressMessage(progressBean.getBytesRead(), progressBean.getContentLength(), progressBean.isDone());
                break;
            default:
                break;
        }
    }
}
