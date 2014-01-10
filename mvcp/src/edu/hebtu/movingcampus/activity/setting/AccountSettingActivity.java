package edu.hebtu.movingcampus.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.entity.User;

public class AccountSettingActivity extends Activity {
	private User user;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.account_show);
		// TODO
		user = ((AppInfo) getApplication()).getUser();
		((EditText) findViewById(R.id.et_stuID)).setText(user.getCid());
		((EditText) findViewById(R.id.et_stuName)).setText("张三");
		((EditText) findViewById(R.id.et_sex)).setText("男");
		((EditText) findViewById(R.id.et_role)).setText("学生");
		((EditText) findViewById(R.id.et_mz)).setText("汉");
		((EditText) findViewById(R.id.et_birth)).setText(" 1992年2月");
		((EditText) findViewById(R.id.et_sfzh)).setText("293810394930"
				+ user.getPassword());
		bindButton();
	}

	private void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						AccountSettingActivity.this.finish();
					}
				});
	}
}
