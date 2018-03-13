package com.ysjr.mmjf.module.customer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;

/**
 * Created by Administrator on 2017-11-28.
 */

public class HomeVpAdapter extends FragmentStatePagerAdapter {
  private List<Fragment> mFragments;
  public HomeVpAdapter(FragmentManager fm,List<Fragment> fragments) {
    super(fm);
    mFragments = fragments;
  }

  @Override public Fragment getItem(int position) {
    return mFragments.get(position);
  }

  @Override public int getCount() {
    return mFragments.size();
  }

  @Override public int getItemPosition(Object object) {
    return POSITION_NONE;
  }
}
