package edu.hebtu.movingcampus.view;

import java.util.ArrayList;

import edu.hebtu.movingcampus.adapter.ListviewAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomerSpinner extends Spinner implements
		AdapterView.OnItemClickListener {
	public static SelectDialog dialog = null;
	public static String text;
	private ArrayList<String> list;
	public String seltext;

	public CustomerSpinner(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public ArrayList<String> getList() {
		return this.list;
	}

	public String getText() {
		return text;
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {
		setSelection(paramInt);
		setText((String) this.list.get(paramInt));
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public boolean performClick() {
		Context localContext = getContext();
		View localView = LayoutInflater.from(getContext()).inflate(2130903043,
				null);
		ListView localListView = (ListView) localView.findViewById(2131165218);
		((TextView) localView.findViewById(2131165217)).setText("--请选择"
				+ this.seltext + "--");
		localListView.setAdapter(new ListviewAdapter(localContext, getList()));
		localListView.setOnItemClickListener(this);
		dialog = new SelectDialog(localContext, 2131099648);
		dialog.getWindow().setLayout(-2, -1);
		ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(
				-2, -1);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		dialog.addContentView(localView, localLayoutParams);
		return true;
	}

	public void setList(ArrayList<String> paramArrayList) {
		this.list = paramArrayList;
	}

	public void setText(String paramString) {
		text = paramString;
	}
}

/*
 * Location: /tmp/apksrc_tmp_9gTLc/classes-dex2jar.jar Qualified Name:
 * com.caii101.view.CustomerSpinner JD-Core Version: 0.6.2
 */