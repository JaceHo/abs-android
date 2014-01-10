package edu.hebtu.movingcampus.adapter;

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
import edu.hebtu.movingcampus.activity.NewsDetailsActivity;
import edu.hebtu.movingcampus.adapter.base.AdapterBase;
import edu.hebtu.movingcampus.entity.NewsShort;

//主页新闻+本地通知数据展示
public class InfoNewsAdapter extends AdapterBase<NewsShort> {
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
	public InfoNewsAdapter(final Context context, int resourceId) {
		super();
		this.context = context;
		this.rowlayout = resourceId;
	}

	// position MK
	@Override
	public long getItemId(int position) {
		// return ((News)getItem(position)).getId();
		// ?
		return mList.get(position).getID();
	}

	@Override
	protected View getNextView(final int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		// 构造一个布局文件加载器
		if (convertView == null) {
			LayoutInflater inflator = ((Activity) context).getLayoutInflater();
			convertView = inflator.inflate(rowlayout, null);
			final ViewHolder viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		final NewsShort news = (NewsShort) getItem(position);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, NewsDetailsActivity.class);
				i.putExtra("position", position);
				context.startActivity(i);
			}
		});

		// show
		holder.icon.setImageResource(news.getIcon());
		holder.title.setText(news.getTitle());
		holder.content.setText(news.getContent());
		holder.time.setText(news.getDate().toLocaleString());

		// return 加载数据后的iew对象
		return convertView;
	}

	@Override
	protected void onReachBottom() {
	}

	static class ViewHolder {
		public ViewHolder(View convertView) {
			this.title = (TextView) convertView.findViewById(R.id.news_title);
			this.time = (TextView) convertView.findViewById(R.id.news_time);
			this.content = (TextView) convertView
					.findViewById(R.id.news_short_content);
			this.icon = (ImageView) convertView.findViewById(R.id.img_thu);
		}

		public TextView title;
		public TextView time;
		public TextView content;
		public ImageView icon;
	}
}
