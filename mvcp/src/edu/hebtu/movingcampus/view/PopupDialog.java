package edu.hebtu.movingcampus.view;

import android.app.Activity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;

//	// 以下拉方式显示。
//popup.showAsDropDown(v);
//将PopupWindow显示在指定位置
//popup.showAtLocation(findViewById(R.id.bn), Gravity.CENTER, 20,
//	20);

public class PopupDialog extends PopupWindow {
	private View root;
	private final PopupWindow popup;

	public PopupDialog(Activity activity, String title, String content,
			Runnable run) {
		root = activity.getLayoutInflater().inflate(R.layout.popup, null);
		popup = new PopupWindow(root, 280, 360);
		((TextView) activity.findViewById(R.id.tv_popup_title)).setText(title);
		((TextView) activity.findViewById(R.id.tv_popup_message))
				.setText(content);
		bindButton(run);
	}

	public void bindButton(final Runnable run) {
		root.findViewById(R.id.btn_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						run.run();
					}
				});
		;
		// 获取Popup窗口中的关闭按钮。
		root.findViewById(R.id.btn_cancel).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						// 关闭Popup窗口
						popup.dismiss();
					}
				});
	}
}
