package edu.hebtu.movingcampus.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.biz.ExamDao;
import edu.hebtu.movingcampus.biz.RoomDao;
import edu.hebtu.movingcampus.entity.ClassRoom;

public class Show_FreedRoom extends BaseActivity {
	private static int buildingid = 0;
	private static int schoolid = 0;
	private static int lable = 0;
	private LinearLayout parent;
	private Button btn_rquery;
	
	private boolean flag = false;
	private GridView gv_room;
	private ImageView img_show;
	private List<Map<String, String>> list_room = new ArrayList<Map<String, String>>();
	private String[] buildings;//教学楼的数组
	private String[] schools;//校区的数组
	private String[] units;//几节课的数组
	private String[] weekdays;//星期几的数组
	private String[] weeks;//第几周的数组
	private Spinner spinner_school;//校区的spinner
	private Spinner spinner_building;//教学楼的spinner
	private Spinner spinner_unit;//几节课的spinner
	private Spinner spinner_weekday;//星期几的spinner
	private Spinner spinner_weeks;//第几周的spinner
	private ArrayAdapter<String> adapter_School;//校区的adapter
	private ArrayAdapter<String> adapter_building;//教学楼的adapter
	private ArrayAdapter<String> adapter_unit;//几节课的adapter
	private ArrayAdapter<String> adapter_weekday;//星期几的adapter
	private ArrayAdapter<String> adapter_weeks;//第几周的adapter
	private SimpleAdapter simpleAdapter = null;
	private ArrayList<ClassRoom> roomlist = new ArrayList();
	private PopupWindow selectPopupWindow = null;
	private TextView tv_XnXq;
	private TextView tv_id;
	private TextView tv_name;
	private MyAdapter adapter;
	
