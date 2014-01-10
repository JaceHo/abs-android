package edu.hebtu.movingcampus.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.biz.ExamDao;
import edu.hebtu.movingcampus.entity.ExamScore;
import edu.hebtu.movingcampus.entity.User;

public class Show_Score extends BaseActivity {
	private final int ITEM_BACK = 0;
	private final int ITEM_EDIT = 2;
	private final int ITEM_EXIT = 3;
	private final int ITEM_HOME = 1;
	private ImageView img_show;
	private ListView lv_show;
	private AlertDialog menuDialog;
	private GridView menuGridView;
	private View menuView;
	private ExamScore[] scoreArray;
	private ArrayList<ExamScore> scorelist = new ArrayList<ExamScore>();
	private TextView tv_XnXq;
	private TextView tv_id;
	private TextView tv_name;
	private TextView person_info;
	private Spinner spn_year;
	private Spinner spn_pro;
	private Spinner spn_term;//学期数
	private static String[] year = { "历年成绩", "2011~2012", "2012~2013",
			"2013~2014", "2014~2015" };
	private static final String[]term = {"全部","第一学期","第二学期"};
	private static final String[] property = { "全部", "专业必修", "专业选修", "公共选修",
			"公共必修", "素质类课程" };
	private ArrayAdapter<String> adpt_year ;// 课程年份的适配器
	private ArrayAdapter<String> adpt_pro;// 课程属性的适配器
	private ArrayAdapter<String> adpt_term;// 学期属性的适配器
	
	
	private String courseyear="历年成绩";//学年
	private String courseterm="全部";//学期
	private String courseType="全部";//课程类型
	
