package edu.hebtu.movingcampus.activity.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.subjects.AllInOneCardNewsdump;
import edu.hebtu.movingcampus.subjects.LibraryNewsdump;

public class LocalPreferSetting extends BaseActivity {
	private SeekBar seekBarlib;
	private static final int libmin=3;
	private static final int libmax=30;
	private static final int cardmin=5;
	private static final int cardmax=100;
	private SeekBar seekBarcard;
	private TextView libtip;
	private TextView cardtip;
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.localnews_settting);
		//5-100
		SharedPreferences pre= getSharedPreferences(
		Constants.PREFER_FILE, 0);
		seekBarlib=(SeekBar) findViewById(R.id.SeekBarlib);
		seekBarlib.setProgress((int) ((100.0*LibraryNewsdump.days-libmin)/(1.0*(libmax-libmin))));
		libtip=(TextView) findViewById(R.id.lib_num);

		libtip.setText(pre.getInt("lib.days", 10)+"天");

		//3-30
		seekBarcard=(SeekBar) findViewById(R.id.seekBarcard);
		seekBarcard.setProgress((int) (100.0*(AllInOneCardNewsdump.loweast-cardmin)/(1.0*(cardmax-cardmin))));
		cardtip=(TextView) findViewById(R.id.card_num);
		cardtip.setText(pre.getInt("card.loweast", 10)+"元");

		bindButton();
	}

	@Override
	public void bindButton() {
		findViewById(R.id.btn_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						LocalPreferSetting.this.finish();
					}
				});
		
		seekBarlib.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			//当拖动条的滑块位置发生改变时触发该方法
			@Override
			public void onProgressChanged(SeekBar arg0
				, int progress, boolean fromUser)
			{
				//动态改变图片的透明度
				seekBarlib.getBackground().setAlpha((int) (progress/10.0));
				libtip.setText((int) (progress*1.0/100.0*(libmax-libmin)+libmin)+"天");
			}
			@Override
			public void onStartTrackingTouch(SeekBar bar){}
			@Override
			public void onStopTrackingTouch(SeekBar bar){
				SharedPreferences.Editor editor= LocalPreferSetting.this.getSharedPreferences(
						Constants.PREFER_FILE, 0).edit();
				LibraryNewsdump.days= (int) (bar.getProgress()*1.0/100.0*(libmax-libmin)+libmin);
				editor.putInt("lib.days",LibraryNewsdump.days);
				editor.commit();
			}
		});

		seekBarcard.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			//当拖动条的滑块位置发生改变时触发该方法
			@Override
			public void onProgressChanged(SeekBar arg0
				, int progress, boolean fromUser)
			{
				//动态改变图片的透明度
				seekBarcard.getBackground().setAlpha((int) (progress/10.0));
				cardtip.setText((int) (1.0*progress/100.0*(cardmax-cardmin)+cardmin)+"元");
			}
			@Override
			public void onStartTrackingTouch(SeekBar bar){}
			@Override
			public void onStopTrackingTouch(SeekBar bar){
				SharedPreferences.Editor editor= LocalPreferSetting.this.getSharedPreferences(
						Constants.PREFER_FILE, 0).edit();
				AllInOneCardNewsdump.loweast=(int) (1.0*bar.getProgress()/100.0*(cardmax-cardmin)+cardmin);
				editor.putInt("card.loweast",AllInOneCardNewsdump.loweast);
				editor.commit();
			}
		});
	}
}
