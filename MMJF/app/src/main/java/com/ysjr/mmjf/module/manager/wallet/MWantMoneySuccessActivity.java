package com.ysjr.mmjf.module.manager.wallet;

import android.os.Bundle;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MWantMoneySuccessActivity extends TopBarBaseActivity {
  @Override protected int getContentView() {
    return R.layout.m_activity_want_money_success;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.want_money_success));
  }
}