	private MyAdapter myAdapter = new MyAdapter(null);
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(LayoutInflater.from(this).inflate(R.layout.score_show,
				null));
		setYear();
		setSpinner();
		insertData();
		bindButton();
		setScoreView();
		// setMenu();
		
	}
	//给选择学年的spinner添加数据源
	private void setYear() {
		// TODO Auto-generated method stub
		String stuNum = AppInfo.getUser().getCid();
		int yearOfJoinCollege =Integer.valueOf(stuNum.substring(0, 4));
		Calendar calendar = Calendar.getInstance();
		int myyear = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		String data = "历年成绩";
		if(month<=8){
		for(int i =yearOfJoinCollege;i<myyear;i++){
			data+="_"+i+"~"+(i+1);
			Log.i("年数", i+"");
		}
		}else{
			for(int i =yearOfJoinCollege;i<=myyear;i++){
				Log.i("年数", i+"");
				data+="_"+i+"~"+(i+1);
			}
		}
		year = data.split("_");
	}

	private void setSpinner() {
		
		//初始化spinner
		this.spn_year = (Spinner) findViewById(R.id.spinner_course_year);
		this.spn_pro = (Spinner) findViewById(R.id.spinner_course_pro);
		this.spn_term = (Spinner)findViewById(R.id.spinner_course_term);
		Log.i("判断spnterm是否为空",spn_term.toString());
		adpt_year = new ArrayAdapter<String>(this,
				android.R.layout.simple_gallery_item, year);
		adpt_pro = new ArrayAdapter<String>(this,
				android.R.layout.simple_gallery_item, property);
		adpt_term = new  ArrayAdapter<String>(this,
				android.R.layout.simple_gallery_item, term);
		
		// 设置下拉列表的风格
		adpt_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adpt_pro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adpt_term.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter添加到spinner
		System.out.print("这是spinner加adapter前");
		spn_year.setAdapter(adpt_year);
		spn_pro.setAdapter(adpt_pro);
		spn_term.setAdapter(adpt_term);
		System.out.print("这是spinner加adapter后");
		spn_pro.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2==0){
					courseyear = "all";
				}else if(arg2==1){
					courseyear = "zybx";
				}else if(arg2==2){
					courseyear = "zyxx";
				}else if(arg2==3){
					courseyear = "ggxx";
				}else if(arg2==4){
					courseyear = "ggbx";
				}else{
					courseyear = "sz";
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				courseType  = "all";
			}
		});
		spn_year.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2==0){
					courseType="all";
				}else{
				courseType  = year[arg2];
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
				courseyear  = "all";
			}
		});
		spn_term.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2==0){
					courseterm = "all";
				}else if(arg2==1){
					courseterm = "first";
				}else{
					courseyear = "second";
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				courseterm  = "all";
			}
		});
		// 设置默认值
		spn_year.setVisibility(View.VISIBLE);
		spn_pro.setVisibility(View.VISIBLE);
		spn_term.setVisibility(View.VISIBLE);
	}

	// 插入信息
	private void insertData() {
		ArrayList<ExamScore> res = (ArrayList<ExamScore>) new ExamDao(this)
				.getExamResultMsg(false, courseyear, courseterm,courseType);
		Log.i("从服务器请求数据", "从服务器传回数据");
		if (res != null) {
			Log.i("从服务器传回数据", "从服务器传回数据");
			ArrayList<ExamScore> courseScores = (ArrayList<ExamScore>) res;
			for (ExamScore c : courseScores){
				ExamScore examscore = new ExamScore();
				examscore.setAttribute(c.getAttribute());
				examscore.setCredit(c.getCredit());
				examscore.setName(c.getName());
				examscore.setScore(c.getScore());
				this.scorelist.add(examscore);
			}
		} else
		// TODO{
		{
			Toast.makeText(this, "接口获取错误", Toast.LENGTH_LONG).show();
			
			for(int i = 0 ; i < 10 ; i++){
				ExamScore examscore = new ExamScore();
				examscore.setName("java");
				examscore.setCredit("4");
				examscore.setAttribute("3.3");			
				examscore.setScore("99");
				this.scorelist.add(examscore);
			}
			
		}
	}

	private void setScoreView() {

		this.scoreArray = (this.scorelist.toArray(new ExamScore[scorelist.size()]));
		this.tv_id = ((TextView) findViewById(R.id.tvscore_stuID));
		this.tv_name = ((TextView) findViewById(R.id.tvscore_StuName));
		this.tv_XnXq = ((TextView) findViewById(R.id.tvpage_xnxq));
		String str = AppInfo.getXnXq().substring(0, 9) + "学年 第"
				+ AppInfo.getXnXq().substring(-1 + AppInfo.getXnXq().length())
				+ "学期";
		this.tv_XnXq.setText(str);
		this.tv_id.setText(AppInfo.getUser().getUserName().toCharArray(), 0,
				AppInfo.getUser().getUserName().length());
		this.tv_name.setText(AppInfo.getUser().getUserName().toCharArray(), 0,
				AppInfo.getUser().getUserName().length());

		this.img_show = ((ImageView) findViewById(R.id.img_pic));
		this.img_show.setBackgroundResource(R.drawable.btn_bg);
		this.lv_show = ((ListView) findViewById(R.id.lv_score));
		this.lv_show.setCacheColorHint(Color.rgb(231, 230, 216));
		this.lv_show.setAdapter(myAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu paramMenu) {
		paramMenu.add("menu");
		return super.onCreateOptionsMenu(paramMenu);
	}

	@Override
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

	private class MyAdapter extends BaseAdapter {
		private MyAdapter(Object object) {
		}

		@Override
		public int getCount() {
			return Show_Score.this.scoreArray.length;
		}

		@Override
		public Object getItem(int paramInt) {
			return Integer.valueOf(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return paramInt;
		}

		@Override
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			
//			if (paramView != null)
//				;
//			
//			// for (
//			TextView localTextView = new TextView(
//					Show_Score.this.getApplicationContext());
//			// {
//			localTextView.setText(Show_Score.this.scoreArray[paramInt]);
//			localTextView.setPadding(10, 0, 0, 0);
//			localTextView.setTextSize(16.0F);
//			localTextView.setTextColor(-16777216);
//			return localTextView;
			// }
			paramView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.score_course_items, null);
			TextView course_name = (TextView) paramView.findViewById(R.id.tv_course_name);
			TextView course_attribute = (TextView) paramView.findViewById(R.id.tv_course_attribution);
			TextView course_score = (TextView) paramView.findViewById(R.id.tv_course_score);
			TextView course_credit = (TextView) paramView.findViewById(R.id.tv_course_credit);
			ExamScore examscore = scorelist.get(paramInt);
			System.out.print("这是输出的成绩单"+examscore.getName());
			course_attribute.setText(examscore.getAttribute());
			course_name.setText(examscore.getName());
			course_score.setText(examscore.getScore());
			course_credit.setText(examscore.getCredit());
			return paramView;
		}
	}

	@Override
	protected void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Show_Score.this.finish();
					}
				});
		findViewById(R.id.btn_course_search).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//初始化想服务器传递的参数各个属性的默认值
				Message msg = new Message();
				msg.what=0;
				msg.arg1 = 3;
				myhandler.sendMessage(msg);
				scorelist.removeAll(scorelist);
				insertData();//获得数据
				myAdapter.notifyDataSetChanged();
				Log.i("请求的参数",courseterm+"\n"+courseType+"\n"+courseyear);
				
			}
		});
	}
	Handler myhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0&&msg.arg1 == 3){
				setScoreView();
			}
		}
		
	};
}
