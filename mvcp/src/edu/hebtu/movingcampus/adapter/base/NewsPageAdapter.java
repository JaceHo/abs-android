package edu.hebtu.movingcampus.adapter.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import edu.hebtu.movingcampus.enums.NewsType;
import edu.hebtu.movingcampus.view.NewsFragment;

public class NewsPageAdapter extends FragmentPagerAdapter {


	private NewsFragment[] fragments;
	private Activity mActivity;

	public NewsPageAdapter(FragmentActivity activity) {
		super(activity.getSupportFragmentManager());
		this.mActivity = activity;
		fragments=new  NewsFragment[NewsType.values().length-1];
	}

	public List<Fragment> getFragments(){
		List<Fragment> l=new ArrayList<Fragment>();
		for (Fragment fragment : fragments) 
			l.add(fragment);
		return l;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return NewsType.values()[position+1].getDesc();
	}

	@Override
	public Fragment getItem(int arg0) {
		NewsFragment fragment = fragments[arg0];
		if (fragment == null) {
			fragment=NewsFragment.getInstance(arg0+"", mActivity);
			return fragments[arg0] = fragment;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return fragments.length;
	}

	@Override
	public int getItemPosition(Object object) {
		for(int i=0;i<fragments.length;i++)
			if(fragments[i].equals(object))
				return i;
		return POSITION_NONE;
	}

	//

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}
}
