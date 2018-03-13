package com.ysjr.mmjf.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CommonPagerAdapter extends FragmentStatePagerAdapter {
  private List<Fragment> mFragments;
  private List<String> mTitles;
  public CommonPagerAdapter(FragmentManager fm,List<Fragment> list,List<String> titles) {
    super(fm);
    mFragments = list;
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
