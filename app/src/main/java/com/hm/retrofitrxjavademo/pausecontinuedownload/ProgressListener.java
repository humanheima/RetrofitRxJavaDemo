package com.hm.retrofitrxjavademo.pausecontinuedownload;

public interface ProgressListener {

    void onPreExecute(long contentLength);

    void update(long totalBytes, boolean done);
}