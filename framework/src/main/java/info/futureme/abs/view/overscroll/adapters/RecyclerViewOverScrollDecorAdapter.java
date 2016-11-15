package info.futureme.abs.view.overscroll.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import info.futureme.abs.view.overscroll.HorizontalOverScrollBounceEffectDecorator;
import info.futureme.abs.view.overscroll.VerticalOverScrollBounceEffectDecorator;

/**
 * @author amitd
 *
 * @see HorizontalOverScrollBounceEffectDecorator
 * @see VerticalOverScrollBounceEffectDecorator
 */
public class RecyclerViewOverScrollDecorAdapter implements IOverScrollDecoratorAdapter {

    protected final RecyclerView mRecyclerView;
    protected final LinearLayoutManager mLayoutManager;

    public RecyclerViewOverScrollDecorAdapter(RecyclerView recyclerView) {
        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            throw new IllegalArgumentException("Recycler views with non-linear layout managers are not supported by this adapter. Consider implementing a new adapter, instead");
        }

        mRecyclerView = recyclerView;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    @Override
    public View getView() {
        return mRecyclerView;
    }

    @Override
    public boolean isInAbsoluteStart() {
        return mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return mLayoutManager.findLastCompletelyVisibleItemPosition() == (mRecyclerView.getAdapter().getItemCount() - 1);
    }

}
