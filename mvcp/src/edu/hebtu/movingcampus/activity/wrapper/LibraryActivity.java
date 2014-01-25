package edu.hebtu.movingcampus.activity.wrapper;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.MainActivity;
import edu.hebtu.movingcampus.activity.base.Observer;
import edu.hebtu.movingcampus.activity.base.PageWraper;
import edu.hebtu.movingcampus.config.Urls;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver.NetworkchangeListener;

/**
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:32 AM
 */
public class LibraryActivity implements Observer, PageWraper,NetworkchangeListener {
	private WebView browser;
	private Activity mainActivity = MainActivity.instance;
	private View contentView;
	private static View loadingLayout;
	private static View loadfailedLayout;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public LibraryActivity(View view) {
		this.contentView = view;
		browser = (WebView) view
				.findViewById(R.id.webkit);
		
		loadingLayout= view.findViewById(R.id.loading_layout);
		loadfailedLayout= view.findViewById(R.id.loadfailed_layout);

		browser.getSettings().setJavaScriptEnabled(true);
		browser.setWebViewClient(new SampleWebViewClient());

		AppInfo app = (AppInfo) (mainActivity.getApplication());
		// 自动登录
		login(app);
		
		bindButton();
		
		view.post(new Runnable() {
			
			@Override
			public void run() {
				browser.loadUrl(Urls.LIB_URL);
			}
		});
	}

	private void bindButton() {
		// TODO Auto-generated method stub

		contentView.findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				browser.reload();
			}
		});
		
	}

	private void login(AppInfo app) {
		// 登录
		try{
		String name = AppInfo.getUser().getCid();
		String password = app.getPassword().substring(2, 6);
		String logindata = "username=" + name + "&password=" + password;
		browser.postUrl(Urls.LIB_POST_LOGIN,
				EncodingUtils.getBytes(logindata, "base64"));

		// 注册
		TelephonyManager tMgr = (TelephonyManager) (mainActivity
				.getSystemService(Context.TELEPHONY_SERVICE));
		String mPhoneNumber = tMgr.getLine1Number();
		String regiestdata = "userinfo.phone=" + mPhoneNumber;
		browser.postUrl(Urls.LIB_POST_REGIST,
				EncodingUtils.getAsciiBytes(regiestdata));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static class SampleWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			loadingLayout.setVisibility(View.VISIBLE);
			loadfailedLayout.setVisibility(View.GONE);
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view,int  errorCode,String  description,String failingUrl){
			loadingLayout.setVisibility(View.GONE);
			loadfailedLayout.setVisibility(View.VISIBLE);
		}
		@Override 
        public void onPageFinished(WebView view,String url) 
        { 
			loadingLayout.setVisibility(View.GONE);
			loadfailedLayout.setVisibility(View.GONE);
        } 
	}

	@Override
	public void update() {
	}

	@Override
	public void onResume() {
		if(browser.getUrl()==null);
		browser.loadUrl(Urls.LIB_URL);
		NetworkChangeReceiver.registNetWorkListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		NetworkChangeReceiver.unRegistNetworkListener(this);
	}

	@Override
	public void onDataEnabled() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDataDisabled() {
		// TODO Auto-generated method stub
		
	}
}