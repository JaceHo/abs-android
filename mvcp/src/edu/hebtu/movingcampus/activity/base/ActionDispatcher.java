package edu.hebtu.movingcampus.activity.base;

import android.view.MotionEvent;

public interface ActionDispatcher {
	public boolean dispatchTouchEvent(MotionEvent event);
}
