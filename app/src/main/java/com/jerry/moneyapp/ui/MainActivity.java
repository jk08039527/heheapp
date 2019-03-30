package com.jerry.moneyapp.ui;

import java.io.DataOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.moneyapp.MyService;
import com.jerry.moneyapp.MyService.Callback;
import com.jerry.moneyapp.R;
import com.jerry.moneyapp.bean.GBData;

public class MainActivity extends AppCompatActivity implements OnClickListener, Callback, OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static String URL = "http://www.3122u.com/";
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private TextView tvInfo;
    private WebView mWebView;
    private boolean isBind;
    private MyService myService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            MyService.PlayBinder playBinder = (MyService.PlayBinder) service;
            myService = playBinder.getPlayService();
            myService.setCallback(MainActivity.this);
            myService.startExe();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            isBind = false;
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjectionManager != null) {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn3).setOnLongClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        tvInfo = findViewById(R.id.tvInfo);
        mWebView = findViewById(R.id.webview);
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
        mWebView.removeJavascriptInterface("accessibility");

        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setAllowFileAccess(false);

        settings.setDisplayZoomControls(false);// 不显示缩放按钮
        settings.setBuiltInZoomControls(true);// 设置内置的缩放控件
        settings.setSupportZoom(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 支持内容重新布局
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true);// 支持自动加载图片
        settings.setNeedInitialFocus(true); // 当WebView调用requestFocus时为WebView设置节点
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        mWebView.requestFocus();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return overrideUrlLoading(url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return overrideUrlLoading(request.getUrl().toString());
            }

            private boolean overrideUrlLoading(String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    mWebView.loadUrl(url);
                }
                return true;
            }
        });

        mWebView.loadUrl(URL);
        rootCmd();
    }

    public void rootCmd() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("chmod 777 /dev/block/mmcblk0\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    public void showText(String text) {
        tvInfo.setText(text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "User cancelled");
                Toast.makeText(this, "User cancelled!", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i(TAG, "Starting screen capture");

            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            setUpVirtualDisplay();
        }
    }

    private void setUpVirtualDisplay() {
        Point size = new Point();
        DisplayMetrics metrics = new DisplayMetrics();
        Display defaultDisplay = getWindow().getWindowManager().getDefaultDisplay();
        defaultDisplay.getSize(size);
        defaultDisplay.getMetrics(metrics);

        final ImageReader imageReader = ImageReader.newInstance(size.x, size.y, PixelFormat.RGBA_8888, 1);
        mMediaProjection.createVirtualDisplay("ScreenCapture",
            size.x, size.y, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.getSurface(), null, null);
        GBData.reader = imageReader;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                if (isBind) {
                    myService.setBtnClickable();
                } else {
                    Toast.makeText(this, "先点击开始", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn2:
                if (isBind) {
                    return;
                }
                bindService(new Intent(this, MyService.class), mServiceConnection, BIND_AUTO_CREATE);
                break;
            case R.id.btn3:
                if (mWebView != null) {
                    mWebView.reload();
                }
                break;
            case R.id.btn4:
                startActivity(new Intent(this, AnalyzeActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn3:
                NoticeDialog noticeDialog = new NoticeDialog(this);
                noticeDialog.setEditText(URL);
                noticeDialog.setPositiveListener(view -> {
                    String url = noticeDialog.getEditText();
                    if (TextUtils.isEmpty(url)){
                        return;
                    }
                    URL = url;
                    mWebView.loadUrl(URL);
                });
                noticeDialog.show();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        isBind = false;
        myService = null;
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}