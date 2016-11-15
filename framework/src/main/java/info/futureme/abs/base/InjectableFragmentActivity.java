package info.futureme.abs.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.animation.AnimationUtils;

import info.futureme.abs.R;

/**
 * activity with only one ActionbarFragment (with actionbar icon), & there is no
 * other code to customize in this activity
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 14:57:48
 */
public class InjectableFragmentActivity extends InjectableActivity {

    public static final String FRAGMENT_CLASS_NAME = "fragment_class_name";
    private InjectableFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        String fragmentName = bundle.getString(FRAGMENT_CLASS_NAME);
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Class<InjectableFragment> fragmentClass  = (Class<InjectableFragment>) Class.forName(fragmentName);
            fragment = fragmentClass.newInstance();
            super.onCreate(savedInstanceState);
            bundle.remove(FRAGMENT_CLASS_NAME);
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

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_with_fragment;
    }

    @Override
    public void finish() {
        //hold the fragment after finish
        if(fragment != null && fragment.getView() != null) {
            fragment.getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.hold));
        }
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
