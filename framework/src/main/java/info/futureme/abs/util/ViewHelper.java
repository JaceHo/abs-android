package info.futureme.abs.util;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class ViewHelper {
    /**
     *refrenced from http://stackoverflow.com/questions/18305945/how-to-resume-fragment-from-backstack-if-exists
     * @param fragment
     */
    public static void replaceFragment (Fragment fragment, int container){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = fragment.getChildFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    /**
     * Draw the view into a bitmap.
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            DLog.e("viewhelper", "failed getViewBitmap(" + v + ")");
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public static void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    public static void replaceFragment (AppCompatActivity activity, Fragment fragment, int container){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = activity.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public static ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }


    //此方法在setAdapter之前调用
    public static int getListViewHeightBasedOnChildren(RecyclerView listView, int maxItem) {
        RecyclerView.Adapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return 0;
        }
        int shownCount  = listAdapter.getItemCount() >= maxItem ? maxItem : listAdapter.getItemCount();
        View child = listView.getLayoutManager().findViewByPosition(shownCount - 1);
        return child.getBottom();
    }

    public static void nullViewDrawablesRecursive(View view)
    {
        if(view != null)
        {
            try
            {
                ViewGroup viewGroup = (ViewGroup)view;

                int childCount = viewGroup.getChildCount();
                for(int index = 0; index < childCount; index++)
                {
                    View child = viewGroup.getChildAt(index);
                    nullViewDrawablesRecursive(child);
                }
            }
            catch(Exception e)
            {
            }

            nullViewDrawable(view);
        }
    }

    public static void nullViewDrawable(View view)
    {
        try
        {
            /*
            try {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) view.getBackground());
                if(bitmapDrawable != null) {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if(bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
            }catch (Exception e){
            }
            */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(null);
            }else {
                view.setBackgroundDrawable(null);
            }
        }
        catch(Exception e)
        {
        }

        try
        {
            ImageView imageView = (ImageView)view;
            imageView.setImageDrawable(null);
            imageView.setBackgroundDrawable(null);
        }
        catch(Exception e)
        {
        }
    }

    public static void ensureTop(View centerView) {
        centerView.bringToFront();
        ViewGroup viewGroup = (ViewGroup) centerView.getParent();
//        while (viewGroup != null){
        viewGroup.setClipToPadding(false);
        viewGroup.setClipChildren(false);
//            viewGroup = (ViewGroup) centerView.getParent();
//        }
    }
}
