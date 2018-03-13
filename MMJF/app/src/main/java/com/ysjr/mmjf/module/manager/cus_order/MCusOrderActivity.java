package com.ysjr.mmjf.module.manager.cus_order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import butterknife.BindView;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.module.manager.adapter.MCommonPageAdapter;
import com.ysjr.mmjf.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MCusOrderActivity extends TopBarBaseActivity {
  public static final String KEY_REFER = "refer";
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.viewPager) ViewPager viewPager;
  private MCommonPageAdapter mPageAdapter;
  private List<String> mTitles = new ArrayList<>();
  private List<Fragment> mFragments = new ArrayList<>();
  private String refer;
  @Override protected int getContentView() {
    return R.layout.m_activity_cus_order;
  }

  @Override protected void init(Bundle savedInstanceState) {
    refer = getIntent().getStringExtra(KEY_REFER);
    if (refer.equals("customer")) {
      setTitle(getString(R.string.cus_order));
    } else if (refer.equals("junk")) {
      setTitle(getString(R.string.my_order));
    }
    initVp();
  }

  private void initVp() {
    mFragments.add(MCusOrderFragment.newInstance(MCusOrderFragment.TYPE_ALL,refer));
    mFragments.add(MCusOrderFragment.newInstance(MCusOrderFragment.TYPE_PROCESSING,refer));
    mFragments.add(MCusOrderFragment.newInstance(MCusOrderFragment.TYPE_EVALUATING,refer));
    mFragments.add(MCusOrderFragment.newInstance(MCusOrderFragment.TYPE_LOAN_RECORDING,refer));

    mTitles.add("全部");
    mTitles.add("办理中");
    mTitles.add("待评价");
    mTitles.add("订单记录");
    CommonUtils.reflex(tabLayout);
    mPageAdapter = new MCommonPageAdapter(getSupportFragmentManager(), mFragments, mTitles);
    viewPager.setAdapter(mPageAdapter);
    tabLayout.setupWithViewPager(viewPager);
  }
}
