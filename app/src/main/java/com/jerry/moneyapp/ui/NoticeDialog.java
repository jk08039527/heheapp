package com.jerry.moneyapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jerry.moneyapp.R;

/**
 * Created by wzl on 2016/3/16.
 *
 * @Description 通知类型对话框, (有title)
 */
public class NoticeDialog extends Dialog {

    private EditText mEditText;
    private View.OnClickListener mPositiveListener;


    public NoticeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notice);
        mEditText = findViewById(R.id.et_url);
        findViewById(R.id.confirm_tv).setOnClickListener(v -> {
            dismiss();
            if (mPositiveListener != null) {
                mPositiveListener.onClick(v);
            }
        });
        findViewById(R.id.cancel_tv).setOnClickListener(v -> dismiss());
    }

    public void setPositiveListener(View.OnClickListener positiveListener) {
        mPositiveListener = positiveListener;
    }

    public String getEditText() {
        return mEditText.getText().toString();
    }
}
