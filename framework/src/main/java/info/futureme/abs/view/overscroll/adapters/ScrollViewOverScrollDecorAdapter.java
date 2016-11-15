package info.futureme.abs.view.overscroll.adapters;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.ScrollView;

import info.futureme.abs.view.overscroll.HorizontalOverScrollBounceEffectDecorator;
import info.futureme.abs.view.overscroll.VerticalOverScrollBounceEffectDecorator;

/**
 * An adapter that enables over-scrolling over a {@link ScrollView}.
 * <br/>Seeing that {@link ScrollView} only supports vertical scrolling, this adapter
 * should only be used with a {@link VerticalOverScrollBounceEffectDecorator}. For horizontal
 * over-scrolling, use {@link HorizontalScrollViewOverScrollDecorAdapter} in conjunction with
 * a {@link android.widget.HorizontalScrollView}.
 *
 * @author amit
 *
 * @see HorizontalOverScrollBounceEffectDecorator
 * @see VerticalOverScrollBounceEffectDecorator
 */
public class ScrollViewOverScrollDecorAdapter implements IOverScrollDecoratorAdapter {

    protected final ScrollView mView;

    public ScrollViewOverScrollDecorAdapter(ScrollView view) {
        mView = view;
    }

    @Override
    public View getView() {
        return mView;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean isInAbsoluteStart() {
        return !mView.canScrollVertically(-1);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean isInAbsoluteEnd() {
        return !mView.canScrollVertically(1);
    }
}
