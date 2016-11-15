package info.futureme.abs.example.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import butterknife.Bind;
import info.futureme.abs.base.InjectableActivity;
import info.futureme.abs.example.R;
import info.futureme.abs.example.ui.fragment.ImageFragment;
import info.futureme.abs.example.ui.fragment.IntroFragment;
import me.relex.circleindicator.CircleIndicator;


/**
 * Created by Jeffrey on 6/15/16.
 */
public class IntroActivity extends InjectableActivity{
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.indicator)
    CircleIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.intro;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private Fragment [] fragments = new Fragment[]{
            ImageFragment.newInstance(R.drawable.intro1), new IntroFragment()
        };
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

