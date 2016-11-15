package info.futureme.abs.example.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import info.futureme.abs.util.ReflectHelper;

/**
 * Created by Jeffrey on 7/5/16.
 */
public class NestedRecyclerView extends RecyclerView{
    private float initX = -1;
    private float initY = -1;
    boolean init = false;

    public NestedRecyclerView(Context context) {
        super(context);
    }

    public NestedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if(!init) {
            int touchslop = ReflectHelper.getFieldValue(this, RecyclerView.class,  "mTouchSlop");
            ReflectHelper.setField(this, RecyclerView.class, "mTouchSlop", (int) (touchslop * 10f));
            init = true;
        }
        boolean res = super.onInterceptTouchEvent(e);
//        int action = e.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                initX = (int) (e.getRawX() + 0.5f);
//                initY = (int) (e.getRawY() + 0.5f);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int moveX = (int) e.getRawX();
//                int moveY = (int) e.getRawY();
//                if (Math.abs(moveX - initX) > Math.abs(moveY - initY)) {
//                    if(getScrollState() == SCROLL_STATE_DRAGGING){
//                        //reset to state idle
//                        ReflectHelper.genericInvokMethod(this,
//                                "setScrollState", 1, SCROLL_STATE_IDLE);
//                        DLog.i("setScrollState", "success");
//                        res = false;
//                    }
//                }
//            case MotionEvent.ACTION_UP:
//                break;
//        }
        return res;

    }
}
