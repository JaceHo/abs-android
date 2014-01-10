package edu.hebtu.movingcampus.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;

public class Share_Quickmark extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share__quickmark);
		bindButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	protected void bindButton() {
		findViewById(R.id.btn_submit_feedback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				recommandToYourFriend("http://baidu.com", "分享移动校园android版");
			}
		});
		findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Share_Quickmark.this.finish();
			}
		});
	}

}
