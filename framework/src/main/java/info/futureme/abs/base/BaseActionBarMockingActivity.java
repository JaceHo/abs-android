package info.futureme.abs.base;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.concurrent.TimeUnit;

import info.futureme.abs.R;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.image.FastBlur;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Since we cannot customize the android framework actionbar in so many ways, we
 * mock an actionbar to our needs with title, menu, tools button available here.
 * @author Jeffrey
 * @version 1.0
 * @updated 25-一月-2016 15:39:39
 */
public abstract class BaseActionBarMockingActivity extends InjectableActivity {
    public static final long QUERY_UPDATE_DELAY_MILLIS = 200;
    public static final long QUERY_TIMEOUT = 2000;
    TextView title;
    RelativeLayout navTitleLayout;
    ImageView rightBtn;
    ImageView leftBtn;
    FrameLayout leftLayout;
    FrameLayout rightLayout;
    View rightLine;
    View leftLine;
    EditText searchText;
    RelativeLayout searchBox;
    private OnSearchListener listener;
    private boolean searching = false;
    private TextView rightTitle;
    private TextView leftTitle;
    private ImageView blurImage;
    private FrameLayout actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        title = (TextView) findViewById(R.id.actionbar_title);
        if(title == null) return;
        actionbar = (FrameLayout) findViewById(R.id.action_bar);
        blurImage = (ImageView)findViewById(R.id.blur_image);
        navTitleLayout = (RelativeLayout) findViewById(R.id.inner_title_layout);
        leftTitle = (TextView) findViewById(R.id.title_left);
        rightTitle = (TextView) findViewById(R.id.title_right);
        rightBtn = (ImageView) findViewById(R.id.actionbar_right);
        leftBtn = (ImageView) findViewById(R.id.actionbar_left);
        leftLayout = (FrameLayout) findViewById(R.id.actionbar_left_layout);
        rightLayout = (FrameLayout) findViewById(R.id.actionbar_right_layout);
        rightLine = findViewById(R.id.actionbar_title_right_line);
        leftLine = findViewById(R.id.actionbar_title_left_line);
        searchText = (EditText) findViewById(R.id.actionbar_search_text);
        searchBox = (RelativeLayout) findViewById(R.id.search_box_layout);
        rightTitle.setTag(false);
        updateLeftBtn();
        updateTitle();
        updateRightBtn();

