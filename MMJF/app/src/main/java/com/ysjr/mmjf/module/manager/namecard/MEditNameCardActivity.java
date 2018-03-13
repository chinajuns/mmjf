package com.ysjr.mmjf.module.manager.namecard;

import android.os.Bundle;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MEditNameCardActivity extends TopBarBaseActivity {
  @Override protected int getContentView() {
    return R.layout.m_activity_edit_name_card;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.edit_name_card));
  }
}
