package edu.hebtu.movingcampus.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.adapter.base.AdapterBase;
import edu.hebtu.movingcampus.entity.ExamineInfor;

public class ExamInforAdapter extends AdapterBase<ExamineInfor> {

	@Override
	protected View getNextView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exanination_show_list_item, null);
		TextView examName = (TextView) convertView.findViewById(R.id.examlist_item_kcm);
		TextView examDate = (TextView) convertView.findViewById(R.id.examlist_item_ksrq);
		TextView examTime = (TextView) convertView.findViewById(R.id.examlist_item_kssj);
		TextView examPlace = (TextView) convertView.findViewById(R.id.examlist_item_roomid);
		ExamineInfor examInfo = (ExamineInfor) getItem(position);
		System.out.print("这是考试日期"+examInfo.toString());
		Log.i("这是考试日期", examDate.toString());
		examDate.setText(examInfo.getKsrq());
		examName.setText(examInfo.getKcm());
		examTime.setText(examInfo.getKssj());
		examPlace.setText(examInfo.getRoomid());
		return convertView;
	}

	@Override
	protected void onReachBottom() {
		// TODO Auto-generated method stub
	}

}
