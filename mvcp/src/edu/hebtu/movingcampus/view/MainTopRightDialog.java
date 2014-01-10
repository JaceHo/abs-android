package edu.hebtu.movingcampus.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;

public class MainTopRightDialog extends Activity {
	// private MyDialog dialog;
	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_top_right_dialog);
		// dialog=new MyDialog(this);
		layout = (LinearLayout) findViewById(R.id.main_dialog_layout);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "some text",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	/*
	 * public void exitbutton1(View v) { this.finish(); } public void
	 * exitbutton0(View v) { this.finish();
	 * MainWeixin.instance.finish();//�ر�Main ���Activity }
	 */
}
