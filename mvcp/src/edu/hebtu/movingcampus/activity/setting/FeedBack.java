package edu.hebtu.movingcampus.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;

public class FeedBack extends Activity {

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.feed_back);
		bindButton();
	}

	private void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FeedBack.this.finish();
			}
		});
		findViewById(R.id.btn_submit_feedback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text=((EditText)findViewById(R.id.et_feedback_string)).getText().toString();
				Toast.makeText(FeedBack.this	,"你的反馈是对我们最好的支持" ,Toast.LENGTH_SHORT).show();
			}
		});
	}

}