	private String unit="";//选中的第几节课
	private String building="";//选中的教学楼
	private String school="";//选中的校区
	private String week="";//选中的第几周
	private String weekday="";//选中的周几
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(LayoutInflater.from(this).inflate(R.layout.freeroom,
				null));
		getMessage();
		InitList();//定义spinner
		InitSpinnerList();//定义spinner视图
		initView();//定义视图
		bindButton();//绑定按钮
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		this.img_show = ((ImageView) findViewById(R.id.img_pic));
		this.img_show.setBackgroundResource(R.drawable.btn_bg);
		this.tv_id = ((TextView) findViewById(R.id.tvscore_stuID));
		this.tv_name = ((TextView) findViewById(R.id.tvscore_StuName));
		this.tv_id.setText(AppInfo.getUser().getCid().toString().toCharArray(),
				0, AppInfo.getUser().getCid().toString().length());
		this.tv_name.setText(AppInfo.getUser().getUserName().toCharArray(), 0,
				AppInfo.getUser().getUserName().length());
		this.tv_XnXq = ((TextView) findViewById(R.id.tvpage_xnxq));
		String str = AppInfo.getXnXq().substring(0, 9) + "学年 第"
				+ AppInfo.getXnXq().substring(-1 + AppInfo.getXnXq().length())
				+ "学期";
		this.tv_XnXq.setText(str);
		this.gv_room = ((GridView) findViewById(R.id.gv_freedroom));
		adapter = new MyAdapter(null);
		gv_room.setAdapter(adapter);
	}

	private void InitList() {
		this.spinner_school = ((Spinner) findViewById(R.id.spinner_school));
		this.spinner_building = ((Spinner) findViewById(R.id.spinner_building));
		this.spinner_weeks = ((Spinner) findViewById(R.id.spinner_weeks));
		this.spinner_weekday = ((Spinner) findViewById(R.id.spinner_weekday));
		this.spinner_unit = ((Spinner) findViewById(R.id.spinner_unit));
		ArrayList<String> list_weekday = new ArrayList();
		ArrayList<String> list_school = new ArrayList();
		ArrayList<String> list_building = new ArrayList();
		ArrayList<String> list_weeks = new ArrayList();
		ArrayList<String> list_unit = new ArrayList();

		list_school.add("新校区");
		list_school.add("老校区");
		if ((this.building.equalsIgnoreCase(""))
				|| (this.building.equalsIgnoreCase("null"))) {
			list_building.add("全部");
			list_building.add("公教楼_A");
			list_building.add("公教楼_B");
			list_building.add("公教楼_C");
			list_building.add("公教楼_D");
			list_building.add("公教楼_E");
			// TODO
			String str1 = "3";
			list_weeks.add("本周");
			list_weeks.add("下一周");
			String str2 = String.valueOf(1 + Integer.parseInt(str1));
			list_weekday.add("星期日");
			list_weekday.add("星期一");
			list_weekday.add("星期二");
			list_weekday.add("星期三");
			list_weekday.add("星期四");
			list_weekday.add("星期五");
			list_weekday.add("星期六");
			list_unit.add("全部");
			list_unit.add("1-2节课");
			list_unit.add("3-5节课");
			list_unit.add("6-8节课");
			list_unit.add("9-11节课");
			list_unit.add("11-13节课");
			this.weekdays = (String[]) list_weekday
					.toArray(new String[list_weekday.size()]);
			this.weeks = (String[]) list_weeks.toArray(new String[list_weeks
					.size()]);
			this.units = (String[]) list_unit.toArray(new String[list_unit
					.size()]);
			this.buildings = (String[]) list_building
					.toArray(new String[list_building.size()]);
			this.schools = (String[]) list_school
					.toArray(new String[list_school.size()]);
			return;
		}
	}

	private void InitSpinnerList() {
		this.adapter_School = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.item, schools);

		this.spinner_school.setAdapter(adapter_School);
		this.adapter_School
				.setDropDownViewResource(R.layout.item);
		this.spinner_school
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if(arg2==0){
							school = "newCollege";
						}else{
							school = "oldCollege";
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						school = "新校区";
					}
				});
		this.adapter_building = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.item, buildings);

		this.spinner_building.setAdapter(adapter_building);
		this.adapter_building
				.setDropDownViewResource(R.layout.item);
		this.spinner_building
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if(arg2==0){
							building="all";
						}else if(arg2==1){
							building="A";
						}else if(arg2==2){
							building="B";
						}else if(arg2==3){
							building="C";
						}else if(arg2==4){
							building="D";
						}else{
							building="E";
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						building="all";
					}
				});
		this.adapter_unit = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.item, units);

		this.spinner_unit.setAdapter(adapter_unit);
		this.adapter_unit
				.setDropDownViewResource(R.layout.item);
		this.spinner_unit
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if(arg2==0){
							unit="all";
						}else if(arg2==1){
							unit="1";
						}else if(arg2==2){
							unit="3";
						}else if(arg2==3){
							unit="6";
						}else if(arg2==4){
							unit="9";
						}else{
							building="11";
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						unit="all";
					}
				});
		this.adapter_weekday = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.item, weekdays);

		this.spinner_weekday.setAdapter(adapter_weekday);
		this.adapter_weekday
				.setDropDownViewResource(R.layout.item);
		this.spinner_weekday
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if(arg2==0){
							weekday="sunday";
						}else if(arg2==1){
							weekday="monday";
						}else if(arg2==2){
							weekday="tuesday";
						}else if(arg2==3){
							weekday="wednsday";
						}else if(arg2==4){
							weekday="thursday";
						}else if(arg2==5){
							weekday="friday";
						}else{
							building="saturday";
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						weekday="sunday";
					}
				});
		this.adapter_weeks = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.item, weeks);

		this.spinner_weeks.setAdapter(adapter_weeks);
		this.adapter_weeks
				.setDropDownViewResource(R.layout.item);
		this.spinner_weeks
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if(arg2==0){
							week = "thisweek";
						}else{
							week = "nextweek";
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						week = "thisweek";
					}
				});
	}

	private void getMessage() {
		ArrayList<ClassRoom> res = (ArrayList<ClassRoom>) new ExamDao(Show_FreedRoom.this)
		.getFreeRoomMsg(false,school,building,week,weekday,unit);
Log.i("从服务器请求数据", "从服务器传回数据");
if (res != null) {
	Log.i("从服务器传回数据", "从服务器传回数据");
	ArrayList<ClassRoom> freerooms = (ArrayList<ClassRoom>) res;
	for (ClassRoom c : freerooms){
		ClassRoom classroom = new ClassRoom();
		classroom.setBuilding(c.getBuilding());
		classroom.setJc(c.getJc());
		classroom.setRoomid(c.getRoomid());
		classroom.setXiaoQu(c.getRoomid());
		classroom.setXq(c.getXq());
		classroom.setZc(c.getZc());
		classroom.setRoomname(c.getRoomname());
		roomlist.add(classroom);
	}
} else
// TODO{
{
	Toast.makeText(getApplicationContext(), "接口获取错误", Toast.LENGTH_LONG).show();
	
	for(int i = 0 ; i < 10 ; i++){
		ClassRoom classroom = new ClassRoom();
		classroom.setBuilding("公教楼");
		classroom.setJc("8~10节");
		classroom.setRoomid("2");
		classroom.setXiaoQu("新校区");
		classroom.setXq("第十一周");
		classroom.setZc("周三");
		classroom.setRoomname("222");
		roomlist.add(classroom);
	}
	
}
	}

	private int getWeek() {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(new Date());
		int i = -1 + localCalendar.get(7);
		if (i < 0)
			i = 0;
		return i;
	}


	void queryFreedRoom() {
		this.list_room.clear();
		if ((this.spinner_school.getSelectedItem().toString()
				.equalsIgnoreCase(""))
				|| (this.spinner_building.getSelectedItem().toString()
						.equalsIgnoreCase(""))
				|| (this.buildings == null)
				|| (this.spinner_school.getSelectedItem().toString()
						.equals("null"))
				|| (this.spinner_building.getSelectedItem() == null)
				|| (this.spinner_building.getSelectedItem().equals("null"))) {
			Toast localToast1 = Toast.makeText(this, "学校或者楼号不能为空，请重新输入！", 0);
			localToast1.setGravity(17, 0, 0);
			localToast1.show();
			return;
		}

		if ((!this.spinner_weeks.getSelectedItem().toString()
				.equalsIgnoreCase(""))
				&& (!this.spinner_weekday.getSelectedItem().toString()
						.equalsIgnoreCase(""))
				&& (!this.spinner_unit.getSelectedItem().toString()
						.equalsIgnoreCase(""))) {
			String xiqu = this.spinner_school.getSelectedItem().toString();
			String zc = this.spinner_weeks.getSelectedItem().toString();
			// (String) this.list_weeks.get(this.spinner_weeks
			// .getSelectedItemPosition());
			String jc = this.spinner_unit.getSelectedItem().toString();
			// (String) this.list_unit.get(this.spinner_unit
			// .getSelectedItemPosition());
			String xq = this.spinner_weekday.getSelectedItem().toString();
			String str5 = this.spinner_building.getSelectedItem().toString();
			String bd = str5.substring(-2 + str5.length()).replace("_", " ")
					.trim();
			List<ClassRoom> res = new RoomDao(this).mapperJson(xiqu, jc, zc, xq,
					jc);
			if (res != null)
				this.roomlist = (ArrayList<ClassRoom>) res;
			else {
				Toast.makeText(this, (CharSequence) res, Toast.LENGTH_SHORT)
						.show();
			}

			if ((this.roomlist != null)
					&& (!this.roomlist.toString().equalsIgnoreCase("[]")))
				for (int i = 0;; i++) {
					if (i >= this.roomlist.size()) {
						this.simpleAdapter = new SimpleAdapter(this,
								this.list_room, R.layout.grid_room,
								new String[] { "room" },
								new int[] { R.id.text });
						this.gv_room.setAdapter(this.simpleAdapter);
						return;
					}
					HashMap localHashMap = new HashMap();
					localHashMap.put("room", this.roomlist.get(i)
							.getRoomLocation());
					this.list_room.add(localHashMap);
				}
			this.simpleAdapter = new SimpleAdapter(this, this.list_room,
					R.layout.grid_layout, new String[] { "room" },
					new int[] { R.id.img });
			this.gv_room.setAdapter(this.simpleAdapter);
			Toast localToast3 = Toast.makeText(this, "查询结果为空！", 0);
			localToast3.setGravity(17, 0, 0);
			localToast3.show();
			return;
		}
		Toast localToast2 = Toast.makeText(this, "星期或周次不能为空，请重新输入！", 0);
		localToast2.setGravity(17, 0, 0);
		localToast2.show();
	}

	public void dismiss() {
		this.selectPopupWindow.dismiss();
	}

	// @Override
	// public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
	// if (paramInt == 4) {
	// this.list_school.clear();
	// this.list_building.clear();
	// this.list_weeks.clear();
	// this.list_weekday.clear();
	// this.list_unit.clear();
	// }
	// return super.onKeyDown(paramInt, paramKeyEvent);
	// }

	//
	// public void onWindowFocusChanged(boolean paramBoolean) {
	// super.onWindowFocusChanged(paramBoolean);
	// while (true) {
	// if (this.flag)
	// return;
	// initWedget();
	// this.flag = true;
	// }
	// }
	
	private class MyAdapter extends BaseAdapter {
		private MyAdapter(Object object) {
		}

		@Override
		public int getCount() {
			return Show_FreedRoom.this.roomlist.size();
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
			paramView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.freeroom_item, null);
			TextView mycollege = (TextView) paramView.findViewById(R.id.freeroom_college);
			TextView mybuilding = (TextView) paramView.findViewById(R.id.freeroom_building);
			TextView myweek = (TextView) paramView.findViewById(R.id.freeroom_week);
			TextView myweekday = (TextView) paramView.findViewById(R.id.freeroom_weekday);
			TextView myclass = (TextView) paramView.findViewById(R.id.freeroom_class);
			TextView myroom = (TextView) paramView.findViewById(R.id.freeroom_room);
			ClassRoom classroom = roomlist.get(paramInt);
			System.out.print("这是输出的空闲教室"+classroom.toString());
			mycollege.setText(classroom.getXiaoQu());
			mybuilding.setText(classroom.getBuilding());
			myweek.setText(classroom.getXq());
			myweekday.setText(classroom.getZc());
			myclass.setText(classroom.getJc());
			myroom.setText(classroom.getRoomname());
			return paramView;
		}
	}
	private class SpinnerAdapter extends ArrayAdapter<String> {
		Context context;
		String[] items = new String[0];
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);

		public SpinnerAdapter(Context paramInt, int layout, String[] arg4) {
			super(paramInt, layout);
			this.items = arg4;
			this.context = paramInt;
		}

		@Override
		public View getDropDownView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (paramView == null)
				paramView = LayoutInflater.from(this.context).inflate(
						R.layout.option_item, paramViewGroup, false);

			TextView localTextView = (TextView) paramView
					.findViewById(R.id.item_text);
			localTextView.setGravity(48);
			localTextView.setTypeface(Typeface.DEFAULT, 0);
			localTextView.setLayoutParams(this.params);
			localTextView.setText(this.items[paramInt]);
			localTextView.setTextColor(Color.rgb(136, 28, 237));
			localTextView.setTextSize(12.0F);

			return localTextView;
		}

		@Override
		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			if (paramView == null)
				paramView = LayoutInflater.from(this.context).inflate(
						R.layout.option_item, paramViewGroup, false);

			TextView localTextView = (TextView) paramView
					.findViewById(R.id.item_text);
			localTextView.setGravity(48);
			localTextView.setTypeface(Typeface.DEFAULT, 0);
			localTextView.setLayoutParams(this.params);
			localTextView.setText(this.items[paramInt]);
			localTextView.setTextColor(Color.rgb(136, 28, 237));
			localTextView.setTextSize(12.0F);

			return localTextView;
		}
	}

	@Override
	protected void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Show_FreedRoom.this.finish();
					}
				});
		findViewById(R.id.btn_rquery).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Show_FreedRoom.this.queryFreedRoom();
				Message msg = new Message();
				msg.what=0;
				msg.arg1 = 3;
				myhandler.sendMessage(msg);
				roomlist.removeAll(roomlist);
				getMessage();//获得数据
				adapter.notifyDataSetChanged();
				Log.i("请求的参数",school+"\n"+building+"\n"+week+"\n"+weekday+"\n"+unit);
			}
		});
	}
	Handler myhandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0&&msg.arg1 == 3){
				initView();
			}
		}
		
	};

}