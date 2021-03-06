package com.ysjr.mmjf.module.customer.store;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

/**
 * Created by Administrator on 2017-12-1.
 */

public class EvaluatePageAdapter extends FragmentPagerAdapter {
  private List<Fragment> mFragments;
  private List<String> mTitles;

  public EvaluatePageAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
    super(fm);
    mFragments = fragments;
    mTitles = titles;
  }

  @Override public Fragment getItem(int position) {
    return mFragments.get(position);
  }

  @Override public int getCount() {
    return mFragments.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return mTitles.get(position);
  }
}
