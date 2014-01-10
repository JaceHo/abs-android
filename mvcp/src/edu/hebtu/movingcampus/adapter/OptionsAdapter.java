package edu.hebtu.movingcampus.adapter;

import java.util.ArrayList;

import edu.hebtu.movingcampus.view.ViewHolder;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OptionsAdapter extends BaseAdapter {
	private Activity activity = null;
	private Handler handler;
	private ArrayList<String> list = new ArrayList();

	public OptionsAdapter(Activity paramActivity, Handler paramHandler,
			ArrayList<String> paramArrayList) {
		this.activity = paramActivity;
		this.handler = paramHandler;
		this.list = paramArrayList;
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int paramInt) {
		return this.list.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(final int paramInt, View paramView,
			ViewGroup paramViewGroup) {
		ViewHolder localViewHolder;
		if (paramView == null) {
			localViewHolder = new ViewHolder();
			paramView = LayoutInflater.from(this.activity).inflate(2130903057,
					null);
			localViewHolder.textView = ((TextView) paramView
					.findViewById(2131165255));
			localViewHolder.textView.setTypeface(Typeface.DEFAULT, 0);
			localViewHolder.textView.setTextColor(Color.rgb(136, 28, 237));
			localViewHolder.textView.setTextSize(13.0F);
			paramView.setTag(localViewHolder);
		}
		while (true) {
			/*
			 * localViewHolder.textView.setText((CharSequence)this.list.get(paramInt
			 * )); localViewHolder.textView.setOnClickListener(new
			 * View.OnClickListener() { public void onClick(View
			 * paramAnonymousView) { Message localMessage = new Message();
			 * Bundle localBundle = new Bundle(); localBundle.putInt("selIndex",
			 * paramInt); localMessage.setData(localBundle); localMessage.what =
			 * 1; OptionsAdapter.this.handler.sendMessage(localMessage); } });
			 */
			return paramView;
			// localViewHolder = (ViewHolder)paramView.getTag();
		}
	}
}

/*
 * Location: /tmp/apksrc_tmp_9gTLc/classes-dex2jar.jar Qualified Name:
 * com.caii101.view.OptionsAdapter JD-Core Version: 0.6.2
 */