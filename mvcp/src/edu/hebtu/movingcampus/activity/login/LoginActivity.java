package edu.hebtu.movingcampus.activity.login;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.MainActivity;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.activity.setting.SettingActivity;
import edu.hebtu.movingcampus.activity.wrapper.IPreference;
import edu.hebtu.movingcampus.biz.CardDao;
import edu.hebtu.movingcampus.biz.UserDao;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.entity.User;
import edu.hebtu.movingcampus.enums.NewsType;
import edu.hebtu.movingcampus.utils.NetWorkHelper;
import edu.hebtu.movingcampus.utils.Utils;

public class LoginActivity extends BaseActivity {
	public static final String KEY = "dbkey";
	// TODO
	public static final String SharedName = null;
	public static String xn;
	public static String xq;
	private ProgressDialog processBar;
	private CheckBox cb_save;
	private InputMethodManager manager;
	private EditText passwordET;
	private EditText usernameET;
	private UserDao dao;

	public Boolean TEMP = false;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_login);
		dao=new UserDao(this);
		mRunningTask = new LoginTask(dao);

		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(LoginActivity.this, "api_key"));
		try {
			// 做版本判断
			int version = getPackageManager().getPackageInfo(getPackageName(),
					0).versionCode;
			if (version != getSharedPreferences(Constants.PREFER_FILE, 0)
					.getInt("version", 0)) {
				// 第一次打开本版本应用
				SharedPreferences.Editor editor = getSharedPreferences(
						Constants.PREFER_FILE, 0).edit();
				editor.putInt("version", version);
				editor.commit();
				// getIntent().putExtra("newToThis", true);
				TEMP = true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		try {
			// 此段代码什么意思？？
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
		} catch (Exception e) {
		}
		// 获取焦点，显示软键盘
		this.manager = ((InputMethodManager) getSystemService("input_method"));
		if (!manager.isActive())
			manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		this.usernameET = ((EditText) findViewById(R.id.username));
		this.passwordET = ((EditText) findViewById(R.id.password));
		this.cb_save = ((CheckBox) findViewById(R.id.cbrmpassword));
		// getMessage(); //获取本地存储的用户用户名和密码吗？没有必要，，在SplashActivity中已经做了判断
		bindButton();
	} // OnCreate()方法结束

	private View.OnClickListener loginButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View paramAnonymousView) {
			mRunningTask.execute(new Object[]{new Object()});
		}
	};

	protected void bindButton() {
		((Button) findViewById(R.id.sign_in_button))
				.setOnClickListener(this.loginButtonListener);

		usernameET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.i("text", s.toString());
				if (s != null && s.length() == 10
						&& passwordET.getText().toString() != null
						&& passwordET.getText().toString().length() == 6) {
					LoginActivity.this.toHome();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});
		passwordET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.i("text", s.toString());
				if (s != null && s.length() == 6
						&& usernameET.getText().toString() != null
						&& usernameET.getText().toString().length() == 10) {
					LoginActivity.this.toHome();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});
	}

	private void getMessage() {
		SharedPreferences localSharedPreferences = super.getSharedPreferences(
				Constants.PREFER_FILE, 0);
		String str1 = localSharedPreferences.getString("username", "");
		String str2 = localSharedPreferences.getString("password", "");
		this.usernameET.setText(str1);
		this.passwordET.setText(str2);
		this.cb_save.setChecked(true);
	}

	private void toHome() {
		// 得到用户名和密码

		mRunningTask.execute(new Object[]{new Object()});
		// 向服务器发送请求，得到返回的值（User类对象）

	}// toHome()函数结束

	@Override
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {

		if (paramInt == KeyEvent.KEYCODE_BACK) {
			Log.i("msg", "点击返回键，执行了");
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		if ((paramInt == 4) && (paramKeyEvent.getRepeatCount() == 0)) {
			if (this.processBar != null)
				this.processBar.dismiss();
			finish();
			System.exit(0);
		}

		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		if ((paramMotionEvent.getAction() == 0) && (getCurrentFocus() != null)
				&& (getCurrentFocus().getWindowToken() != null))
			this.manager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 2);
		return super.onTouchEvent(paramMotionEvent);
	}

	private class LoginTask extends AsyncTask<Object, Void, User> {
		private UserDao dao;
		public LoginTask(UserDao dao){
			this.dao=dao;
		}
		@Override
		protected void onPreExecute() {
			if ((LoginActivity.this.usernameET.getText().toString().length() == 10)
					&& (LoginActivity.this.passwordET.getText().toString()
							.length() == 6)) {
				LoginActivity.this.processBar = ProgressDialog.show(
						LoginActivity.this, "", "正在登录，请稍后...");
				LoginActivity.this.processBar.onStart();
				LoginActivity.this.processBar.show();
				LoginActivity.this.processBar.setCancelable(true);
			} else {
				Toast localToast = Toast.makeText(LoginActivity.this,
						"用户名或者密码格式不正确", 0);
				// localToast.setGravity(17, 0, 0);
				localToast.show();
			}
			super.onPreExecute();
		}

		@Override
		protected User doInBackground(Object... params) {
			String name = LoginActivity.this.usernameET.getText().toString()
					.trim();
			String password = LoginActivity.this.passwordET.getText()
					.toString().trim();
			return dao.mapperJson(name, password);
		}

		@Override
		protected void onPostExecute(User res) {
			// 如果为获取到信息或修改了一卡通状态,但状态未变化
			// 如果返回的对象值为空
			try {
				if (res == null) {
					Log.i("LoginActivity", "返回的对象值为空");
					Log.i("LoginActivity", "用户名或者密码错误");
					if (!NetWorkHelper.isNetworkAvailable(LoginActivity.this)) {
						Toast.makeText(LoginActivity.this, "您暂时没有可用的网络,请检查网络",
								0).show();
						startActivity(new Intent(
								"android.settings.WIRELESS_SETTINGS"));
						return;
					} else {
						LoginActivity.this.processBar.dismiss();
						Toast localToast2 = Toast.makeText(LoginActivity.this,
								res == null ? "服务器内部错误" : res.toString()
										+ "，请重输", 0);
						localToast2.setGravity(17, 0, 0);
						localToast2.show();
					}
					LoginActivity.this.usernameET.setText("");
					LoginActivity.this.passwordET.setText("");
					LoginActivity.this.usernameET.setFocusable(true);
					return;
				}
			} catch (Exception localException) {
				Toast localToast1 = Toast.makeText(LoginActivity.this,
						"用户名信息异常，请注意", 0);
				localToast1.setGravity(17, 0, 0);
				localToast1.show();
				LoginActivity.this.passwordET.setText("");
				LoginActivity.this.usernameET.setFocusable(true);
				return;
			}

			// 返回的对象值不为空
			if (res != null) {
				// 得到SharedPreferences，将此用户的用户名和密码存入本地数据库，以便下一次登陆时直接进入主界面
				SharedPreferences.Editor localEditor = getSharedPreferences(
						Constants.PREFER_FILE, 0).edit();
				if (LoginActivity.this.cb_save.isChecked()) {
					Log.i("LoginActivity", "选择了记住密码，正在保存用户名和密码");
					localEditor.putInt("count", 1);
					localEditor.putString("username",
							LoginActivity.this.usernameET.getText().toString());
					localEditor.putString("password",
							LoginActivity.this.passwordET.getText().toString());
					localEditor.commit();
				}
				// 保存user对象信息
				AppInfo.setUser(res);
				Log.d("user:", AppInfo.getUser().toString());
				// 如果该登陆用户是老师或者学生，则执行if内语句
				if (AppInfo.getUser().getRoleName().equals("学生")
						|| AppInfo.getUser().getRoleName().equals("老师")) {
					Log.i("msg", "进入到if语句中来了");
					Log.i("该用户是", AppInfo.getUser().getRoleName());
					localEditor.putString(LoginActivity.KEY, AppInfo.getUser()
							.getJid());

					/**
					 * 以下代码为改动代码
					 */
					final SharedPreferences.Editor edit = LoginActivity.this
							.getSharedPreferences(Constants.PREFER_FILE, 0)
							.edit();
					// 启动一线程
					new Thread(new Runnable() {
						@Override
						public void run() {
							IPreference.getInstance(LoginActivity.this);

							if (TEMP) {
								for (int i = 0; i < NewsType.values().length - 1; i++) {
									edit.putBoolean("news_" + i, true);
									edit.commit();
								}
							}
							if (TEMP) {
								Log.i("msg", "开启Whatsnew");
								// 第一次使用，显示ViewPager，进行展示
								LoginActivity.this.processBar.dismiss();
								LoginActivity.this.startActivity(new Intent(
										LoginActivity.this, Whatsnew.class));
							} else { // 为空执行
								Log.i("msg", "直接跳转MainActivity");
								// 直接跳转，进入MainActivity
								LoginActivity.this.processBar.dismiss();
								LoginActivity.this
										.startActivity(new Intent(
												LoginActivity.this,
												MainActivity.class));
							}
							LoginActivity.this.finish();
						}// run方法结束
					}).start();
					return;
				}
				Toast.makeText(LoginActivity.this, "用户请重新确认！", 0).show();
				LoginActivity.this.passwordET.setText("");
				LoginActivity.this.usernameET.setFocusable(true);
			}
		}
	};
}