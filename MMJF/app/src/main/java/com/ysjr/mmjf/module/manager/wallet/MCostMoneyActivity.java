package com.ysjr.mmjf.module.manager.wallet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MCostMoneyActivity extends TopBarBaseActivity {
  @BindView(R.id.layoutWxSelect) FrameLayout layoutWxSelect;
  @BindView(R.id.layoutZfbSelect) FrameLayout layoutZfbSelect;
  @BindView(R.id.layout500Select) FrameLayout layout500Select;
  @BindView(R.id.layout1000Select) FrameLayout layout1000Select;
  @BindView(R.id.layout2000Select) FrameLayout layout2000Select;
  @BindView(R.id.layout3000Select) FrameLayout layout3000Select;
  @BindView(R.id.layout5000Select) FrameLayout layout5000Select;
  @BindView(R.id.layout10000Select) FrameLayout layout10000Select;
  @BindView(R.id.layout20000Select) FrameLayout layout20000Select;
  @BindView(R.id.tv500) TextView tv500;
  @BindView(R.id.tv1000) TextView tv1000;
  @BindView(R.id.tv2000) TextView tv2000;
  @BindView(R.id.tv3000) TextView tv3000;
  @BindView(R.id.tv5000) TextView tv5000;
  @BindView(R.id.tv10000) TextView tv10000;
  @BindView(R.id.tv20000) TextView tv20000;

  @Override protected int getContentView() {
    return R.layout.m_activity_cost_money;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.score_cost));
  }

  @OnClick({
      R.id.layout500Pay, R.id.layout1000Pay, R.id.layout2000Pay, R.id.layout3000Pay,
      R.id.layout5000Pay, R.id.layout10000Pay, R.id.layout20000Pay
  }) public void onViewClicked(View view) {
    hideAllSelect();
    switch (view.getId()) {
      case R.id.layout500Pay:
        layout500Select.setVisibility(View.VISIBLE);
        tv500.setTextColor(Color.RED);
        break;
      case R.id.layout1000Pay:
        layout1000Select.setVisibility(View.VISIBLE);
        tv1000.setTextColor(Color.RED);
        break;
      case R.id.layout2000Pay:
        layout2000Select.setVisibility(View.VISIBLE);
        tv2000.setTextColor(Color.RED);
        break;
      case R.id.layout3000Pay:
        layout3000Select.setVisibility(View.VISIBLE);
        tv3000.setTextColor(Color.RED);
        break;
      case R.id.layout5000Pay:
        layout5000Select.setVisibility(View.VISIBLE);
        tv5000.setTextColor(Color.RED);
        break;
      case R.id.layout10000Pay:
        layout10000Select.setVisibility(View.VISIBLE);
        tv10000.setTextColor(Color.RED);
        break;
      case R.id.layout20000Pay:
        layout20000Select.setVisibility(View.VISIBLE);
        tv20000.setTextColor(Color.RED);
        break;
    }
  }

  /**
   * 隐藏所有充值金额的选中状态
   */
  private void hideAllSelect() {
    layout500Select.setVisibility(View.GONE);
    layout1000Select.setVisibility(View.GONE);
    layout2000Select.setVisibility(View.GONE);
    layout3000Select.setVisibility(View.GONE);
    layout5000Select.setVisibility(View.GONE);
    layout10000Select.setVisibility(View.GONE);
    layout20000Select.setVisibility(View.GONE);
    tv500.setTextColor(getResources().getColor(R.color.title_black_color));
    tv1000.setTextColor(getResources().getColor(R.color.title_black_color));
    tv2000.setTextColor(getResources().getColor(R.color.title_black_color));
    tv3000.setTextColor(getResources().getColor(R.color.title_black_color));
    tv5000.setTextColor(getResources().getColor(R.color.title_black_color));
    tv10000.setTextColor(getResources().getColor(R.color.title_black_color));
    tv20000.setTextColor(getResources().getColor(R.color.title_black_color));
  }

  @OnClick(R.id.layoutWxPay) public void onLayoutWxPayClicked() {
    layoutWxSelect.setVisibility(View.VISIBLE);
    layoutZfbSelect.setVisibility(View.GONE);
  }

  @OnClick(R.id.layoutZfbPay) public void onLayoutZfbPayClicked() {
    layoutZfbSelect.setVisibility(View.VISIBLE);
    layoutWxSelect.setVisibility(View.GONE);
  }

  @OnClick(R.id.btnCommit) public void onBtnCommitClicked() {
    ActivityUtils.startActivity(MCostSuccessActivity.class);
  }
}
