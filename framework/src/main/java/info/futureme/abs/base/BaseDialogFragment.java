package info.futureme.abs.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import info.futureme.abs.util.ViewServer;
import rx.functions.Action1;


/**
 * since RxJava is on top of all async processing in dialogfragment lifecycle,
 * BaseDialogFragment is considered to be an instance of RxDialogFragment,
 * then observable could use compose to binding fragment lifecycle to itself.
 * and to be easy injected with base dialog fragment used for dialog styling in this
 * app, this dialog is customized using styling params from fragment argument bundle
 * @author JeffreyHe
 * @version 1.0
 * @updated 26-一月-2016 14:51:51
 */
public class BaseDialogFragment extends RxDialogFragment {

    public static final String GRAVITY = "gravity";
    public static final String LAYOUT = "layout";
    public static final String LAYOUT_HEIGHT = "layout_height";
    public static final String LAYOUT_WIDTH = "layout_width";
    public static final String WINDOW_ANIM = "window_anim";
    public static final String DISMISSABLE = "dismiss_able";
    public static final String IGNORE_WATCH_REFERENCE = "ignore_watch_reference";
    //is normal dialogfragment, not loading!
    public static final String IS_NORMAL = "is_normal";
    public static final String NEED_TRANSPARENT = "need_transparent";
    private Rect outRect;
    /**
     * listener to retrieve data from this fragment
     */
    protected DialogFragmentDismissListener mInterface;
    private FragmentDecker fragmentDecker;
    private Dialog dialog;

    public static BaseDialogFragment newInstance(Bundle bundle, FragmentDecker fragmentDecker) {
        BaseDialogFragment fragment = new BaseDialogFragment();
        fragment.setArguments(bundle);
        fragment.setDecker(fragmentDecker);
        return fragment;
    }

    public static abstract class FragmentDecker {
        //on resume setting up method
        public abstract void setup(Dialog dialog);

        protected void decorate(Dialog dialog) {
            setup(dialog);
        }
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
        if (gravity != -1) {
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
        if (fragmentDecker != null)
            fragmentDecker.decorate(getDialog());
        super.onResume();
        if (FApplication.DEBUG)
            ViewServer.get(getContext()).setFocusedWindow(getView());
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
        if (FApplication.DEBUG)
            ViewServer.get(getContext()).addWindow(getDialog());
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

    public void setDecker(FragmentDecker fragmentDecker) {
        this.fragmentDecker = fragmentDecker;
    }

    /**
     * this listener is used to retrieve dialog fragment data to the observer.
     * @author JeffreyHe
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (FApplication.DEBUG)
            ViewServer.get(getContext()).removeWindow(getView());
        super.onDismiss(dialog);
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

