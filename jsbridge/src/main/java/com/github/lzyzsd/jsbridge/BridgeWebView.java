package com.github.lzyzsd.jsbridge;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;

import com.tencent.smtt.export.external.interfaces.HttpAuthHandler;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * see http://stackoverflow.com/questions/3130654/memory-leak-in-webview and http://code.google.com/p/android/issues/detail?id=9375
 * Note that the bug does NOT appear to be fixed in android 2.2 as romain claims
 *
 * Also, you must call {@link #destroy()} from your activity's onDestroy method.
 */
@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView implements WebViewJavascriptBridge {
    public static final String LOCAL_FILE_SCHEMA = "mobile_file://";
    public static final String LOCAL_ASSET_SCHEMA = "mobile_asset://";

    public static final String HTML_INTERFACE = "HTMLOUT";
    private final String TAG = "BridgeWebView";
    private HtmlContentInterceptor htmlContentInterceptor;
    public static String CACHE_DIR;

    String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    BridgeHandler defaultHandler = new DefaultHandler();
    private OnPageLoadingListener pageLoadingListener;

    List<Message> startupMessage = new ArrayList<Message>();
    long uniqueId = 0;

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);
        init();
    }

    public BridgeWebView(Context context) {
        super(context.getApplicationContext());
        init();
    }

    @Override
    public void destroy() {
        defaultHandler = null;
        setTag(null);
        setHtmlContentInterceptor(null);
        setPageLoadingListener(null);
        setDefaultHandler(null);
        setTag(null);
        /* remove a new JavaScript interface called HTMLOUT */
        removeJavascriptInterface(BridgeWebView.HTML_INTERFACE);

        if(responseCallbacks != null)
            responseCallbacks.clear();
        if(messageHandlers != null)
            messageHandlers.clear();
        setOnLongClickListener(null);

        //flushMessageQueue();
        clearCache(true);
        clearFormData();
        clearMatches();
        clearSslPreferences();
        clearDisappearingChildren();
        clearHistory();
        //@Deprecated
        //clearView();
        clearAnimation();
        loadUrl("about:blank");
        removeAllViews();
        freeMemory();
        super.destroy();
    }

    /**
     * @param handler default handler,handle messages send by js without assigned handler name,
     *                if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    /*
        set htmlcontent processor
     */
    public void setHtmlContentInterceptor(HtmlContentInterceptor htmlContentInterceptor) {
        this.htmlContentInterceptor = htmlContentInterceptor;
        if (htmlContentInterceptor != null) {
            /* JavaScript must be enabled if you want it to work, obviously */
            getSettings().setJavaScriptEnabled(true);

            /* Register a new JavaScript interface called HTMLOUT */
            addJavascriptInterface(htmlContentInterceptor, HTML_INTERFACE);
        }
    }


    private void init() {
        //this.clearCache(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        //getSettings().setPluginsEnabled(false);
        getSettings().setSupportMultipleWindows(false);

        this.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        //设置支持缓存
        getSettings().setAppCacheEnabled(false);
        //设置缓存模式为默认
        //getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        this.setWebViewClient(new BridgeWebViewClient());
    }

    private void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    public void setPageLoadingListener(OnPageLoadingListener pageFinishedListener) {
        this.pageLoadingListener = pageFinishedListener;
    }

    class BridgeWebViewClient extends WebViewClient {
        private static final int WEBVIEW_LOAD_FAIL = -1;

        private WebResourceResponse getUtf8EncodedWebResourceResponse(InputStream data, String mime) {
            return new WebResourceResponse(mime, "UTF-8", data);
        }

        @Override
        public void onLoadResource(WebView webView, String url) {
//            clearCache(true);
            super.onLoadResource(webView, url);
        }


        // compaitble for android v21 on some device here!
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            // call shouldInterceptRequest(view, url) instead ;
            if(request != null && request.getUrl() != null) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }else{
                return null;
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
            if(url != null) {
                if(!BridgeUtil.isWhiteUrl(url))
                    return null;
                if (url.contains(LOCAL_ASSET_SCHEMA)) {
                    url = url.substring(url.indexOf(LOCAL_ASSET_SCHEMA));
                    try {
                        String extention = url.substring(1 + url.lastIndexOf("."));
                        String fileName = url.replace(LOCAL_ASSET_SCHEMA, "");
                        try {
                            fileName = URLDecoder.decode(fileName, "UTF-8");
                        }catch (Exception e){}
                        if (fileName.contains("?")) {
                            fileName = fileName.substring(0, fileName.indexOf("?"));
                        }
                        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
                        return getUtf8EncodedWebResourceResponse(getContext().getAssets().open(fileName), mime);
                    } catch (Exception e) {
                        return null;
                    }
                } else if (url.contains(LOCAL_FILE_SCHEMA)) {
                    try {
                        url = url.substring(url.indexOf(LOCAL_FILE_SCHEMA));
                        String extention = url.substring(1 + url.lastIndexOf("."));
                        String fileName = url.replace(LOCAL_FILE_SCHEMA, "");
                        try {
                            fileName = URLDecoder.decode(fileName, "UTF-8");
                        }catch (Exception e){}
                        if (fileName.contains("?")) {
                            fileName = fileName.substring(0, fileName.indexOf("?"));
                        }
                        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);

                        if (CACHE_DIR != null && (fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".png"))) {
                            String thumbFile = CACHE_DIR + "/" + Base64.encodeToString(fileName.getBytes(), 0).replaceAll("\\s*", "").replaceAll("=*", "").trim();
                            if (new File(thumbFile).exists()) {
                                fileName = thumbFile;
                            }
                        }
                    /*
                    final String finalFileName = fileName;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(finalFileName);
                        }
                    });
                    */
                        return getUtf8EncodedWebResourceResponse(new FileInputStream(fileName), mime);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url != null) {
                if (url.startsWith(LOCAL_ASSET_SCHEMA) || url.startsWith(LOCAL_FILE_SCHEMA)) {
                    return true;
                }
                if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
                    try {
                        url = URLDecoder.decode(url, "UTF-8");
                        handlerReturnData(url);
                    } catch (UnsupportedEncodingException e) {
                    }
                    return true;
                } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
                    flushMessageQueue();
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (pageLoadingListener != null) {
                pageLoadingListener.onStart(view, url);
            }
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (htmlContentInterceptor != null) {
                // have the page spill its guts, with a secret prefix
                view.loadUrl("javascript:" +
                        "window." + HTML_INTERFACE + ".processContent(document.getElementsByTagName('html')[0].innerHTML);");
//                view.loadUrl("javascript:" +
//                        "window." + HTML_INTERFACE + ".processContent(document.getElementsByTagName('body')[0].innerHTML);");
            }
            if (pageLoadingListener != null) {
                pageLoadingListener.onFinish(view, url);
            }

            if (toLoadJs != null) {
                BridgeUtil.webViewLoadLocalJs(view, toLoadJs);
            }

            //
            if (startupMessage != null) {
                for (Message m : startupMessage) {
                    dispatchMessage(m);
                }
                startupMessage = null;
            }
        }

        @SuppressLint("NewApi")
        //@Override
        public void onPermissionRequest(final PermissionRequest request) {
            Log.d("MyPersonalRep", "onPermissionRequest");
            post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    request.grant(request.getResources());
                }
            });
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d(TAG, "received ssl error: " + error.toString());
            handler.proceed(); // Ignore SSL certificate errors
        }

        public void onReceivedHttpAuthRequest(WebView var1, HttpAuthHandler var2, String var3, String var4) {
            var2.proceed(var3, var4);
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            //http://stackoverflow.com/questions/6552160/prevent-webview-from-displaying-web-page-not-available
            view.loadUrl("about:blank");
            if (pageLoadingListener != null) {
                pageLoadingListener.onFinish(view, null);
            }
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    private void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    public void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(final String data) {
                    //TODO comment
                    // deserializeMessage
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(data);
                        }
                    });
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(final String data) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                System.out.println(data);
                                            }
                                        });
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                        //TODO comment
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                        //TODO comment
                                    }
                                };
                            }
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            handler.handler(m.getData(), responseFunction);
                        }
                    }
                }
            });
        }
    }


    // Calculate the % of scroll progress in the actual web page content
    public float calculateProgression() {
        float positionTopView = getTop();
        float contentHeight = getContentHeight();
        float currentScrollPosition = getScrollY();
        float percentWebview = (currentScrollPosition - positionTopView) / contentHeight;
        return percentWebview;
    }

    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     *
     * @param handlerName
     * @param handler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * call javascript registered handler
     *
     * @param handlerName
     * @param data
     * @param callBack
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }
}
