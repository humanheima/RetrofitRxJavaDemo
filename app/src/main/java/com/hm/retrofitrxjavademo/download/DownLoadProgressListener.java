package com.hm.retrofitrxjavademo.download;

/**
 * Created by Administrator on 2017/1/11.
 * 下载进度监听
 */
public interface DownLoadProgressListener {

    /**
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     */
    void onProgress(long progress, long total, boolean done);
}
