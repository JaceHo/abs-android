package edu.hebtu.movingcampus.activity;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.Observer;
import edu.hebtu.movingcampus.activity.base.PageWraper;
import edu.hebtu.movingcampus.adapter.InfoSubjectAdapter;
import edu.hebtu.movingcampus.subject.base.Subject;
import edu.hebtu.movingcampus.subject.base.TitleNews;
import edu.hebtu.movingcampus.utils.IntentUtil;
import edu.hebtu.movingcampus.utils.NetWorkHelper;

/**
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:32 AM
 */
public class InfoCenterActivity implements Observer, PageWraper {

	private List<TitleNews> titles;
	private Activity mainActivity = MainActivity.instance;
	private View content;
	private final InfoSubjectAdapter adapter;

	public InfoCenterActivity(View view) {
		this.content = view;
		titles = IPreference.getInstance(mainActivity).getTopics();
		for (TitleNews s : titles)
			((Subject) s).registObserver(this);
		//API disabled 
//		try{
//		MyWeather.getWeather("石家庄");
//		content.findViewById(R.id.imgbtn_today_weather).setBackgroundResource(
//		MyWeather.iconToday[0]);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		ListView list = (ListView) (content.findViewById(R.id.lv_infoitem));

		// 2 new adapter
		adapter = new InfoSubjectAdapter(titles, MainActivity.instance,
				R.layout.infoitem);

		list.setAdapter(adapter);

		bindButton();
	}

	private void bindButton() {
//		content.findViewById(R.id.imgbtn_today_weather).setOnClickListener(
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Intent intent = new Intent(mainActivity,
//								MyWeather.class);
//						mainActivity.startActivity(intent);
//						Toast.makeText(mainActivity.getApplicationContext(),
//								"some text", Toast.LENGTH_LONG).show();
//					}
//				});
		content.findViewById(R.id.imageview_above_query).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (NetWorkHelper.isNetworkAvailable(mainActivity)) {
							IntentUtil.start_activity(mainActivity,
									SearchActivity.class,
									new BasicNameValuePair("tag", "0"));
						} else {
							Toast.makeText(
									mainActivity.getApplicationContext(),
									"网络连接失败,请检查网络", Toast.LENGTH_LONG).show();
						}
					}
				});
		// TODO
		// content.findViewById(R.id.btn_info_class).setOnClickListener(
		// new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(mainActivity,
		// MainTopRightDialog.class);
		// mainActivity.startActivity(intent);
		// Toast.makeText(mainActivity.getApplicationContext(),
		// "some text", Toast.LENGTH_LONG).show();
		// }
		// });
	}

	@Override
	public void update() {
	}

	@Override
	public void onResume() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

}