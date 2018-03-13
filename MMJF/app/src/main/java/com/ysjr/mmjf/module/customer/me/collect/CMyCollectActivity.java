package com.ysjr.mmjf.module.customer.me.collect;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.utils.CommonUtils;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CMyCollectActivity extends BaseActivity {
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  private CCollectManagerFragment mManagerFragment;
  private CCollectArticleFragment mArticleFragment;

  @Override public int getLayoutId() {
    return R.layout.c_activity_my_collect;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    initFragment(savedInstanceState);
    initTab();
  }

  private void initFragment(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mManagerFragment = (CCollectManagerFragment) getSupportFragmentManager().findFragmentByTag(
          CCollectManagerFragment.class.getName());
      mArticleFragment = (CCollectArticleFragment) getSupportFragmentManager().findFragmentByTag(
          CCollectArticleFragment.class.getName());
      getSupportFragmentManager().beginTransaction()
          .show(mManagerFragment)
          .hide(mArticleFragment)
          .commit();
    } else {
      mManagerFragment = CCollectManagerFragment.newInstance();
      mArticleFragment = CCollectArticleFragment.newInstance();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.frameContent, mManagerFragment, CCollectManagerFragment.class.getName())
          .add(R.id.frameContent, mArticleFragment, CCollectArticleFragment.class.getName())
          .show(mManagerFragment)
          .hide(mArticleFragment)
          .commit();
    }
  }

  private void initTab() {
    tabLayout.addTab(tabLayout.newTab().setText("信贷经理"));
    tabLayout.addTab(tabLayout.newTab().setText("文章"));
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
          getSupportFragmentManager().beginTransaction().show(mManagerFragment).hide(mArticleFragment).commit();
        } else {
          getSupportFragmentManager().beginTransaction().show(mArticleFragment).hide(mManagerFragment).commit();
        }
      }

      @Override public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override public void onTabReselected(TabLayout.Tab tab) {

      }
    });
  }

  @OnClick(R.id.btnBack) public void onViewClicked() {
    finish();
  }

}
