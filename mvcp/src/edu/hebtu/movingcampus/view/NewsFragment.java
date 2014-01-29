package edu.hebtu.movingcampus.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import edu.hebtu.movingcampus.activity.MainActivity;
import edu.hebtu.movingcampus.activity.wrapper.IPreference;
import edu.hebtu.movingcampus.activity.wrapper.MainTabActivity;
import edu.hebtu.movingcampus.adapter.NewsListAdapter;
import edu.hebtu.movingcampus.biz.NewsDao;
import edu.hebtu.movingcampus.entity.NewsShort;

@SuppressLint("NewApi")
public class NewsFragment extends BaseListFragment {

	public Activity mActivity;
	private String page;
	public boolean loaded=false;
	private NewsListAdapter mAdapter;
	private List<NewsShort>loadMoreEntity;
	private List<NewsShort> mlist;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		listview.setXListViewListener(this);
		// construct the RelativeLayout
		mAdapter = new NewsListAdapter(mActivity, R.layout.news_item, listview);
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

	@Override
	public void onRefresh() {
		onStopLoad();
	}

	public void onLoad(){
		loadMoreEntity=new NewsDao(mActivity).mapperJson(true,(Integer.parseInt(page)+1)+"", (mlist.size()+1)+"",null);
		if (loadMoreEntity!= null) {
			mHandler.sendEmptyMessage(0);
		}
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

}
