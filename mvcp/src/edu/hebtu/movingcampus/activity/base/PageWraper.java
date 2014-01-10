package edu.hebtu.movingcampus.activity.base;

import android.content.Intent;

public interface PageWraper {
	public void onResume();

	public void onActivityResult(int requestCode, int resultCode, Intent data);
}
