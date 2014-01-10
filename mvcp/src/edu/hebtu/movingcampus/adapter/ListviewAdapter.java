package edu.hebtu.movingcampus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.view.ViewHolder;

public class ListviewAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> list;

	public ListviewAdapter(Context paramContext,
			ArrayList<String> paramArrayList) {
		this.context = paramContext;
		this.list = paramArrayList;
	}

	@Override
	public int getCount() {
		return this.list.size();
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
	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		ViewHolder localViewHolder;
		if ((paramView == null) && (this.list.size() != 0)) {
			localViewHolder = new ViewHolder();
			paramView = LayoutInflater.from(this.context).inflate(
					R.layout.option_item, null);
			localViewHolder.textView = ((TextView) paramView
					.findViewById(R.id.item_text));
			localViewHolder.textView.setTextColor(Color.rgb(136, 28, 237));
			localViewHolder.textView.setTextSize(13.0F);
			localViewHolder.textView.setTypeface(Typeface.DEFAULT, 0);
			paramView.setTag(localViewHolder);
		}
		localViewHolder = (ViewHolder) paramView.getTag();
		localViewHolder.textView.setText(this.list.get(paramInt));
		return paramView;
	}
}