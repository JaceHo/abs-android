package edu.hebtu.movingcampus.activity.setting;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.activity.login.SplashActivity;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.utils.ToolUtils;

/**
 * setting user interests in this app,and preference about activity or
 * notifications
 * 
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:33 AM
 */

public class SettingActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.settings);
		if (SplashActivity.upgradeAble)
			findViewById(R.id.newapp).setVisibility(View.VISIBLE);
		else
			findViewById(R.id.newapp).setVisibility(View.GONE);
		bindButton();
	}

	protected void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SettingActivity.this.finish();
					}
				});

		findViewById(R.id.ll_info).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(SettingActivity.this,
								NewstypePreferSetting.class));
					}
				});

		findViewById(R.id.ll_local).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(SettingActivity.this,
								LocalPreferSetting.class));
					}
				});

		findViewById(R.id.ll_code).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(SettingActivity.this,
								Share_Quickmark.class));
					}
				});

		findViewById(R.id.ll_feed).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(SettingActivity.this,
								FeedBack.class));
					}
				});

		findViewById(R.id.ll_version).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (SplashActivity.upgradeAble)
							showUpdateDialog();
					}
				});

		findViewById(R.id.ll_about).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(SettingActivity.this,
								About.class));
					}
				});

		findViewById(R.id.ll_flow).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(SettingActivity.this,
								NetWorkFlow.class));
					}
				});

		findViewById(R.id.ll_cache).setOnClickListener(
				new View.OnClickListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onClick(View v) {
						mRunningTask = new AsyncTask<File, Integer, Integer>() {

							@Override
							protected Integer doInBackground(File... params) {
								File f = params[0];
								int size = (int) (ToolUtils.dirSize(f)/(1024*1024));
								ToolUtils.clearCacheFolder(f, 10);
								return size;
							}

							@Override
							protected void onPostExecute(Integer result) {
								Toast.makeText(SettingActivity.this,
										"为您清理了" + result + "M缓存",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							protected void onProgressUpdate(Integer... progress) {
							}
						};
						mRunningTask.execute(new File[] { AppInfo.cacheDir });
					}
				});

		findViewById(R.id.btn_exit).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						SharedPreferences.Editor editor= SettingActivity.this.getSharedPreferences(
								Constants.PREFER_FILE, 0).edit();
						editor.putString("username", "");
						editor.putString("password", "");
						editor.commit();
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addCategory(Intent.CATEGORY_HOME);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});

	}
	protected void showUpdateDialog() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
		localBuilder.setTitle("版本升级");
		localBuilder.setMessage("请注意，河北师大移动校园有新版本，需要进行版本升级吗？"
				+ SplashActivity.info.getInformation().toString());
		localBuilder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
						SettingActivity.this.downLoadApk();
					}
				});
		localBuilder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
					}
				});
		localBuilder.create().show();
	}
	protected void downLoadApk() {
		final ProgressDialog localProgressDialog = new ProgressDialog(this);
		localProgressDialog.setProgressStyle(1);
		localProgressDialog.setMessage("正在下载更新");
		localProgressDialog.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File localFile = new ToolUtils(SettingActivity.this)
							.getFileFromServer(SplashActivity.info.getUrl(),
									localProgressDialog,
									SplashActivity.info.getApk());
					sleep(3000L);
					installApk(localFile);
					localProgressDialog.dismiss();
					return;
				} catch (Exception localException) {
					Message localMessage = new Message();
					localMessage.what = 3;
					// SettingActivity.this.handler.sendMessage(localMessage);
					localException.printStackTrace();
				}
			}
		}.start();
	}

	protected void installApk(File paramFile) {
		Intent localIntent = new Intent();
		localIntent.setAction("android.intent.action.VIEW");
		localIntent.setDataAndType(Uri.fromFile(paramFile),
				"application/vnd.android.package-archive");
		startActivity(localIntent);
	}
}