package info.futureme.abs.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.github.lzyzsd.jsbridge.HtmlContentInterceptor;
import com.github.lzyzsd.jsbridge.OnPageLoadingListener;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import info.futureme.abs.FApplication;
import info.futureme.abs.R;
import info.futureme.abs.base.InjectableFragment;
import info.futureme.abs.conf.FConstants;
import info.futureme.abs.rest.RefreshableAuthInterceptor;
import info.futureme.abs.util.CompatHelper;
import info.futureme.abs.util.DLog;
import info.futureme.abs.view.LongPressListenerWrapper;
import info.futureme.abs.view.WebFrameLayout;
import info.futureme.abs.view.overscroll.OverScrollDecoratorHelper;

/**
 * Created by hippo on 11/15/15.
 *
 * webview fragment with js,native,h5 title handlers and so on.
 */
public class BridgedX5WebViewFragment extends InjectableFragment implements
        HtmlContentInterceptor, SwipeRefreshLayout.OnRefreshListener,
        OnPageLoadingListener, WebFrameLayout.OnSoftKeyboardListener {
    public static final String IS_DETAIL = "is_detail_page";
    private long timerCounter;
    protected BridgeWebView webView;
    private WeakReference<BridgeHandler> defaultBridgeHandler;
    private TitleUpdater onTitleReceiveHandler;
    private WeakReference<ProgressBar> progressBar;
    private Handler handler;
    private Runnable delayRunable;
    private WebFrameLayout webFrameLayout;

    public static BridgedX5WebViewFragment newInstance(String url) {
        BridgedX5WebViewFragment fragment = new BridgedX5WebViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putString(FConstants.X5WEBVIEW_INITIAL_URL, url);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        webFrameLayout = (WebFrameLayout) view.findViewById(R.id.web_frame);
        webFrameLayout.setOnSoftKeyboardListener(this);

        progressBar = new WeakReference<>((ProgressBar) view.findViewById(R.id.progressBar));
        if(progressBar.get() != null) {
            progressBar.get().setVisibility(View.GONE);
        }
        this.timerCounter = System.currentTimeMillis();
        /*
        Tip 2: Using application context when initializing webview. This means webview should not be initialized from xml. You need to create it programmatically.
        By doing this, 90% of the time, webview memory consumption is no longer an issue
         */
        webView =  new BridgeWebView(getContext().getApplicationContext());
        webFrameLayout.addView(webView, 0);

        //webView = (BridgeWebView)view.findViewById(R.id.web_view);
        webView.getSettings().setUseWideViewPort(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        webView.setHtmlContentInterceptor(this);
        webView.setPageLoadingListener(this);
        if (FApplication.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (!QbSdk.isTbsCoreInited()) {//preinit只需要调用一次，如果已经完成了初始化，那么就直接构造view
            QbSdk.preInit(getContext().getApplicationContext(), myCallback);//设置X5初始化完成的回调接口
        } else {
            doX5WebViewConstruction();
        }


        //refreshLayout.setEnabled(true);
        /*
        refreshLayout.setEnabled(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
                */
        // Only enable swipeToRefresh if is mainWebView is scrolled to the top.

        /*if (CompatHelper.hasIceCreamSandwich()) {
            OverScrollDecoratorHelper.setUpStaticOverScroll(webView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        webView.registerHandler("pageListener", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Res res = FGson.gson().fromJson(data, Res.class);
                if ("top".equals(res.getMessage())) {
                    boolean top = Boolean.parseBoolean("" + res.getResult());
                    DLog.w("html scroll:", top + " data:" + data);
                    if (refreshLayout != null) {
                        if (top) {
                            //refreshLayout.setEnabled(true);
                            refreshLayout.setEnabled(false);
                        } else {
                            refreshLayout.setEnabled(false);
                        }
                    }
                }
            }
        });
        */

        boolean detail = getArguments().getBoolean(IS_DETAIL);
        if (!detail && CompatHelper.hasIceCreamSandwich()) {
            OverScrollDecoratorHelper.setUpStaticOverScroll(webView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        }
        setConfigCallback((WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        return view;
    }

    public void clearCacheAndRefresh() {
        if (FApplication.DEBUG) {
            webView.clearCache(true);
            webView.clearFormData();
            webView.clearHistory();
        }
        if (webView != null) {
            doRefresh(true);
        }
    }

    //flipscreen not visible again
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private QbSdk.PreInitCallback myCallback = new QbSdk.PreInitCallback() {

        @Override
        public void onViewInitFinished() {//当X5webview 初始化结束后的回调
            // TODO Auto-generated method stub
            float deltaTime = (System.currentTimeMillis() - BridgedX5WebViewFragment.this.timerCounter) / 1000;
            DLog.toast("x5初始化使用了" + deltaTime + "秒");
            DLog.i("yuanhaizhou", "x5初始化使用了" + deltaTime + "秒");
            doX5WebViewConstruction();
        }

        @Override
        public void onCoreInitFinished() {
            // TODO Auto-generated method stub
            DLog.i("yuanhaizhou", "onCoreInitFinished");
        }
    };

    /**
     * 使用这个方法完成webview的构造<br>
     * 总之webview的初始化一定要放在QbSdk.preInit的X5初始化构造之后
     */
    private void doX5WebViewConstruction() {
        if(webView == null) return;
        if (defaultBridgeHandler != null && defaultBridgeHandler.get() != null) {
            webView.setDefaultHandler(defaultBridgeHandler.get());
            webView.setTag(defaultBridgeHandler.get());
        } else {
            if (webView.getTag() instanceof BridgeHandler)
                webView.setDefaultHandler((BridgeHandler) webView.getTag());
            else {
                webView.setDefaultHandler(new DefaultHandler());
                DLog.w(BridgedX5WebViewFragment.class.getName(), "setting default handler!!!!!!!!!!!!!!!!!");
            }
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(true);// 设置允许访问文件数据
        webSettings.setBuiltInZoomControls(false);// 设置不支持缩放
        webSettings.setSavePassword(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        // By using this method together with the overridden method onReceivedSslError()
        // you will avoid the "WebView Blank Page" problem to appear. This might happen if you
        // use a "https" url!
        webSettings.setDomStorageEnabled(true);


        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                onProgress(view, view.getUrl(), progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (onTitleReceiveHandler != null)
                    //do little tick to hide system error page
                //http://stackoverflow.com/questions/6552160/prevent-webview-from-displaying-web-page-not-available
                    onTitleReceiveHandler.updateTitle(title);
            }
        });

        /**
         * https://github.com/cmti95035/FaceClassificationApp/blob/ee166f91972d7cb72b863094260b4e6a547c96bc/app/src/main/java/com/chinamobile/cmti/faceclassification/WebRTCActivity.java
         */
//        trustEveryone();

        doRefresh(true);

        webView.setOnLongClickListener(new LongPressListenerWrapper(webView, getContext().getApplicationContext()));//do long press listener
    }


    //refresh with dynamic header, like auth_token and api_version
    private void doRefresh(boolean doHold) {
        Bundle bundle = getArguments();

        Map<String, String> headers = new HashMap<String, String>();
        if(RefreshableAuthInterceptor.accountService != null)
            headers.put(FConstants.HEADER_AUTHORIZATION_KEY, "Basic " + RefreshableAuthInterceptor.accountService.getAccessToken());
        if (webView != null) {
            webView.flushMessageQueue();

            if(doHold){
                //do hold animation
                webView.clearAnimation();
                webView.startAnimation(AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.hold_on));
            }
            webView.loadUrl(bundle.getString(FConstants.X5WEBVIEW_INITIAL_URL), headers);
        }
    }

    @Override
    public int provideContentRes() {
        return R.layout.x5_hibrd_web;
    }

    @Override
    protected void onFragmentInVisible(Bundle savedInstanceState) {
    }

    @Override
    protected void onFragmentVisible(Bundle savedInstanceState) {
    }

    public void setDefaultBridgeHandler(BridgeHandler defaultBridgeHandler) {
        this.defaultBridgeHandler = new WeakReference<BridgeHandler>(defaultBridgeHandler);
    }

    public void setOnTitleReceiveHandler(TitleUpdater onTitleReceiveHandler) {
        this.onTitleReceiveHandler = onTitleReceiveHandler;
    }

    public void send(String data) {
        if(webView != null)
            webView.send(data);
    }

    public void send(String data, CallBackFunction responseCallback) {
        if(webView != null)
            webView.send(data, responseCallback);
    }

    public void send(String handlerName, String data, CallBackFunction responseCallback) {
        if(webView != null)
            webView.callHandler(handlerName, data, responseCallback);
    }

    /* process HTML */
    @JavascriptInterface
    @SuppressWarnings("unused")
    @Override
    public void processContent(String html) {
//        DLog.i("html:", html);
        if (TextUtils.isEmpty(html)) {
            triggerWebFinish();
            return;
        }
        html = html.trim();
        //{} must be json, token failed?
        //TODO
        if (html.contains("401 Unauthorized")) {
                RefreshableAuthInterceptor.attemptRefreshToken();
            if (webView != null)
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        doRefresh(false);
                    }
                });
        } else {
            triggerWebFinish();
        }
    }


    private void triggerWebFinish() {
        if (webView != null)
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if (webView != null)
                        onFinish(webView, getUrl());
                }
            });
    }

    @Override
    public void onRefresh() {
        doRefresh(true);
    }

    public void onRefresh(String url) {
        if(webView == null) return;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(FConstants.HEADER_AUTHORIZATION_KEY, "Basic " + RefreshableAuthInterceptor.accountService.getAccessToken());
        webView.loadUrl(url, headers);
    }

    @Override
    public void onStart(final WebView webView, final String url) {
        if(progressBar != null && progressBar.get() != null && getView() != null) {
            progressBar.get().setVisibility(View.VISIBLE);
            progressBar.get().setProgress(0);
            getView().findViewById(R.id.error_frame).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_error_retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doRefresh(false);
                }
            });
            if(webView != null)
                webView.setVisibility(View.VISIBLE);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DLog.i("webview start", url);
            }
        });
    }

    @Override
    public void onFinish(WebView webView, final String url) {
        if(url == null && getView() != null){
            if (onTitleReceiveHandler != null)
                onTitleReceiveHandler.updateTitle(null);
            getView().findViewById(R.id.error_frame).setVisibility(View.VISIBLE);
            if(webView != null)
                webView.setVisibility(View.GONE);
        }
        if(webView != null)
            webView.clearAnimation();
    }

    public synchronized void onProgress(WebView webView, final String url, final int progress) {
        if (progressBar != null && progressBar.get() != null) {
            progressBar.get().setVisibility(View.VISIBLE);
            progressBar.get().setProgress(progress);
        }
        if(progress == 100 && handler == null){
            handler = new Handler(Looper.getMainLooper());
            delayRunable = new Runnable() {
                @Override
                public void run() {
                    if(progressBar != null && progressBar.get() != null) {
                        progressBar.get().setVisibility(View.GONE);
                        if (handler != null) {
                            handler.removeCallbacks(this);
                            handler = null;
                        }
                    }
                }
            };
            handler.postDelayed(delayRunable, 100);
        }
    }

    public void loadJS(String message, Object data) {
        DLog.w("js:", "javascript:" + message + "(" + data + ");");
        if(webView != null)
            webView.loadUrl("javascript:" + message + "(" + data + ");");
    }

    @Override
    public void onDestroy() {
        progressBar = null;
        webFrameLayout = null;
        onTitleReceiveHandler = null;
        defaultBridgeHandler = null;
        setOnTitleReceiveHandler(null);
        setDefaultBridgeHandler(null);
        if(webView != null) {
            webView.setOnLongClickListener(null);//do long press listener
            webView.setDefaultHandler(null);
            webView.setTag(null);
        }
        if (handler != null) {
            handler.removeCallbacks(delayRunable);
            handler = null;
        }
        setConfigCallback(null);
        super.onDestroy();
    }


    public void setConfigCallback(WindowManager windowManager) {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field = field.getType().getDeclaredField("mBrowserFrame");
            field = field.getType().getDeclaredField("sConfigCallback");
            field.setAccessible(true);
            Object configCallback = field.get(null);

            if (null == configCallback) {
                return;
            }

            field = field.getType().getDeclaredField("mWindowManager");
            field.setAccessible(true);
            field.set(configCallback, windowManager);
        } catch(Exception e) {
        }
    }

    public String getUrl() {
        return webView == null ? "" : webView.getUrl();
    }

    public void onDetach() {
        releaseWebViews();
        super.onDetach();
    }

    /* Based on user1668939's answer on this post
    *(http://stackoverflow.com/a/12408703/1369016), this is how I fixed my WebView leak inside a fragment
    */
    public synchronized void releaseWebViews() {
        if(webView != null) {
            try {
                if(webView.getParent() != null) {
                    ((ViewGroup) webView.getParent()).removeView(webView);
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    //this is causing the segfault occasionally below 4.2
                webView.destroy();
//                }
//                BugFix.destroyAttach(webView);
            }catch (IllegalArgumentException e) {
                DLog.p(e);
            }

            RefWatcher refWatcher = FApplication.getRefWatcher();
            refWatcher.watch(webView);
            webView = null;
        }
    }

    @Override
    public void onShown(int newSpec) {
        /*
        if(webView != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(webView.getWidth(), newSpec);
            webView.setLayoutParams(lp);
        }
        */
    }

    @Override
    public void onHidden(int oldSpec) {
        /*
        if(webView != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(webView.getWidth(), webFrameLayout.getMeasuredHeight());
            webView.setLayoutParams(lp);
            webView.requestLayout();
        }
        */
    }

    public static interface TitleUpdater {
        void updateTitle(String title);
    }

}
