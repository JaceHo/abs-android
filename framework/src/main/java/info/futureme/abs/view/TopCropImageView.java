package info.futureme.abs.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import info.futureme.abs.util.DLog;

/**
 * ImageView to display top-crop scale of an image view.
 *
 */
public class TopCropImageView extends ImageView {
    int clipHeight = 0;

    public TopCropImageView(Context context) {
        super(context.getApplicationContext());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TopCropImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context.getApplicationContext(), attrs, defStyleAttr, defStyleRes);
    }

    public TopCropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);
    }

    public TopCropImageView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
    }

    public void setClipHeight(int clipHeight){
        this.clipHeight = clipHeight;
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            if(getWidth() > 0){
                canvas.clipRect(0, clipHeight, getWidth(), getHeight());
            }
            super.draw(canvas);
        }catch (Exception e){
            DLog.p(e);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top - clipHeight, right, bottom);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        return super.setFrame(l, t - clipHeight, r, b);
    }
}
