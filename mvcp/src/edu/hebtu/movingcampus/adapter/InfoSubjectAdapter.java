package edu.hebtu.movingcampus.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.NewsListActivity;
import edu.hebtu.movingcampus.adapter.base.AdapterBase;
import edu.hebtu.movingcampus.subject.base.ListOfNews;

//可选主题主页新闻+本地通知
public class InfoSubjectAdapter extends AdapterBase<ListOfNews> {
	private Context context;
	private int rowlayout;

	/**
	 * initial
	 * 
	 * @param list
	 *            :news resource
	 * @param context
	 *            :app context
	 * @param resourceId
	 *            :item xml view
	 */
	public InfoSubjectAdapter(List<ListOfNews> list, final Context context,
			int resourceId) {
		super();
		this.context = context;
		this.rowlayout = resourceId;
		this.mList = list;
	}

	// position MK,重写
	@Override
	public long getItemId(int position) {
		// return ((News)getItem(position)).getId();
		// ?
		return mList.get(position).getId();
	}

	@Override
	protected View getNextView(int position, View convertView, ViewGroup parent) {
		// 构造一个布局文件加载器
		if (convertView == null) {
			LayoutInflater inflator = ((Activity) context).getLayoutInflater();
			convertView = inflator.inflate(rowlayout, null);
			final ViewHolder viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		final ListOfNews subject = (ListOfNews) getItem(position);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, NewsListActivity.class);
				i.putExtra("id", subject.getId() + "");
				context.startActivity(i);
			}
		});

		// show
		holder.icon.setImageResource(subject.getIcon());
		holder.title.setText(subject.getDesc());

		// return 加载数据后的iew对象
		return convertView;
	}

	static class ViewHolder {
		public ViewHolder(View convertView) {
			this.title = (TextView) convertView.findViewById(R.id.info_tv);
			this.icon = (ImageView) convertView.findViewById(R.id.info_icon);
		}

		public TextView title;
		public ImageView icon;
	}

	@Override
	protected void onReachBottom() {
		// TODO Auto-generated method stub

	}
}
