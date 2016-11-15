package info.futureme.abs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * framelayout containing webview
 */
public class WebFrameLayout extends FrameLayout{

    private Boolean isKeyboardShown = false;

    public WebFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebFrameLayout(Context context) {
        super(context);
    }

    private OnSoftKeyboardListener onSoftKeyboardListener;

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (onSoftKeyboardListener != null) {
            final int newSpec = MeasureSpec.getSize(heightMeasureSpec);
            final int oldSpec = getMeasuredHeight();
            if (oldSpec > newSpec){
                if (isKeyboardShown == null || !isKeyboardShown) {
                    isKeyboardShown = true;
                    onSoftKeyboardListener.onShown(newSpec);
                }else{
                    isKeyboardShown = null;
                }
            } else {
                if(isKeyboardShown != null && isKeyboardShown) {
                    isKeyboardShown = false;
                    onSoftKeyboardListener.onHidden(oldSpec);
                }else{
                    isKeyboardShown = null;
                }
            }
        }
        //if(isKeyboardShown != null)
         //   super.onMeasure(getMeasuredWidth(), getMeasuredHeight());
        //else
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final void setOnSoftKeyboardListener(final OnSoftKeyboardListener listener) {
        this.onSoftKeyboardListener = listener;
    }

    public interface OnSoftKeyboardListener {
        public void onShown(int newSpec);
        public void onHidden(int newSpec);
    }
}

