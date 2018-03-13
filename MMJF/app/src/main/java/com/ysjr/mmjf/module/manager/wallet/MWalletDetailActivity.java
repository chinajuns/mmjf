package com.ysjr.mmjf.module.manager.wallet;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.Pay;
import com.ysjr.mmjf.module.manager.adapter.WalletDetailAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MWalletDetailActivity extends TopBarBaseActivity {
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  private List<Pay> mPayList = new ArrayList<>();
  private WalletDetailAdapter mAdapter;
  @Override protected int getContentView() {
    return R.layout.m_activity_wallet_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.pay_detail));
    initRv();
  }

  private void initRv() {
    for (int i = 0; i < 10; i++) {
      mPayList.add(new Pay());
    }
    mAdapter = new WalletDetailAdapter(R.layout.m_wallet_detail_item, mPayList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ActivityUtils.startActivity(MSinglePayActivity.class);
      }
    });
  }
}
