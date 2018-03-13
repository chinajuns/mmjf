package com.ysjr.mmjf.module;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Process;
import android.util.Log;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;

/**
 * Created by Administrator on 2017-11-28.
 */

public class MainActivity extends BaseActivity {
  public static final String IS_CUSTOMER_LOGIN_KEY = "is_customer_login";
  private static final String TAG = MainActivity.class.getSimpleName();
  private boolean isCustomerLogin = true;
  private CustomerMainFragment mCustomerFragment;
  private ManagerMainFragment mManagerFragment;
  public boolean isPause;
  @Override public int getLayoutId() {
    return R.layout.activity_main;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    if (getIntent() != null) {
      isCustomerLogin = getIntent().getBooleanExtra(IS_CUSTOMER_LOGIN_KEY, true);
    }

    if (savedInstanceState != null) {
      isCustomerLogin = savedInstanceState.getBoolean(IS_CUSTOMER_LOGIN_KEY, true);
      if (isCustomerLogin) {
        mCustomerFragment = (CustomerMainFragment) getSupportFragmentManager().findFragmentByTag(
            CustomerMainFragment.class.getSimpleName());
      } else {
        mManagerFragment = (ManagerMainFragment) getSupportFragmentManager().findFragmentByTag(
            ManagerMainFragment.class.getSimpleName());
      }
    } else {
      if (isCustomerLogin) {
        mCustomerFragment = CustomerMainFragment.newInstance();
      } else {
        mManagerFragment = ManagerMainFragment.newInstance();
      }
    }

    if (isCustomerLogin) {
      if (mCustomerFragment == null) {
        return;
      }
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.frameContent, mCustomerFragment, CustomerMainFragment.class.getSimpleName())
          .commitAllowingStateLoss();
    } else {
      if (mManagerFragment == null) {
        return;
      }
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.frameContent, mManagerFragment, ManagerMainFragment.class.getSimpleName())
          .commitAllowingStateLoss();
    }

  }

  @Override protected void onNewIntent(Intent intent) {
    Log.e(TAG, "onNewIntent");
    super.onNewIntent(intent);
    isCustomerLogin = intent.getBooleanExtra(IS_CUSTOMER_LOGIN_KEY, true);
    Log.e(TAG, "islogin = " + isCustomerLogin);
    if (isCustomerLogin) {
      mCustomerFragment = CustomerMainFragment.newInstance();
    } else {
      mManagerFragment = ManagerMainFragment.newInstance();
    }
    if (isCustomerLogin) {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.frameContent, mCustomerFragment, CustomerMainFragment.class.getSimpleName())
          .commit();
    } else {
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.frameContent, mManagerFragment, ManagerMainFragment.class.getSimpleName())
          .commit();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    isPause = false;
  }

  @Override protected void onPause() {
    super.onPause();
    isPause = true;
  }

  @Override public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    super.onSaveInstanceState(outState, outPersistentState);
    outState.putBoolean(IS_CUSTOMER_LOGIN_KEY,isCustomerLogin);
  }

  private long current;
  @Override public void onBackPressed() {
    if (System.currentTimeMillis() - current > 2000) {
      current = System.currentTimeMillis();
      ToastUtils.setBgColor(Color.BLACK);
      ToastUtils.showShort("再按一次退出毛毛金服");
    } else {
      ActivityUtils.finishAllActivities();
      System.exit(0);
      Process.killProcess(Process.myPid());
    }
  }
}