        // fast blur
        /*
        ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0)
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                applyBlurRS();
                            }
                        });
        //applyBlurFB();
        */
    }


    private void applyBlurFB() {
        blurImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                blurImage.getViewTreeObserver().removeOnPreDrawListener(this);
                blurImage.buildDrawingCache();

                Bitmap bmp = blurImage.getDrawingCache();
                blurFB(bmp, actionbar);
                return true;
            }
        });
    }

    /**
     * fast blur effect
     */
    private void blurFB(Bitmap bkg, View view) {
        float scaleFactor = 1;
        float radius = 20;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(new BitmapDrawable(getResources(), overlay));
        }else{
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        }
    }


    /**
     * apply blur effect
     */
    private void applyBlurRS() {
        blurImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                blurImage.getViewTreeObserver().removeOnPreDrawListener(this);
                blurImage.buildDrawingCache();

                Bitmap bmp = blurImage.getDrawingCache();
                blurRS(bmp, actionbar);
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    /**
     * render script blur effect
     */
    private void blurRS(Bitmap bkg, View view) {
        float scaleFactor = 1;
        float radius = 20;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);

        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        RenderScript rs = RenderScript.create(this);

        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);

        view.setBackground(new BitmapDrawable(
                getResources(), overlay));

        rs.destroy();
    }


	/**
	 * trigger search ui called to show or hide search view
	 * 
	 * @param enable
	 */
    public void triggerSearchUi(boolean enable){
        searchBox.setVisibility(enable ? View.VISIBLE : View.GONE);
        updateTitle();
        leftBtn.setImageResource(enable ? R.drawable.back_arow: getActionBarLeftResourceId());
        if(enable) {
            leftLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    triggerSearchUi(false);
                    updateLeftBtn();
                }
            });
        }
        if(listener != null){
            if(enable) listener.onEnterSearchMode();
            else listener.onExitSearchMode();;
        }
        searching = enable;
    }

	/**
	 * update left button image when resource changes
	 */
    protected void updateLeftBtn(){
        leftBtn.setImageResource(getActionBarLeftResourceId());
        leftLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onActionBarLeftClick();
            }
        });
    }

    protected void updateSearch() {
        searchText.setHint(getSearchHint());
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH) { //Whenever you got user click enter. Get text in edittext and check it equal test1. If it's true do your code in listenerevent of button3
                    if (listener != null) {
                        listener.onSearchEnter();
                        return true;
                    }
                }
                return false;
            }
        });
        RxTextView.textChangeEvents(searchText)
                .filter(new Func1<TextViewTextChangeEvent, Boolean>() {
                    @Override
                    public Boolean call(TextViewTextChangeEvent textViewTextChangeEvent) {
                        return textViewTextChangeEvent.text().length() > 0;
                    }
                })
                .debounce(QUERY_UPDATE_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<TextViewTextChangeEvent, Observable<String>>() {
                    @Override
                    public Observable<String> call(TextViewTextChangeEvent textViewTextChangeEvent) {
                        return Observable.just(textViewTextChangeEvent.text().toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                               @Override
                               public void call(String s) {
                                   DLog.d("search:", s);
                                   if (listener != null)
                                       listener.onTextChanged(s);
                               }
                           }
                );
    }

    public void setSearchListener(OnSearchListener listener) {
        this.listener = listener;
        updateSearch();
    }

	/**
	 * whether search mode is enabled or search ui is shown.
	 */
    public boolean isSearching() {
        return searching;
    }

    public String getRightTitle() {
        return null;
    }

    public String getLeftTitle() {
        return null;
    }

    public String getSearchHint() {
        return "";
    }

    /**
	 * Search listener triggers when search <u>mode enable,</u> <u>search keyword
	 * changes</u>, and <u>exit search mode</u>. When keyword changes, the search is
	 * happend across local list  adapter's dataset, and <i>onSearchEnter()</i> is
	 * responsible for retrieve network search result when use hit enter.
     * @author Jeffrey
     * @version 1.0
	 * @updated 25-一月-2016 15:39:39
	 */
    public static interface OnSearchListener{
		/**
		 * called whenever search keyword changes
		 * 
		 * @param text
		 */
        void onTextChanged(String text);
		/**
		 * called when use hit enter to retrieve network search result
		 */
        void onSearchEnter();
		/**
		 * called when trigger search mode and show the search UI.
		 */
        void onEnterSearchMode();
		/**
		 * Called when trigger exiting search mode and hide search UI.
		 */
        void onExitSearchMode();
    }

    /**
     * update right button using rightresource
     */
    protected void updateRightBtn() {
        if (getActionBarRightResourceId() != 0) {
            rightBtn.setImageResource(getActionBarRightResourceId());
            rightLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onActionBarRightClick();
                }
            });
            rightLayout.setVisibility(View.VISIBLE);
        } else {
            rightLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * update title when title changes
     */
    protected void updateTitle() {
        if (getActionBarTitleStringId() != 0) {
            title.setText(getActionBarTitleStringId());
            title.setVisibility(View.VISIBLE);
            navTitleLayout.setVisibility(View.GONE);
            return;
        }
        if (getActionBarTitleString() != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(getActionBarTitleString());
            navTitleLayout.setVisibility(View.GONE);
            return;
        }

        if(null != getRightTitle() && null != getLeftTitle()) {
            title.setVisibility(View.GONE);
            navTitleLayout.setVisibility(View.VISIBLE);
            leftTitle.setText(getLeftTitle());
            rightTitle.setText(getRightTitle());
            leftTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    showTitleLayout();
                }
            });
        }
    }

    //show title layout in actionbar
    private void showTitleLayout() {
        final int[] rl = new int[2];
        final int[] ll = new int[2];
        leftTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightLine.getLocationOnScreen(rl);
                leftLine.getLocationOnScreen(ll);
                rightTitle.setTextColor(Color.parseColor("#758694"));
                leftTitle.setTextColor(getResources().getColor(R.color.app_default_blue));
                final int diff = rl[0] - ll[0];
                rightLine.animate().setDuration(400).translationX(-diff).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rightLine.setVisibility(View.INVISIBLE);
                        leftLine.setVisibility(View.VISIBLE);
                        rightLine.setTranslationX(0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        rightLine.setVisibility(View.INVISIBLE);
                        leftLine.setVisibility(View.VISIBLE);
                        rightLine.setTranslationX(0);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                onTitleLeftClick();
            }
        });
        rightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightLine.getLocationOnScreen(rl);
                leftTitle.setTextColor(Color.parseColor("#758694"));
                rightTitle.setTextColor(getResources().getColor(R.color.app_default_blue));
                leftLine.getLocationOnScreen(ll);
                final int diff = rl[0] - ll[0];
                leftLine.animate().setDuration(400).translationX(diff).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rightLine.setVisibility(View.VISIBLE);
                        leftLine.setVisibility(View.INVISIBLE);
                        leftLine.setTranslationX(0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        rightLine.setVisibility(View.VISIBLE);
                        leftLine.setVisibility(View.INVISIBLE);
                        leftLine.setTranslationX(0);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                onTitleRightClick();
            }
        });

    }

    protected String getActionBarTitleString() {
        return null;
    }

    protected int getActionBarLeftResourceId() {
        return R.drawable.actionbar_menu;
    }

    protected int getActionBarRight2ResourceId() {
        return 0;
    }

    protected int getActionBarRightResourceId() {
        return 0;
    }

	/**
	 * actionbar left image icon click
	 */
    protected void onActionBarLeftClick() {
    }

	/**
	 * called when the second image icon from right is clicked
	 */
    protected void onActionBarRight2Click() {
    }

	/**
	 * right image icon click
	 */
    protected void onActionBarRightClick() {
    }

    protected void onActionBarRightStatusClick() {
    }

    protected int getActionBarTitleStringId() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        this.finish();
        //super.onBackPressed();
        //this cause webview memory leaking around fragment, activity lifecycle
    }

	/**
	 * middle left title clicked
	 */
    protected void onTitleLeftClick() {
    }

    protected void onTitleRightClick() {
    }

}
