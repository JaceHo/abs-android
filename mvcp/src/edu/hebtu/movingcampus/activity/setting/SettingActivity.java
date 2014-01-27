package edu.hebtu.movingcampus.activity.setting;

import java.io.File;

import com.umeng.fb.FeedbackAgent;

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
import edu.hebtu.movingcampus.activity.MainActivity;
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
						   new FeedbackAgent(SettingActivity.this).startFeedbackActivity();
					}
				});

		findViewById(R.id.ll_version).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						//TODO
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
}