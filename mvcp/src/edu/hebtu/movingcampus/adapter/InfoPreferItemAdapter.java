package edu.hebtu.movingcampus.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.IPreference;
import edu.hebtu.movingcampus.adapter.base.AdapterBase;
import edu.hebtu.movingcampus.entity.InfoPreferItem;
import edu.hebtu.movingcampus.enums.NewsType;
import edu.hebtu.movingcampus.subjects.NewsSubject;

//主页新闻+本地通知数据展示
public class InfoPreferItemAdapter extends AdapterBase<InfoPreferItem> {
	private Activity context;
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
	public InfoPreferItemAdapter(List<InfoPreferItem> list,
			final Activity context, int resourceId) {
		super();
		this.context = context;
		this.rowlayout = resourceId;
		this.mList = list;
	}

	// position MK
	@Override
	public long getItemId(int position) {
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
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		final InfoPreferItem info = (InfoPreferItem) getItem(position);

		// show,saved state
		if (info.getId() % 2 == 1)
			convertView.setBackgroundResource(R.color.white);
		holder.title.setText(info.getTitle());
		holder.check.setChecked(info.isChecked(context));

		holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				info.setChecked(holder.check, isChecked, context);
				if (!isChecked)
					IPreference.getInstance(context).removeTitledNewsById(
							info.getId());
				else
					IPreference.getInstance(context).addTopic(
							new NewsSubject(NewsType.values()[info.getId()],
									context));
			}
		});

		// return 加载数据后的iew对象
		return convertView;
	}

	static class ViewHolder {
		public ViewHolder(View convertView) {
			this.title = (TextView) convertView
					.findViewById(R.id.tv_title_inofo_prefer);
			this.check = (CheckBox) convertView
					.findViewById(R.id.cb_info_prefer);
		}

		public TextView title;
		public CheckBox check;
	}

	@Override
	protected void onReachBottom() {
		// TODO Auto-generated method stub

	}
}
