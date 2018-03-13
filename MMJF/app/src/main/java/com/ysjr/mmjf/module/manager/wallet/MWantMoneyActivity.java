package com.ysjr.mmjf.module.manager.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MWantMoneyActivity extends TopBarBaseActivity {
  @BindView(R.id.layoutWxSelect) FrameLayout layoutWxSelect;
  @BindView(R.id.layoutZfbSelect) FrameLayout layoutZfbSelect;
  @BindView(R.id.etMoney) EditText etMoney;
  @BindView(R.id.tvLeaveMoney) TextView tvLeaveMoney;
  @BindView(R.id.btnWantCurrent) Button btnWantCurrent;

  @Override protected int getContentView() {
    return R.layout.m_activity_want_money;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.i_want_money));
  }

  @OnClick(R.id.layoutWxPay) public void onLayoutWxPayClicked() {
    layoutWxSelect.setVisibility(View.VISIBLE);
    layoutZfbSelect.setVisibility(View.GONE);
  }

  @OnClick(R.id.layoutZfbPay) public void onLayoutZfbPayClicked() {
    layoutZfbSelect.setVisibility(View.VISIBLE);
    layoutWxSelect.setVisibility(View.GONE);

  }

  @OnClick(R.id.tvWantAll) public void onTvWantAllClicked() {

  }

  @OnClick(R.id.btnWantCurrent) public void onBtnWantCurrentClicked() {
    ActivityUtils.startActivity(MWantMoneySuccessActivity.class);
  }
}
