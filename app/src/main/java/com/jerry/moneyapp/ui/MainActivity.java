package com.jerry.moneyapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.moneyapp.MyService;
import com.jerry.moneyapp.R;
import com.jerry.moneyapp.util.LogUtils;
import com.jerry.moneyapp.util.WeakHandler;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final ArrayMap<String, String> MAP = new ArrayMap<>();

    static {
        MAP.put("http://evassmat.com/I7xC", "baidu.com");
        MAP.put("http://evassmat.com/I93J", "aniu.tv");
    }

    private WebView[] webViews = new WebView[MAP.size()];

    private WeakHandler weakHandler = new WeakHandler(msg -> {
        for (int i = 0; i < webViews.length; i++) {
            LogUtils.d("点击！");
            String current = webViews[i].getUrl();
            String origin = MAP.keyAt(i);
            String target = MAP.valueAt(i);
            if (current == null) {
                break;
            }
            if (current.contains("locked?")) { webViews[i].loadUrl("javascript:document.getElementsByTagName(\"a\")[0].click();");
            } else if (target != null && current.contains(target)) {
                webViews[i].loadUrl(origin);
            } else {
                webViews[i].loadUrl("javascript:document.getElementsByClassName(\"mwButton\")[0].click();");
            }
        }
        this.weakHandler.sendEmptyMessageDelayed(0, 5000);
        return false;
    });

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, MyService.class));
//        init();
    }

    private void init() {
        Set<String> set = MAP.keySet();
        String[] urls = new String[set.size()];
        set.toArray(urls);

        webViews[0] = findViewById(R.id.webview1);
        webViews[1] = findViewById(R.id.webview2);


        for (int i = 0; i < urls.length; i++) {

            webViews[i].removeJavascriptInterface("searchBoxJavaBridge_");
            webViews[i].removeJavascriptInterface("accessibilityTraversal");
            webViews[i].removeJavascriptInterface("accessibility");

            WebSettings settings = webViews[i].getSettings();
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

            webViews[i].requestFocus();
            int finalI = i;
            webViews[i].setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return overrideUrlLoading(url);
                }

                private boolean overrideUrlLoading(String url) {
                    if (URLUtil.isNetworkUrl(url)) {
                        webViews[finalI].loadUrl(url);
                    }
                    return true;
                }
            });
            webViews[i].loadUrl(urls[i]);
        }
        weakHandler.sendEmptyMessage(0);
    }
}