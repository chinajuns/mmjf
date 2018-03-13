package com.ysjr.mmjf.module.manager.wallet;

import android.os.Bundle;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MSinglePayActivity extends TopBarBaseActivity {
  @Override protected int getContentView() {
    return R.layout.m_activity_single_pay;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.pay_detail));
  }
}
