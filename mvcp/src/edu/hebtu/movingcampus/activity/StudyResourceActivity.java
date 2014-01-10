package edu.hebtu.movingcampus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.PageWraper;
import edu.hebtu.movingcampus.config.Constants;

public class StudyResourceActivity implements Handler.Callback, PageWraper {
	private static final Activity mainActivity = MainActivity.instance;
	private static String Schedule;
	private static String defaultXq;
	private Intent intent;
	private AlertDialog menuDialog;
	private View menuView;
	private View content;

	public StudyResourceActivity(View view) {
		this.content = view;
		Schedule = "true";
		defaultXq = "2013-2014学年第1学期";
		AppInfo.setXnXq(defaultXq);
		bindButton();
	}

	private void bindButton() {
		content.findViewById(R.id.choosecourse).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
						StudyResourceActivity.this.toselectCourse();
					}
				});
		content.findViewById(R.id.classcourse).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
						StudyResourceActivity.this.toClassCourse();
					}
				});
		content.findViewById(R.id.personcourse).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
						StudyResourceActivity.this.toPresonCources();
					}
				});
		content.findViewById(R.id.examconsult).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
						StudyResourceActivity.this.toCoursesScore();
					}
				});
		content.findViewById(R.id.freeroom).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
						StudyResourceActivity.this.toFreedClassRoom();
					}
				});
		content.findViewById(R.id.examschedule).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View paramAnonymousView) {
						StudyResourceActivity.this.toexamPage();
					}
				});
	}

	private void dialog() {
		new AlertDialog.Builder(mainActivity)
				.setTitle("退出程序")
				.setMessage("确定退出程序？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
						Intent localIntent = new Intent(
								"android.intent.action.MAIN");
						localIntent.addCategory("android.intent.category.HOME");
						StudyResourceActivity.this.menuDialog.dismiss();
						StudyResourceActivity.mainActivity.finish();
						StudyResourceActivity.mainActivity
								.startActivity(localIntent);
						System.exit(0);
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
						StudyResourceActivity.this.menuDialog.dismiss();
					}
				}).create().show();
	}

	private void toClassCourse() {
		if (Schedule.equalsIgnoreCase("true")) {
			this.intent = new Intent(mainActivity, ShowCourse.class);
			this.intent.putExtra("classcourseinfor", defaultXq.toString());
			this.intent.putExtra("domain", Constants.COURSE_DOMAIN.CLASS);
			mainActivity.startActivityForResult(this.intent, 6);
			return;
		}
		Toast localToast = Toast.makeText(mainActivity, "对不起，该学期的课程尚未对外开放", 0);
		localToast.setGravity(17, 0, 0);
		localToast.show();
	}

	private void toPresonCources() {
		if (Schedule.equalsIgnoreCase("true")) {
			this.intent = new Intent(mainActivity, ShowCourse.class);
			this.intent.putExtra("courseinfor", defaultXq.toString());
			if (AppInfo.getUser().getRoleName().equals("学生"))
				this.intent.putExtra("domain", Constants.COURSE_DOMAIN.STUDENT);
			else if (AppInfo.getUser().getRoleName().equals("老师"))
				this.intent.putExtra("domain", Constants.COURSE_DOMAIN.TEACHER);
			mainActivity.startActivityForResult(this.intent, 2);
			return;
		}
		Toast localToast = Toast.makeText(mainActivity, "对不起，该学期的课程未对外开放", 0);
		localToast.setGravity(17, 0, 0);
		localToast.show();
	}

	private void toCoursesScore() {
		this.intent = new Intent(mainActivity, Show_Score.class);
		this.intent.putExtra("scoreinfor", defaultXq.toString());
		mainActivity.startActivityForResult(this.intent, 1);
	}

	private void toFreedClassRoom() {
		this.intent = new Intent(mainActivity, Show_FreedRoom.class);
		this.intent.putExtra("roominfor", defaultXq.toString());
		mainActivity.startActivityForResult(this.intent, 8);
	}

	private void toexamPage() {
		this.intent = new Intent(mainActivity, Show_ExaminationStu.class);
		this.intent.putExtra("examineinfor", defaultXq.toString());
		mainActivity.startActivityForResult(this.intent, 7);
	}

	private void toselectCourse() {
		this.intent = new Intent(mainActivity, Show_Selectcourse.class);
		this.intent.putExtra("selectcourseinfor", defaultXq.toString());
		mainActivity.startActivityForResult(this.intent, 5);
	}

	@Override
	public boolean handleMessage(Message paramMessage) {
		Bundle localBundle = paramMessage.getData();
		switch (paramMessage.what) {
		default:
		case 1:
		}
		return false;
	}

	public void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent) {
		// TODO
		// mainActivity.onActivityResult(paramInt1, paramInt2, paramIntent);
		// if (paramInt1 == 1)
		// switch (paramInt2) {
		// case 0:
		// case 1:
		// default:
		// case -1:
		// case 2:
		// }
		// do {
		// return;
		// } while (paramInt1 != 8);
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((paramInt == 4) && (paramKeyEvent.getRepeatCount() == 0))
			dialog();
		return false;
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
}