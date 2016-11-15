package info.futureme.abs.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import info.futureme.abs.util.BugFix;
import info.futureme.abs.util.DLog;

/**
 * dialog fragment that support <b>butterknife </b>injection
 * @author Jeffrey
 * @version 1.0
 * @updated 26-一月-2016 13:26:49
 */
public abstract class InjectableDialogFragment extends BaseDialogFragment {
    /**
	 * this reference is used for caching dataset when fragment visibility change
	 */
    private Bundle savedInstanceState;

    public InjectableDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(provideContentRes(), container, false);
        this.savedInstanceState = savedInstanceState;
        injectViews(view);
        return view;
    }

    public abstract int provideContentRes();

    /**
     * ButterKnife injection
     */
    private void injectViews(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DLog.d(getClass().getName(), "onDetach!");
    }

    @Override
    public void onResume() {
        super.onResume();
        DLog.d(getClass().getName(), "onResume!");
        onFragmentVisible(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        DLog.d(getClass().getName(), "onPause!");
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
        DLog.d(getClass().getName(), "onDestory!");
        super.onDestroy();
        BugFix.destoryCallback(
                getView() == null ? null : getView().findViewById(provideContentRes())
        );
    }

    /**
	 * cannel loading or updating data called when onpause
	 * 
	 * @param savedInstanceState
	 */
    protected abstract void onFragmentInVisible(Bundle savedInstanceState);

    /**
	 * do loading? called when on  resume
	 * 
	 * @param savedInstanceState
	 */
    protected abstract void onFragmentVisible(Bundle savedInstanceState);
}
