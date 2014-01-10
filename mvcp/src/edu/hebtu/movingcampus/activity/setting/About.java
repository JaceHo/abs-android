package edu.hebtu.movingcampus.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import edu.hebtu.movingcampus.R;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		bindButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	private void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						About.this.finish();
					}
				});
	}

}
