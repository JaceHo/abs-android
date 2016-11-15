package info.futureme.abs.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import info.futureme.abs.R;
import info.futureme.abs.view.overscroll.VerticalOverScrollBounceEffectDecorator;
import info.futureme.abs.view.overscroll.adapters.IOverScrollDecoratorAdapter;


/**
 * content loaderview with loading listeners and refresh callbacks, empty,error, loading view in it
 */
public class ContentLoaderView extends FrameLayout implements View.OnClickListener, FXRecyclerView.LoadingListener, IOverScrollDecoratorAdapter {
    public static final int LOAD_MORE_ITEM_SLOP = 4;
    View loadingView;

    View emptyView;
    TextView errorMessageTV;
    ImageView errorImage;
    Button errorRetryBtn;

    View errorView;
    TextView emptyMessageTV;
    ImageView emptyImage;
    Button emptyRetryBtn;


    FXRecyclerView recyclerView;

    private OnUpdateStateResourceCallback updateStateResourceCallback;
    private OnRefreshListener refreshListener;
    private OnMoreListener moreListener;

    private boolean loadMore = false;
    private int totalPage = 1;
    private int currentPage = 1;

    private int padding;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;
    private boolean clipToPadding;

    public static final String DISPLAY_STATE = "display_state";
    public static final int STATE_CONTENT = 0x1;
    public static final int STATE_LOADING = 0x2;
    public static final int STATE_EMPTY = 0x3;
    public static final int STATE_ERROR = 0x4;

    public int getDisplayState() {
        return displayState;
    }

    private int displayState = STATE_LOADING;

    public ContentLoaderView(Context context) {
        super(context);
        initViews();
    }

