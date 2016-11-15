package info.futureme.abs.base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import info.futureme.abs.R;
import info.futureme.abs.util.ViewHelper;
import info.futureme.abs.util.WindowUtils;
import info.futureme.abs.view.SlidingLayout;
import info.futureme.abs.view.TopCropImageView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Secondaryactiivty is the second level activity entered from mainactivity,index
 * activity and so on, it is customized to slideback to previous activity like ios
 * and wechat
 * whenever with a drag drop slide or with a back icon clicking.
 * @author Jeffrey
 * @version 1.0
 * @updated 25-一月-2016 15:57:59
 */
public abstract class BaseSecondaryActivity extends BaseActionBarMockingActivity implements SlidingLayout.SlidingListener {
    private TopCropImageView mPreview;
    private View preview;
    private float mInitOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.slide_layout);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        LayoutInflater inflater = LayoutInflater.from(this);
        mInitOffset = -(1.f / 3) * metrics.widthPixels;

        mPreview = (TopCropImageView) findViewById(R.id.iv_preview);
        FrameLayout contentView = (FrameLayout) findViewById(R.id.content_view);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.BOTTOM);
        lp.setMargins(0, 0, 0, 0);
        contentView.addView(inflater.inflate(layoutResID, null), lp);

        final SlidingLayout slideLayout = (SlidingLayout) findViewById(R.id.slide_layout);
        slideLayout.setShadowResource(R.drawable.sliding_back_shadow);
        slideLayout.setSlidingListener(this);

        if(activities.size() < 2){
            // no parent activity
            slideLayout.setEdgeSize(0);
            return;
        }else{
            slideLayout.setEdgeSize((int) (metrics.density * 40));
        }
        //parent activity contentview here
        Activity activity = null;
        for(int i = activities.size()-1; i >=0 ;i--){
            if(activities.get(i).equals(this)){
                activity = activities.get(i - 1);
            }
        }
        if(activity == null){
            slideLayout.setEdgeSize(0);
            return;
        }

        preview = activity.findViewById(android.R.id.content);
        int top = WindowUtils.getStatusBarHeight(activity);
        //Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH
        //是为了处理兼容性问题：android 5.0 以下的手机，图片显示高度会有抖动
        if(activity instanceof BaseSecondaryActivity || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH){
            top = 0;
        }
        mPreview.setClipHeight(top);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mPreview.setBackground(new BitmapDrawable(getResources(), ViewHelper.getViewBitmap(preview)));
        }else {
            mPreview.setBackgroundDrawable(new BitmapDrawable(getResources(), ViewHelper.getViewBitmap(preview)));
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        //hide keyboard by default
        hideKeyboard(panel);
        if (slideOffset <= 0) {
        } else if (slideOffset < 1) {
            mPreview.setTranslationX(mInitOffset * (1 - slideOffset));
        } else {
            mPreview.setTranslationX(0);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    protected int getActionBarLeftResourceId() {
        return R.drawable.back_arow;
    }

    protected void onActionBarLeftClick() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onDestroy(){
        if(preview != null) {
            preview.setDrawingCacheEnabled(false);
            preview.destroyDrawingCache();
            preview = null;
        }
        //release image preview
        Drawable drawable = mPreview.getBackground();
        if(drawable != null && drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mPreview.setBackground(null);
            }else{
                mPreview.setBackgroundDrawable(null);
            }
        }
        super.onDestroy();
    }
}
