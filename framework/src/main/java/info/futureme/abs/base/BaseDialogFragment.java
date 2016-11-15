package info.futureme.abs.base;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

import info.futureme.abs.FApplication;
import info.futureme.abs.R;
import info.futureme.abs.util.DLog;
import rx.functions.Action1;


/**
 * since RxJava is on top of all async processing in dialogfragment lifecycle,
 * BaseDialogFragment is considered to be an instance of RxDialogFragment,
 * then observable could use compose to binding fragment lifecycle to itself.
 * and to be easy injected with base dialog fragment used for dialog styling in this
 * app, this dialog is customized using styling params from fragment argument bundle
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 14:51:51
 */
public class BaseDialogFragment extends RxDialogFragment{

    public static final java.lang.String GRAVITY = "gravity";
    public static final java.lang.String LAYOUT = "layout";
    public static final java.lang.String LAYOUT_HEIGHT = "layout_height";
    public static final java.lang.String LAYOUT_WIDTH = "layout_width";
    public static final String WINDOW_ANIM = "window_anim";
    public static final String DISMISSABLE = "dismiss_able";
    public static final String IGNORE_WATCH_REFERENCE = "ignore_watch_reference";
    //is normal dialogfragment, not loading!
    public static final java.lang.String IS_NORMAL = "is_normal";
    public static final java.lang.String NEED_TRANSPARENT = "need_transparent";
    private Rect outRect;
	/**
	 * listener to retrieve data from this fragment 
	 */
    protected DialogFragmentDismissListener mInterface;
    private Runnable runnable;
    private Dialog dialog;

    public static BaseDialogFragment newInstance(Bundle bundle, Runnable setup){
        BaseDialogFragment fragment = new BaseDialogFragment();
        fragment.setArguments(bundle);
        fragment.setRunnable(setup);
        return fragment;
    }

    public BaseDialogFragment(){
        if(getArguments() == null)
            setArguments(new Bundle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        boolean isNormal = getArguments().getBoolean(IS_NORMAL, true);
        if(isNormal) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.BaseDialogFragmentStyle);
        }else{
            boolean needTransparent = getArguments().getBoolean(NEED_TRANSPARENT, false);
            if(needTransparent){
                setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DeepColorDialogFragment);
            }else{
                setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TintColorDialogFragment);
            }
        }
        //setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DeepColorDialogFragment);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume(){
        outRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        // Auto size the dialog based on it's contents
        int width = getArguments().getInt(LAYOUT_WIDTH, 0);
        int height = getArguments().getInt(LAYOUT_HEIGHT, 0);
        int resId = getArguments().getInt(WINDOW_ANIM,-1);

        dialog = getDialog();

        if(-1 != resId){
            getDialog().getWindow().setWindowAnimations(resId);
        }
        if(width != 0 && height != 0) {
            getDialog().getWindow().setLayout(width, height);
        }else{
            getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        int layout = getArguments().getInt(LAYOUT, -1);
        int gravity = getArguments().getInt(GRAVITY, -1);
        if(gravity != Gravity.BOTTOM && gravity != -1) {
            getDialog().getWindow().setGravity(gravity);
        }
        if(layout != -1){
            getDialog().setContentView(layout);
        };
        if(getArguments().getBoolean(DISMISSABLE)) {
            setCancelable(true);
        }else{
            setCancelable(false);
        }

        //dialog customizing runnable
        if(runnable != null)
            runnable.run();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            RxView.detaches(view)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            dismissAllowingStateLoss();
                        }
                    });
        }
        return view;
    }

    /**
     * layout height
     * @param contentRelative
     * @return
     */
    public int getLayoutHeight(View contentRelative) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentRelative.measure(w, h);
        int height = contentRelative.getMeasuredHeight();
        if(outRect == null) return 0;
        return outRect.height() - height;
    }

    /**
     * is dialog in view
     * @param y
     * @param view
     * @return
     */
    public boolean isOutDistrict(int y,View view){
        int height = getLayoutHeight(view);
        return y<=height;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
	 * this listener is used to retrieve dialog fragment data to the observer.
     * @author Jeffrey
     * @version 1.0
	 * @updated 26-一月-2016 14:51:51
	 */
    public static interface DialogFragmentDismissListener {
        //retrieve data from the dialog fragment
        void onRetrieveDialogFragmentData(Bundle b, int Tag);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof  DialogFragmentDismissListener)
            mInterface = (DialogFragmentDismissListener) activity;
        else mInterface = null;
    }

    @Override
    public void onDetach(){
        mInterface = null;
        super.onDetach();
    }


    public void onDestroy() {
        mInterface = null;
        if(dialog != null) {
            dialog.setOnCancelListener(null);
            dialog.setOnDismissListener(null);
            dialog.setOnShowListener(null);
        }
        super.onDestroy();
        DLog.d(getClass().getName(), "onDestory!");

        if(getArguments()!= null && getArguments().getBoolean(BaseDialogFragment.IGNORE_WATCH_REFERENCE, false)){
            return;
        }

        RefWatcher refWatcher = FApplication.getRefWatcher();
        refWatcher.watch(this);
    }


    //fix bug commit fragment
    //http://stackoverflow.com/questions/14177781/java-lang-illegalstateexception-can-not-perform-this-action-after-onsaveinstanc
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        }catch (Exception e){}
    }
}
