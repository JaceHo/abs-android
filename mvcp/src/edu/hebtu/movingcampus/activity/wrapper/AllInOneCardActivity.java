package edu.hebtu.movingcampus.activity.wrapper;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.CardTransferActivity;
import edu.hebtu.movingcampus.activity.MainActivity;
import edu.hebtu.movingcampus.activity.base.PageWraper;
import edu.hebtu.movingcampus.biz.CardDao;
import edu.hebtu.movingcampus.biz.base.BaseDao;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.entity.CardEntity;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver;
import edu.hebtu.movingcampus.subjects.NetworkChangeReceiver.NetworkchangeListener;
import edu.hebtu.movingcampus.utils.NetWorkHelper;
import edu.hebtu.movingcampus.widget.PopupDialog;

public class AllInOneCardActivity implements PageWraper,NetworkchangeListener {

	private CardEntity bean;
	private CardDao dao;
	private Activity mainActivity = MainActivity.instance;
	private final View contentView;
	private int loweast;
	private AsyncTask<BaseDao, Integer, Boolean[]>mTask;
	private boolean loaded;

	public AllInOneCardActivity(View view) {
		this.contentView = view;
		loaded=false;
		loweast = mainActivity.getSharedPreferences(Constants.PREFER_FILE,
				ComponentCallbacks2.TRIM_MEMORY_MODERATE).getInt(
				Constants.BALANCE_LOWEAST, 10);
		contentView.findViewById(R.id.btn_lockunlock).setBackgroundResource(
				R.drawable.unlock);
		this.dao=new CardDao(mainActivity);

		mTask=new Cardtask(null).execute(dao);
		bindButton();
	}

	private void bindButton() {
		//点击刷新
		contentView.findViewById(R.id.rl_catdleft).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTask=new Cardtask(null).execute(dao);
				Toast.makeText(mainActivity, "更新一卡通余额成功!", Toast.LENGTH_SHORT).show();
			}
		});
		contentView.findViewById(R.id.ly_connection_failed).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mainActivity.startActivity(new Intent(
								"android.settings.WIRELESS_SETTINGS"));
					}
				});
		contentView.findViewById(R.id.btn_balance_transfer).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mainActivity.startActivity(new Intent(mainActivity,
								CardTransferActivity.class));
					}
				});
		contentView.findViewById(R.id.rl_lockunlock).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(bean!=null)
							if(bean.getStatus()==true)
								mTask=new Cardtask(Constants.ACTION_LOCK).execute(dao);
							else 
								mTask=new Cardtask(Constants.ACTION_UNLOCK).execute(dao);
					}
				});
	}

	@Override
	public void onResume() {
		NetworkChangeReceiver.unRegistNetworkListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}

	private class Cardtask extends AsyncTask<BaseDao, Integer, Boolean[]> {
		private String action = null;

		public Cardtask(String action) {
			this.action = action;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loaded=false;
			if (bean != null) {
				Log.d("object:", bean + "status:" + bean.getStatus());
				((TextView) (contentView.findViewById(R.id.tv_balance_left)))
						.setText(bean.getLastPay() + "元");
				if (bean.getStatus() == false)
					contentView.findViewById(R.id.btn_lockunlock)
							.setBackgroundResource(R.drawable.lock);
				else
					contentView.findViewById(R.id.btn_lockunlock)
							.setBackgroundResource(R.drawable.unlock);

			} else {
				// do nothing TODO
				// ((TextView) (contentView.findViewById(R.id.tv_balance_left)))
				// .setText("***" + "元");
			}
		}

		@Override
		protected Boolean[] doInBackground(BaseDao... params) {
			Boolean[] res = new Boolean[2];
			if(action!=null&&bean!=null)
				res[0] = bean.getStatus();
			else res[0]=null;
			if (action != null)
				((CardDao) params[0]).mapperJson(action);
			bean = ((CardDao) params[0]).mapperJson(false);
			if(bean!=null)
				res[1] = bean.getStatus();
			else res[1]=null;
			return res;
		}

		@Override
		protected void onPostExecute(Boolean[] result) {
			// 如果为获取到信息或修改了一卡通状态,但状态未变化
			if (bean == null||result[0]==null||result[1]==null || action != null && result[0] == result[1]) {
				try {
					if (!NetWorkHelper.isMobileDataEnable(mainActivity)
							&& !NetWorkHelper.isWifiDataEnable(mainActivity)) {
						Toast.makeText(mainActivity, "请检查您的网络连接",
								Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(action!=null&&result[0]!=result[1]){
				Toast.makeText(mainActivity, "success",
						Toast.LENGTH_SHORT).show();
			}
			if (bean != null) {
				Log.d("object:", bean + "status:" + bean.getStatus());
				((TextView) (contentView.findViewById(R.id.tv_balance_left)))
						.setText(bean.getLastPay() + "元");
				if (bean.getStatus() == false)
					contentView.findViewById(R.id.btn_lockunlock)
							.setBackgroundResource(R.drawable.lock);
				else
					contentView.findViewById(R.id.btn_lockunlock)
							.setBackgroundResource(R.drawable.unlock);

			} else {
				// do nothing TODO
				// ((TextView) (contentView.findViewById(R.id.tv_balance_left)))
				// .setText("***" + "元");
			}
			loaded=true;
		}
	}

	@Override
	public void onPause() {
		NetworkChangeReceiver.registNetWorkListener(this);
	}

	@Override
	public void onDataEnabled() {
		if(!loaded)
			mTask.execute(dao);
	}

	@Override
	public void onDataDisabled() {
		// TODO Auto-generated method stub
		
	};
}
