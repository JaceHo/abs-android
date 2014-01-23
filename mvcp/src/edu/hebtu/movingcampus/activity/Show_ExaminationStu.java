package edu.hebtu.movingcampus.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.adapter.ExamInforAdapter;
import edu.hebtu.movingcampus.biz.ExamDao;
import edu.hebtu.movingcampus.entity.ExamineInfor;

public class Show_ExaminationStu extends BaseActivity {
	//the source of list of this activity
	private AlertDialog menuDialog;//the menu builder of this activity
	private View menuView;//the view that will be used to create a menu
	//the listview that used to show the list of examnation's infomation
	private ListView examination_show_list;
	private ExamInforAdapter adapter;
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(LayoutInflater.from(this).inflate(R.layout.examination_show,
				null));
		adapter=new ExamInforAdapter();
		setExamData();
		setExamView();
		bindButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu paramMenu) {
		paramMenu.add("menu");
		return super.onCreateOptionsMenu(paramMenu);
	}

	/**
	 * get the examination's infomation
	 * 
	 */
	private void setExamData() {
		// 
		List<ExamineInfor> res = new ExamDao(this).getExamPlanMsg(false);
		if (res != null){
		System.out.print("这网络忏悔数据"+res);
		}else {

			res= new ArrayList<ExamineInfor>();
			ExamineInfor ee = new ExamineInfor();
			ee.setBh(32 + "");
			ee.setKcm("javaee");
			ee.setKsrq("2013.12.20-22");
			ee.setKssj("12:00-18:00");
			ee.setRoomid("A301");
			ee.setKssj("22:00-24:00");
			for(int i =0;i<10;i++){
				res.add(ee);
				System.out.print("这是考试日期"+ee.toString());
			}
			
		}
		adapter.appendToList(res);
	}

	public void setExamView(){
		this.examination_show_list = (ListView) findViewById(R.id.examination_show_list);
		this.examination_show_list.setCacheColorHint(Color.rgb(231, 230, 216));
		this.examination_show_list.setAdapter(adapter);
	}

	@Override//this munuView is null, maybe it will cause some error
	public boolean onMenuOpened(int paramInt, Menu paramMenu) {
		if (this.menuDialog == null) {
			this.menuDialog = new AlertDialog.Builder(this).setView(
					this.menuView).create();
			this.menuDialog.getWindow().setGravity(80);
			this.menuDialog.show();
		}
		while (true) {
			return false;
		}
	}
	@Override
	protected void bindButton() {
		// TODO Auto-generated method stub
		
	}
}
