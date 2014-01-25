package edu.hebtu.movingcampus.activity.wrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.MainActivity;
import edu.hebtu.movingcampus.activity.base.PageWraper;

public class UlitiesActivity implements Handler.Callback, PageWraper {
	private static final Activity mainActivity = MainActivity.instance;
	private Intent intent;
	private AlertDialog menuDialog;
	private View menuView;
	private View content;

	public UlitiesActivity(View view) {
		this.content = view;
		bindButton();
	}

	private void bindButton() {
		content.findViewById(R.id.ulities_calls).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
					}
				});
		content.findViewById(R.id.ulities_function_wish).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
					}
				});
		content.findViewById(R.id.ulities_process_notice).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
					}
				});
		content.findViewById(R.id.ulities_street_map).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
					}
				});
		content.findViewById(R.id.ulities_map).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
					}
				});
	}


	public boolean onMenuOpened(int paramInt, Menu paramMenu) {
		if (this.menuDialog == null) {
			this.menuDialog = new AlertDialog.Builder(mainActivity).setView(
					this.menuView).create();
			this.menuDialog.getWindow().setGravity(80);
			this.menuDialog.show();
		}
		while (true) {
			return false;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}