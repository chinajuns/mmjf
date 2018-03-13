package com.ysjr.mmjf.module.manager.store;

import android.graphics.Color;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.module.manager.VerifyDialogFragment;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-12.
 */

public class MStoreNavigateActivity extends BaseActivity {

  @Override public int getLayoutId() {
    return R.layout.m_activity_store_navigate;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //  View decorView = getWindow().getDecorView();
    //  int option = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    //  decorView.setSystemUiVisibility(option);
    //  BarUtils.setStatusBarColor(mContext,Color.TRANSPARENT,0,true);
    //}
    ButterKnife.bind(this);
  }

  @OnClick(R.id.ivCreateStore) public void onIvCreateStoreClicked() {
    User user = DataSupport.findFirst(User.class);
    if (user.is_auth == 1) {
      VerifyDialogFragment verifyDialogFragment = VerifyDialogFragment.newInstance();
      verifyDialogFragment.show(getSupportFragmentManager(),"dialog");
    } else if (user.is_auth == 2) {
      ToastUtils.setBgColor(Color.BLACK);
      ToastUtils.showShort(getString(R.string.verifying));
    } else if (user.is_auth == 4) {
      ToastUtils.setBgColor(Color.BLACK);
      ToastUtils.showShort(getString(R.string.verify_fail));
    } else {
      ActivityUtils.startActivity(MCreateStoreActivity.class);
    }
  }

  @OnClick(R.id.ivClose) public void onIvCloseClicked() {
    finish();
  }

}
