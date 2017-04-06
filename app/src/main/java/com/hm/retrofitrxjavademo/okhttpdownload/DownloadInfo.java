package com.hm.retrofitrxjavademo.okhttpdownload;

/**
 * Created by dumingwei on 2017/3/24.
 */
public class DownloadInfo {

    //获取进度失败
    public static final long TOTAL_ERROR = -1;
    private String url;
    private long total;
    private long progress;
    private String fileName;

    public DownloadInfo(String url) {
        this.url = url;
    }

    public static long getTotalError() {
        return TOTAL_ERROR;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
