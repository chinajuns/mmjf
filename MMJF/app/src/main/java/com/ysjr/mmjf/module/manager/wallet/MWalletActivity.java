package com.ysjr.mmjf.module.manager.wallet;

import android.os.Bundle;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MWalletActivity extends TopBarBaseActivity {
  @Override protected int getContentView() {
    return R.layout.m_activity_wallet;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTopRightButton("", R.drawable.m_ic_pay_list_detail, new OnClickListener() {
      @Override public void onClick() {
        ActivityUtils.startActivity(MWalletDetailActivity.class);
      }
    });
  }
  //提现
  @OnClick(R.id.btnWantMoney) public void onBtnWantMoneyClicked() {
    ActivityUtils.startActivity(MWantMoneyActivity.class);
  }
  //充值
  @OnClick(R.id.btnCostMoney) public void onBtnSaveMoneyClicked() {
    ActivityUtils.startActivity(MCostMoneyActivity.class);
  }
}
