package com.ysjr.mmjf.module.manager.home.msg;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.module.customer.msg.CMsgChildFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * 先使用C端的消息界面.后面根据数据修改
 */

public class MMsgActivity extends BaseActivity {
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.viewPager) ViewPager viewPager;
  private MMsgFragment mOrderFragment;
  private MMsgFragment mSysFragment;
  private String[] mTitles;
  private List<Fragment> mFragments;
  @Override public int getLayoutId() {
    return R.layout.m_activity_msg;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    mTitles = new String[]{getString(R.string.order_msg),getString(R.string.sys_msg)};
    initVp();
  }

  private void initVp() {
    mFragments = new ArrayList<>();
    mOrderFragment = MMsgFragment.newInstance(1);
    mSysFragment = MMsgFragment.newInstance(2);
    mFragments.add(mOrderFragment);
    mFragments.add(mSysFragment);
    PagerAdapter adapter = new PageAdapter(getSupportFragmentManager());
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
  }

  @OnClick(R.id.ivBack) public void back() {
    finish();
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
