package com.hm.retrofitrxjavademo.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.hm.retrofitrxjavademo.R;

/**
 * Created by dumingwei on 2017/1/10.
 */
public class LoadingDialog extends ProgressDialog {

    private TextView textContent;
    private String content;

    public LoadingDialog(Context context) {
        this(context, R.style.LoadingDialog);
    }

    public LoadingDialog(Context context, String content) {
        this(context, R.style.LoadingDialog);
        this.content = content;
    }

    public LoadingDialog(Context context, int theme) {
        super(context, R.style.LoadingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        setCanceledOnTouchOutside(false);
        textContent = findViewById(R.id.text_dialog);
        if (!TextUtils.isEmpty(content)) {
            textContent.setText(content);
        }
    }
}
