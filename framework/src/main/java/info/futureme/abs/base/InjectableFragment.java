package info.futureme.abs.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;
import info.futureme.abs.FApplication;
import info.futureme.abs.util.BugFix;
import info.futureme.abs.util.DLog;


/**
 * since RxJava is on top of all async processing in fragment lifecycle,
 * Injectablefragment is considered to be an instance of RxFragment,
 * then observable could use compose to binding fragment lifecycle to itself.
 * and to be easy injected with
 * viewid it is customized with butterknife, and across lifecycle debugging
 * support
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 17:52:02
 */
public abstract class InjectableFragment extends RxFragment{

    //this reference is used for fragment visibility change
    private Bundle savedInstanceState;
    private boolean resumed = false;
    private boolean onCreated = false;
    public static final String HARDWARE_ACCELERATED = "hardware_accelerated";
    public static final String IGNORE_WATCH = "ignore_watch";

    public InjectableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(provideContentRes(), container, false);
        injectViews(view);
        onCreated = true;
        return view;
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivity(Intent intent) {
        getActivity().startActivity(intent);
    }


    public void onCreate(Bundle b){
        super.onCreate(b);
        DLog.d(getClass().getName(), "onCreate!");
    }

    public abstract int provideContentRes();

    /**
     * ButterKnife injection
     */
    private void injectViews(View view) {
        ButterKnife.bind(this, view);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        DLog.i(getClass().getName(), "visible hint:" + isVisibleToUser);
        if (isVisibleToUser) {
            if(onCreated)
                onResume();
        } else {
            if(resumed) {
                onSaveInstanceState(savedInstanceState);
                onPause();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DLog.d(getClass().getName(), "onDetach!");
    }

    @Override
    public void onResume() {
        if(getArguments() != null){
            if(getArguments().getBoolean(HARDWARE_ACCELERATED, false)){
                DLog.i("hardware", "accelerated!");
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        }
        super.onResume();
        resumed = true;
        DLog.d(getClass().getName(), "onResume!");
        if(getUserVisibleHint()) {
            DLog.d(getClass().getName(), "onVisible!");
            onFragmentVisible(savedInstanceState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DLog.d(getClass().getName(), "onPause!");
        DLog.d(getClass().getName(), "onInVisible!");
        onFragmentInVisible(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        DLog.d(getClass().getName(), "onDestoryView!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DLog.d(getClass().getName(), "onDestory!");

        BugFix.destoryCallback(
                getView() == null ? null : getView().findViewById(provideContentRes())
        );

        if(getArguments() != null && getArguments().getBoolean(IGNORE_WATCH, false))
            return;
        RefWatcher refWatcher = FApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    //cannel loading or updating data
    protected abstract void onFragmentInVisible(Bundle savedInstanceState);

    //do loading?
    protected abstract void onFragmentVisible(Bundle savedInstanceState);

}
