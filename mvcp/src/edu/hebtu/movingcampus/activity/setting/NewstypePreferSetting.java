package edu.hebtu.movingcampus.activity.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.adapter.InfoPreferItemAdapter;
import edu.hebtu.movingcampus.entity.InfoPreferItem;
import edu.hebtu.movingcampus.enums.NewsType;

public class NewstypePreferSetting extends Activity {
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.infocenter_settings);

		List<InfoPreferItem> items = new ArrayList<InfoPreferItem>();
		// 本地信息不包括在此
		for (int i = 0; i < (NewsType.values()).length - 1; i++) {
			items.add(new InfoPreferItem(i));
		}

		ListView list = (ListView) findViewById(R.id.lv_info_prefer);
		list.setAdapter(new InfoPreferItemAdapter(items, this,
				R.layout.info_prefer_item));

		bindButton();
	}

	private void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						NewstypePreferSetting.this.finish();
					}
				});
	}
}
