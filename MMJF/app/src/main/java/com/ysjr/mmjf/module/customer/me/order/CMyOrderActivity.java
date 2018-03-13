package com.ysjr.mmjf.module.customer.me.order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import butterknife.BindView;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.module.adapter.CommonPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CMyOrderActivity extends TopBarBaseActivity {
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.viewPager) ViewPager viewPager;
  private CommonPagerAdapter mPageAdapter;
  private List<String> mTitles = new ArrayList<>();
  private List<Fragment> mFragments = new ArrayList<>();
  @Override protected int getContentView() {
    return R.layout.c_activity_my_order;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.my_order));
    initVp();
  }

  private void initVp() {
    mFragments.add(CMyOrderFragment.newInstance(CMyOrderFragment.TYPE_ALL));
    mFragments.add(CMyOrderFragment.newInstance(CMyOrderFragment.TYPE_PROCESSING));
    mFragments.add(CMyOrderFragment.newInstance(CMyOrderFragment.TYPE_EVALUATING));
    mFragments.add(CMyOrderFragment.newInstance(CMyOrderFragment.TYPE_LOAN_RECORDING));

    mTitles.add("全部");
    mTitles.add("办理中");
    mTitles.add("待评价");
    mTitles.add("贷款记录");

    mPageAdapter = new CommonPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
    viewPager.setAdapter(mPageAdapter);
    tabLayout.setupWithViewPager(viewPager);
  }
}
