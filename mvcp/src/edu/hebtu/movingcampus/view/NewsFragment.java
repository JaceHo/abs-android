package edu.hebtu.movingcampus.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.wrapper.IPreference;
import edu.hebtu.movingcampus.adapter.NewsListAdapter;
import edu.hebtu.movingcampus.biz.NewsDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.entity.NewsShort;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class NewsFragment extends BaseListFragment {

	public Activity mActivity;
	private String page;
	private long mUpdateDateTime;//最近一次的更新时间
	public boolean loaded=false;
	private NewsListAdapter mAdapter;
	private List<NewsShort>loadMoreEntity;
	private List<NewsShort> mlist;
	private static SharedPreferences mPreferences;
	private static SimpleDateFormat mDateFormat;

	// private DisplayImageOptions options;
	static {
			Looper.prepare();
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				updateTextTime();
				mAdapter.appendToList(loadMoreEntity);
				loaded=true;
				break;
			case 1:
				loaded=true;
				break;
				
				default :
					loaded=false;
					break;
			}
			onStopLoad();
		}

	};

	// add this constructor by King0769, 2013/5/7
	// in order to solve an exception that "can't instantiate class cn.eoe.app.view.NewsFragment; no empty constructor"
	// I found it in this case : 1.open eoe program -> 2.change system language -> 3.reopen eoe, can see FC(force close)
	// I think this bug will happens in many cases.
	public NewsFragment() {
		super();
		page="0";
		if (mDateFormat == null) {
			mDateFormat = new SimpleDateFormat("MM月dd日");
		}
	}
	//--------------------
	public static NewsFragment getInstance(String page,Activity activty){
		NewsFragment nf=new NewsFragment();
		nf.mActivity=activty;
		nf.page=page;
		Log.w("page", page+"");
		nf.mlist=IPreference.getInstance(activty).getListOfNewsSubjectByID(Integer.parseInt(page)+1).dump(activty);
		return nf;
	}

	/** 获取channel的刷新时间 **/
	private long getUpdateDateTime() {
		return mPreferences.getLong(Constants.PREFER_FILE+ "_" + page, 0);
	}

	/** 设置channel的刷新时间 **/
	private void setUpdateDateTime(long nowTime) {
		if (mPreferences.edit().putLong(Constants.PREFER_FILE+ "_" + page, nowTime).commit()) {
			mUpdateDateTime = nowTime;
		}
	}
	private void updateTextTime() {
		boolean isAdded = isAdded();
		if (!isAdded) {//avoid java.lang.IllegalStateException: Fragment BaseFragment{44b01260} not attached to Activity
			return;
		}
		if(mUpdateDateTime == 0) {//初始化更新
			listview.setRefreshTime(getResources().getString(R.string.listview_header_last_time));
		} else {//其他
			long diffTimeSecs = (System.currentTimeMillis() - mUpdateDateTime) / 1000;
			//1min = 60s ; 1h = 60min
			if (diffTimeSecs < 3600) {//一小时内，显示分钟
				Resources resources = getResources();
				if (resources != null) {
					listview.setRefreshTime(resources.getString(
							R.string.listview_header_last_time_for_min,
							diffTimeSecs < 60 ? 1 : diffTimeSecs / 60));
				}
			} else {
				long diffTimeHours = diffTimeSecs / 3600;
				if(diffTimeHours < 24) {//一天内更新，显示小时
					listview.setRefreshTime(getResources().getString( R.string.listview_header_last_time_for_hour, diffTimeHours));
				} else if(diffTimeHours == 24) {//一天更新，显示1天
					listview.setRefreshTime(getResources().getString( R.string.listview_header_last_time_for_day, 1));
				} else {//大于24小时显示xx月xx日
					listview.setRefreshTime(getResources().getString( R.string.listview_header_last_time_for_date, mDateFormat.format(new Date(mUpdateDateTime))));
				}
			}
		}
		setUpdateDateTime(System.currentTimeMillis());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
        mPreferences = getActivity().getSharedPreferences(Constants.PREFER_FILE, Context.MODE_PRIVATE);
		mUpdateDateTime = getUpdateDateTime();
		updateTextTime();
		listview.setXListViewListener(this);
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(true);
		// construct the RelativeLayout
		mAdapter = new NewsListAdapter(mActivity, R.layout.news_item, listview,this);
		mAdapter.setList(mlist);
		mAdapter.appendToList(loadMoreEntity);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NewsShort item = (NewsShort) mAdapter
						.getItem(position);
				startDetailActivity(mActivity, item.getID()+"");
			}
		});
		return view;
	}

	/**
	 * 按时间排序
	 */
	@Override
	public void onRefresh() {
		onStopLoad();
		mAdapter.getList().clear();
		new Thread() {
			public void run(){
				NewsFragment.this.onLoad();
				super.run();
			}
		}.start();
	}

	public List<NewsShort> onLoad(){
		loadMoreEntity=new NewsDao(mActivity).mapperJson(true,(Integer.parseInt(page)+1)+"", (mlist.size()+1)+"",null);
		if (loadMoreEntity!= null) {
			mHandler.sendEmptyMessage(0);
		}
		return loadMoreEntity;
	}

	@Override
	public void onLoadMore() {
		if (loadMoreEntity==null) {
			mHandler.sendEmptyMessage(1);
			return;
		} else {
			new Thread() {
				@Override
				public void run() {
					loadMoreEntity=new NewsDao(mActivity).mapperJson(true,(Integer.parseInt(page)+1)+"", (mlist.size()+1)+"",null);
					if (loadMoreEntity!= null) {
						mHandler.sendEmptyMessage(0);
					}
					super.run();
				}
			}.start();

		}

	}
	public String getPage(){
		return page;
	}

}
