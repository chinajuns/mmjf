package com.ysjr.mmjf.module.customer.msg;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import butterknife.BindView;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.MsgWrapBean;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-11-28.
 */

public class CMsgFragment extends BaseFragment {
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.viewPager) ViewPager viewPager;
  private CMsgChildFragment mOrderFragment;
  private CMsgChildFragment mSysFragment;
  private String[] mTitles;
  private List<Fragment> mFragments;

  public CMsgFragment() {

  }
  @Override public int getLayoutId() {
    return R.layout.c_fragment_msg;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    mTitles = new String[]{getString(R.string.order_msg),getString(R.string.sys_msg)};
    initVp();
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (!hidden) {
      EventBus.getDefault().post(new MsgWrapBean());
    }
  }

  private void initVp() {
    mFragments = new ArrayList<>();
    mOrderFragment = CMsgChildFragment.newInstance(1);
    mSysFragment = CMsgChildFragment.newInstance(2);
    mFragments.add(mOrderFragment);
    mFragments.add(mSysFragment);
    PagerAdapter adapter = new PageAdapter(getChildFragmentManager());
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
  }

  public static CMsgFragment newInstance() {
    CMsgFragment fg = new CMsgFragment();
    return fg;
  }

  public void setTabPositionToSys() {
    if (tabLayout != null) tabLayout.setScrollPosition(1,0,true);
    if (viewPager != null) viewPager.setCurrentItem(1,true);
  }

  class PageAdapter extends FragmentStatePagerAdapter {
    public PageAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      return mFragments.get(position);
    }

    @Override public int getCount() {
      return mFragments.size();
    }

    @Override public CharSequence getPageTitle(int position) {
      return mTitles[position];
    }
  }

}
