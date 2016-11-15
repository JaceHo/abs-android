package info.futureme.abs.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.AnimationUtils;

import java.util.List;

import info.futureme.abs.R;
import info.futureme.abs.view.ContentLoaderView;

/**
 * Created by hippo on 12/5/15.
 * activity with only one ActionbarFragment (with actionbar icon),
 * & there is no other code to customize in this activity
 */
public class ActionBarFragmentActivity extends BaseSecondaryActivity {

    public static final String FRAGMENT_CLASS_NAME = "fragment_class_name";
    public static final String FRAGMENT_TITLE = "fragment_title";
    public static final String RIGHT_ACTIONBAR_ENABLE = "right_action_bar_enable";
    //whether fragment is contentloadivew and refreshable
    public static final String BACK_REFRESHABLE = "contentloader_refresh_able";
    public static final String IS_ACTIONBAR_SHOWN = "is_actionbar_shown";
    public static final String FRAGMENT_RIGHT_TITLE = "right_title";
    public static final String FRAGMENT_LEFT_TITLE = "left_title";
    private String title, leftTitle, rightTitle;
    private Fragment fragment;
    private boolean rightEnable;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bundle = getIntent().getExtras();
        rightEnable = bundle.getBoolean(RIGHT_ACTIONBAR_ENABLE, false);
        String fragmentName = bundle.getString(FRAGMENT_CLASS_NAME);
        if (bundle.getInt(FRAGMENT_TITLE) == 0) {
            title = null;
        } else {
            title = getString(bundle.getInt(FRAGMENT_TITLE));
        }
        if(bundle.getInt(FRAGMENT_LEFT_TITLE) == 0){
            leftTitle = null;
        }else {
            leftTitle = getString(bundle.getInt(FRAGMENT_LEFT_TITLE));
        }
        if(bundle.getInt(FRAGMENT_RIGHT_TITLE) == 0){
            rightTitle = null;
        }else {
            rightTitle = getString(bundle.getInt(FRAGMENT_RIGHT_TITLE));
        }
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Class<Fragment> fragmentClass  = (Class<Fragment>) Class.forName(fragmentName);
            fragment = fragmentClass.newInstance();
            super.onCreate(savedInstanceState);
            //bundle.remove(FRAGMENT_TITLE);
            //bundle.remove(FRAGMENT_CLASS_NAME);
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (ClassNotFoundException e) {
            super.onCreate(savedInstanceState);
            e.printStackTrace();
        } catch (InstantiationException e) {
            super.onCreate(savedInstanceState);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            super.onCreate(savedInstanceState);
            e.printStackTrace();
        }
    }

    protected int getActionBarRightResourceId() {
        if(rightEnable)
            return ((ActionBarInterface)fragment).getActionBarRightResourceId();
        else
            return 0;
    }

    @Override
    protected int provideContentViewId() {
        if(bundle.getBoolean(IS_ACTIONBAR_SHOWN, true)) {
            return R.layout.activity_with_actionbar_fragment;
        }else{
            return R.layout.activity_with_fragment;
        }
    }

    protected String getActionBarTitleString() {
        return title;
    }

    protected void onActionBarRightClick() {
        ((ActionBarInterface)fragment).onActionBarRightClick();
    }

    @Override
    public void finish() {
        //hold the fragment before finish
        if(fragment != null && fragment.getView() != null) {
            fragment.getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.hold));
            List<Fragment> fragmentList = fragment.getChildFragmentManager().getFragments();
            if(fragmentList != null && fragmentList.size() > 0) {
                for (int i = 0; i < fragmentList.size(); i++) {
                    if (fragmentList.get(i) != null && fragmentList.get(i).getView() != null) {
                        fragmentList.get(i).getView().startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.hold));
                    }
                }
            }
        }
        super.finish();
    }

    public void onDestroy(){
        fragment = null;
        super.onDestroy();
    }


    protected int getActionBarRight2ResourceId() {
        return ((ActionBarInterface)fragment).getActionBarRight2ResourceId();
    }


    protected void onActionBarRight2Click() {
        ((ActionBarInterface)fragment).onActionBarRight2Click();
    }


    @Override
    public void onBackPressed() {
        if(fragment != null){
            if(!((ActionBarInterface)fragment).onBackPressed()){
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getBoolean(BACK_REFRESHABLE, false)) {
            if (resultCode == RESULT_OK) {
                ((ContentLoaderView.OnRefreshListener) fragment).onRefresh(false);
            }
        }
    }


    @Override
    public String getRightTitle() {
        return rightTitle;
    }

    @Override
    public String getLeftTitle() {
        return leftTitle;
    }

    protected void onTitleLeftClick() {
        ((ActionBarInterface) fragment).onActionBarTitleLeftClick();
    }

    protected void onTitleRightClick() {
        ((ActionBarInterface) fragment).onActionBarTitleRightClick();
    }
}
