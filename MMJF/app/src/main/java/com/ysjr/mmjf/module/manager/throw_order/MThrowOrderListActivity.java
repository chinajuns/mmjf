package com.ysjr.mmjf.module.manager.throw_order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.MThrowOrderBean;
import com.ysjr.mmjf.module.adapter.CommonPagerAdapter;
import com.ysjr.mmjf.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MThrowOrderListActivity extends TopBarBaseActivity {
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.viewPager) ViewPager viewPager;
  private List<Fragment> mFragments = new ArrayList<>();
  private List<String> mTitles = new ArrayList<>();
  @Override protected int getContentView() {
    return R.layout.m_activity_throw_order_list;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.throw_order_list));
    initVp();
  }


  private void initVp() {
    mFragments.add(MThrowOrderRecordFragment.newInstance(0));
    mFragments.add(MThrowOrderRecordFragment.newInstance(1));
    mFragments.add(MThrowOrderRecordFragment.newInstance(2));
    mFragments.add(MThrowOrderRecordFragment.newInstance(3));
    mFragments.add(MThrowOrderRecordFragment.newInstance(4));
    mTitles.add("全部");
    mTitles.add("审核");
    mTitles.add("进行中");
    mTitles.add("已成交");
    mTitles.add("已过期");
    viewPager.setAdapter(new CommonPagerAdapter(getSupportFragmentManager(),mFragments,mTitles));
    tabLayout.setupWithViewPager(viewPager);
  }
}