    public ContentLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initViews();
    }

    public ContentLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContentLoaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttrs(Context ctx, AttributeSet attr) {
        TypedArray ta = ctx.obtainStyledAttributes(attr, R.styleable.ContentLoaderView);
        padding = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_padding, -1);
        paddingLeft = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingLeft, -1);
        paddingRight = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingRight, -1);
        paddingTop = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingTop, -1);
        paddingBottom = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingBottom, -1);
        clipToPadding = ta.getBoolean(R.styleable.ContentLoaderView_android_clipToPadding, true);
        ta.recycle();
    }

    private void initViews() {
        inflate(getContext(), R.layout.list, this);

        setPadding(0, 0, 0, 0);
        setClickable(true);
        errorView = findViewById(R.id.error_view);
        errorImage = (ImageView) errorView.findViewById(R.id.error_loading_image);
        errorMessageTV = (TextView) errorView.findViewById(R.id.error_message);
        errorRetryBtn = (Button) errorView.findViewById(R.id.btn_error_retry);

        emptyView = findViewById(R.id.empty_view);
        emptyMessageTV = (TextView) emptyView.findViewById(R.id.empty_message);
        emptyImage = (ImageView) emptyView.findViewById(R.id.empty_loading_image);
        emptyRetryBtn = (Button) emptyView.findViewById(R.id.btn_empty_retry);


        loadingView = findViewById(R.id.loading_view);
        recyclerView = (FXRecyclerView) findViewById(R.id.recycler);
        errorRetryBtn = (Button) findViewById(R.id.btn_error_retry);
        errorMessageTV = (TextView) findViewById(R.id.error_message);
        emptyRetryBtn = (Button) findViewById(R.id.btn_empty_retry);
        emptyMessageTV = (TextView) findViewById(R.id.empty_message);
        emptyImage = (ImageView) findViewById(R.id.empty_loading_image);

        errorRetryBtn.setOnClickListener(this);
        emptyRetryBtn.setOnClickListener(this);
        recyclerView.addOnScrollListener(mRecyclerScrollListener);
        recyclerView.setLoadingListener(this);
        if (padding != -1) {
            recyclerView.setPadding(padding, padding, padding, padding);
        } else {
            recyclerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setClipToPadding(clipToPadding);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            new VerticalOverScrollBounceEffectDecorator(this);
        }
        setDisplayState(STATE_LOADING);
    }

    RecyclerView.OnScrollListener mRecyclerScrollListener = new RecyclerView.OnScrollListener() {
        int totalItemCount;
        int visibleItemCount;
        int firstVisibleItemPosition;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager;
            layoutManager = recyclerView.getLayoutManager();
            totalItemCount = layoutManager.getItemCount();
            visibleItemCount = layoutManager.getChildCount();
            View firstVisibleChild = recyclerView.getChildAt(0);
            firstVisibleItemPosition = recyclerView.getChildLayoutPosition(firstVisibleChild);
            if (totalPage > currentPage &&
                    !loadMore &&
                    (firstVisibleItemPosition + visibleItemCount + LOAD_MORE_ITEM_SLOP) >= totalItemCount) {

                loadMore = true;
                if (moreListener != null) {
                    moreListener.onMore(++currentPage);
                }
            }
        }
    };

    public void setAdapter(final RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        if (adapter.getItemCount() > 0) {
            setDisplayState(STATE_CONTENT);
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
                for(int i = positionStart; i< positionStart + itemCount; i++) {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i + 1);
                    adapter.onBindViewHolder(holder, i);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }

            private void update() {
                //success!
                if(recyclerView != null)
                    recyclerView.refreshComplete();
                int itemCount = recyclerView.getLayoutManager().getItemCount();
                itemCount -= 2;//因为有header 还有 footer 啊
                if (itemCount > 0) {
                    if (loadMore) {
                        loadMoreCompleted();
                    }
                    setDisplayState(STATE_CONTENT);
                    if (updateStateResourceCallback != null) {
                        updateStateResourceCallback.onUpdateResource(STATE_CONTENT, emptyImage, emptyMessageTV);
                    }
                } else {
                    //if it's loading or has content, we consider there is nothing,
                    //but if is error, it should change to loading state and then empty

                    //this fix adapter.notifyDataSetChange() method to change error state
                    //to empty state which is not the case
                    if (updateStateResourceCallback != null) {
                        updateStateResourceCallback.onUpdateResource(STATE_EMPTY, emptyImage, emptyMessageTV);
                    }
                    setDisplayState(STATE_EMPTY);
                }
            }
        });
    }

    //@OnClick(R.id.btn_empty_retry)
    public void onRetryButtonClick() {
        setDisplayState(STATE_CONTENT);
        if (refreshListener != null) {
            refreshListener.onRefresh(false);
        }
    }

    public void setUpdateStateResourceCallback(OnUpdateStateResourceCallback updateStateResourceCallback) {
        this.updateStateResourceCallback = updateStateResourceCallback;
    }


    public void notifyLoadFailed(Throwable error){
        loadMore = false;
        if (recyclerView != null) {
            recyclerView.refreshComplete();
            int itemCount = recyclerView.getLayoutManager().getItemCount();
            itemCount -= 2;//因为有header 还有 footer 啊
            if (currentPage == 1 && itemCount == 0) {
                if(updateStateResourceCallback!= null){
                    updateStateResourceCallback.onUpdateResource(STATE_ERROR, errorImage, errorMessageTV);
                }
                setDisplayState(STATE_ERROR);
            } else {
                setDisplayState(STATE_CONTENT);
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static interface OnUpdateStateResourceCallback{
        void onUpdateResource(int state, ImageView image, TextView textView);
    }

    private void loadMoreCompleted() {
        loadMore = false;
        setDisplayState(STATE_CONTENT);
    }

    public void setMoreListener(OnMoreListener moreListener) {
        this.moreListener = moreListener;
    }

    public void setPage(int currentPage, int totalPage) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
    }

    private void setDisplayState(int state) {
        this.displayState = state;
        loadingView.setVisibility(state == STATE_LOADING ? VISIBLE : GONE);
        errorView.setVisibility(state == STATE_ERROR ? VISIBLE : GONE);
        recyclerView.setVisibility(state == STATE_CONTENT ? VISIBLE : GONE);
        emptyView.setVisibility(state == STATE_EMPTY ? VISIBLE : GONE);
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parcelable);
        savedState.state = this.displayState;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.displayState = savedState.state;
        setDisplayState(this.displayState);
    }

    public void triggerRetry(){
        setDisplayState(STATE_LOADING);
        currentPage = 1;
        if(refreshListener != null){
            refreshListener.onRefresh(true);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_error_retry || i == R.id.btn_empty_retry) {
            setDisplayState(STATE_LOADING);
            currentPage = 1;
            if(refreshListener != null){
                refreshListener.onRefresh(true);
            }
        }
    }

    @Override
    public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(refreshListener != null)
                    refreshListener.onRefresh(true);
            }
        }, 500);
    }

    @Override
    public void onLoadMore() {
        //do nothing
    }

    public static interface OnRefreshListener {
        void onRefresh(boolean fromSwipe);
    }

    public static interface OnMoreListener {
        void onMore(int page);
    }


    static class SavedState extends BaseSavedState {
        private int state;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            try {
                state = source.readInt();
            } catch (IllegalArgumentException e) {
                state = STATE_LOADING;
            }
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }



    @Override
    public boolean isInAbsoluteStart() {
        return false;
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return !recyclerView.canScrollVertically(1);
    }

    public View getView(){
        return recyclerView;
    }
}
