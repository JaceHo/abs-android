package com.github.lzyzsd.jsbridge;

import android.webkit.JavascriptInterface;

/**
 * Created by hippo on 12/12/15.
 */
public interface HtmlContentInterceptor {
    /* An instance of this class will be registered as a JavaScript interface */
    @JavascriptInterface
    @SuppressWarnings("unused")
    // process the html as needed by the app
    void processContent(String html);
}
