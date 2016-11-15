package com.github.lzyzsd.jsbridge;


import com.tencent.smtt.sdk.WebView;

/**
 * Created by hippo on 12/14/15.
 */
public interface OnPageLoadingListener {
    void onStart(WebView webView, String url);
    void onFinish(WebView webView, String url);
}
