package edu.hebtu.movingcampus.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.biz.NewsDao;
import edu.hebtu.movingcampus.entity.NewsShort;
import edu.hebtu.movingcampus.utils.ImageUtil;

@SuppressLint({ "NewApi", "ValidFragment" })
public class NewsFragment extends BaseListFragment {

	public Activity mActivity;
	private List<NewsShort> items_list = new ArrayList<NewsShort>();
	private MyAdapter mAdapter;
	// 0 search result,Newtype.values()+1,news type
	private String type;
	private ArrayList<NewsShort> loadMoreEntity;

	// private DisplayImageOptions options;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mAdapter.appendToList(loadMoreEntity);
				break;
			}
			onLoad();
		}

	};

	// add this constructor by King0769, 2013/5/7
	// in order to solve an exception that
	// "can't instantiate class view.NewsFragment; no empty constructor"
	// I found it in this case : 1.open program -> 2.change system language ->
	// 3.reopen eoe, can see FC(force close)
	// I think this bug will happens in many cases.
	public NewsFragment() {

	}

	// --------------------

	public NewsFragment(Activity c) {
		this.mActivity = c;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);

		listview.setXListViewListener(this);
		// construct the RelativeLayout
		mAdapter = new MyAdapter();
		mAdapter.appendToList(items_list);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				NewsShort item = (NewsShort) mAdapter.getItem(position - 1);
				startDetailActivity(mActivity, "" + item.getID(), "资讯",
						item.getTitle());
			}
		});
		return view;
	}

	class MyAdapter extends BaseAdapter {
		List<NewsShort> mList = new ArrayList<NewsShort>();

		// initial load news
		public MyAdapter() {
			onLoadMore();
		}

		public void appendToList(List<NewsShort> lists) {
			if (lists == null) {
				return;
			}
			mList.addAll(lists);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			NewsShort item = mList.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater
						.inflate(R.layout.news_item_layout, null);
				holder.title_ = (TextView) convertView
						.findViewById(R.id.news_title);
				holder.time_ = (TextView) convertView
						.findViewById(R.id.img_thu);
				holder.short_ = (TextView) convertView
						.findViewById(R.id.news_short_content);
				holder.img_thu = (ImageView) convertView
						.findViewById(R.id.img_thu);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			holder.title_.setText(item.getTitle());
			holder.short_.setText(item.getContent());
			holder.time_.setText((CharSequence) item.getDate());
			String img_url = item.getThumbnail_url();
			if (img_url.equals(null) || img_url.equals("")) {
				holder.img_thu.setVisibility(View.GONE);
			} else {
				holder.img_thu.setVisibility(View.VISIBLE);
				ImageUtil.setThumbnailView(img_url, holder.img_thu, mActivity,
						callback1, false);
			}
			return convertView;
		}
	}

	static class ViewHolder {
		public TextView title_;
		public TextView short_;
		public ImageView img_thu;
		public TextView time_;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		onLoad();
	}

	// public ArrayList<NewsShort> mapperJson(boolean useCache,String
	// type,String from,String size) {
	// size default 10
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				ArrayList<NewsShort> response = new NewsDao(mActivity)
						.mapperJson(true, type, "" + (mAdapter.getCount() - 1),
								"" + 20);
				if (response != null) {
					loadMoreEntity = response;
					mHandler.sendEmptyMessage(0);
				}
				super.run();
			}
		}.start();

	}

}
