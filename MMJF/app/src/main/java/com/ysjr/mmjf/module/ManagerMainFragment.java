package com.ysjr.mmjf.module;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MStore;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.manager.VerifyDialogFragment;
import com.ysjr.mmjf.module.manager.home.MHomeFragment;
import com.ysjr.mmjf.module.manager.me.MMeFragment;
import com.ysjr.mmjf.module.manager.rub_order.MRubOrderFragment;
import com.ysjr.mmjf.module.manager.store.MStoreActivity;
import com.ysjr.mmjf.module.manager.store.MStoreNavigateActivity;
import com.ysjr.mmjf.module.manager.throw_order.MThrowOrderFragment;
import com.ysjr.mmjf.utils.Api;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-11-28.
 */

public class ManagerMainFragment extends BaseFragment {
  @BindView(R.id.ivHome) ImageView ivHome;
  @BindView(R.id.ivRubOrder) ImageView ivRubOrder;
  @BindView(R.id.ivThrowOrder) ImageView ivThrowOrder;
  @BindView(R.id.ivMe) ImageView ivMe;
  private MHomeFragment mHomeFragment;
  private MRubOrderFragment mRubFragment;
  private MThrowOrderFragment mThrowFragment;
  private MMeFragment mMeFragment;
  private VerifyDialogFragment verifyDialogFragment;
  public static ManagerMainFragment newInstance() {
    ManagerMainFragment fg = new ManagerMainFragment();
    return fg;
  }

  @Override public int getLayoutId() {
    return R.layout.m_fragment_main;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    initFragments(savedInstanceState);
    EventBus.getDefault().register(this);
  }


  @Override public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }
  private void httpCheckIfHasStore() {
    OkGo.<HttpResult<MStore>>post(Api.MOBILE_BUSINESS_SHOP_INDEX)
        .tag(mContext)
        .execute(new JsonCallback<HttpResult<MStore>>() {
          @Override public void onSuccess(Response<HttpResult<MStore>> response) {
           MStore store = response.body().data;
            if (store.check_result == 0 || store.check_result == 3) {
              ActivityUtils.startActivity(MStoreNavigateActivity.class);
            } else if (store.check_result == 1) {
              ToastUtils.setBgColor(Color.BLACK);
              ToastUtils.showShort("店铺审核中");
            } else if (store.check_result == 2) {
              ActivityUtils.startActivity(MStoreActivity.class);
            }
          }
        });
  }
  private void initFragments(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mHomeFragment = (MHomeFragment) FragmentUtils.findFragment(getChildFragmentManager(),
          MHomeFragment.class);
      mRubFragment = (MRubOrderFragment) FragmentUtils.findFragment(getChildFragmentManager(),
          MRubOrderFragment.class);
      mThrowFragment = (MThrowOrderFragment) FragmentUtils.findFragment(getChildFragmentManager(),
          MThrowOrderFragment.class);
      mMeFragment =
          (MMeFragment) FragmentUtils.findFragment(getChildFragmentManager(), MMeFragment.class);
      getChildFragmentManager().beginTransaction()
          .show(mHomeFragment)
          .hide(mRubFragment)
          .hide(mThrowFragment)
          .hide(mMeFragment)
          .commitAllowingStateLoss();
    } else {
      mHomeFragment = MHomeFragment.newInstance();
      mRubFragment = MRubOrderFragment.newInstance();
      mThrowFragment = MThrowOrderFragment.newInstance();
      mMeFragment = MMeFragment.newInstance();
      getChildFragmentManager().beginTransaction()
          .add(R.id.frameContent, mHomeFragment, MHomeFragment.class.getName())
          .add(R.id.frameContent, mRubFragment, MRubOrderFragment.class.getName())
          .add(R.id.frameContent, mThrowFragment, MThrowOrderFragment.class.getName())
          .add(R.id.frameContent, mMeFragment, MMeFragment.class.getName())
          .show(mHomeFragment)
          .hide(mRubFragment)
          .hide(mThrowFragment)
          .hide(mMeFragment)
          .commitAllowingStateLoss();
    }
  }

  User user;

  @OnClick({
      R.id.layoutHome, R.id.layoutRubOrder, R.id.layoutStore, R.id.layoutThrowOrder, R.id.layoutMe
  }) public void onViewClicked(View view) {
    user = DataSupport.findFirst(User.class);
    if (view.getId() != R.id.layoutStore) {
      resetAllTab(user,view.getId());
    }
    switch (view.getId()) {
      case R.id.layoutHome:
        ivHome.setImageResource(R.drawable.m_home_sel);
        getChildFragmentManager().beginTransaction()
            .show(mHomeFragment)
            .hide(mRubFragment)
            .hide(mThrowFragment)
            .hide(mMeFragment)
            .commitAllowingStateLoss();
        break;
      case R.id.layoutRubOrder:
        ivRubOrder.setImageResource(R.drawable.m_rub_order_sel);
        getChildFragmentManager().beginTransaction()
            .show(mRubFragment)
            .hide(mHomeFragment)
            .hide(mThrowFragment)
            .hide(mMeFragment)
            .commitAllowingStateLoss();
        break;
      case R.id.layoutStore:
        Log.e("ManagerMainFragment", "is_auth =" + user.is_auth);
        if (user.is_auth == 1) {
          verifyDialogFragment = VerifyDialogFragment.newInstance();
          verifyDialogFragment.show(getChildFragmentManager(), "dialog");
        } else if (user.is_auth == 2) {
          ToastUtils.setBgColor(Color.BLACK);
          ToastUtils.showShort(getString(R.string.verifying));
        } else if (user.is_auth == 4) {
          ToastUtils.setBgColor(Color.BLACK);
          ToastUtils.showShort(getString(R.string.verify_fail));
        } else {
          httpCheckIfHasStore();
        }
        break;
      case R.id.layoutThrowOrder:
        if (user.is_auth == 1) {
          verifyDialogFragment = VerifyDialogFragment.newInstance();
          verifyDialogFragment.show(getChildFragmentManager(), "dialog");
        } else if (user.is_auth == 2) {
          ToastUtils.setBgColor(Color.BLACK);
          ToastUtils.showShort(getString(R.string.verifying));
        } else if (user.is_auth == 4) {
          ToastUtils.setBgColor(Color.BLACK);
          ToastUtils.showShort(getString(R.string.verify_fail));
        } else {
          ivThrowOrder.setImageResource(R.drawable.m_throw_order_sel);
          getChildFragmentManager().beginTransaction()
              .show(mThrowFragment)
              .hide(mRubFragment)
              .hide(mHomeFragment)
              .hide(mMeFragment)
              .commitAllowingStateLoss();
        }
        break;
      case R.id.layoutMe:
        ivMe.setImageResource(R.drawable.m_me_sel);
        getChildFragmentManager().beginTransaction()
            .show(mMeFragment)
            .hide(mRubFragment)
            .hide(mThrowFragment)
            .hide(mHomeFragment)
            .commitAllowingStateLoss();
        break;
    }
  }

  private void resetAllTab(User user, int id) {
    if (id == R.id.layoutThrowOrder) {
      if (user != null && user.is_auth == 3) {
        ivThrowOrder.setImageResource(R.drawable.m_throw_order_unsel);
        ivHome.setImageResource(R.drawable.m_home_unsel);
        ivRubOrder.setImageResource(R.drawable.m_rub_order_unsel);
        ivMe.setImageResource(R.drawable.m_me_unsel);
      }
    } else {
      ivThrowOrder.setImageResource(R.drawable.m_throw_order_unsel);
      ivHome.setImageResource(R.drawable.m_home_unsel);
      ivRubOrder.setImageResource(R.drawable.m_rub_order_unsel);
      ivMe.setImageResource(R.drawable.m_me_unsel);
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onEventMainThread(Order order) {
    ivHome.setImageResource(R.drawable.m_home_unsel);
    switch (order.type) {
      case MHomeFragment.RUB_ORDER:
        ivRubOrder.setImageResource(R.drawable.m_rub_order_sel);
        getChildFragmentManager().beginTransaction()
            .show(mRubFragment)
            .hide(mHomeFragment)
            .hide(mThrowFragment)
            .hide(mMeFragment)
            .commitAllowingStateLoss();
        break;
      case MHomeFragment.THROW_ORDER:
        ivThrowOrder.setImageResource(R.drawable.m_throw_order_sel);
        getChildFragmentManager().beginTransaction()
            .show(mThrowFragment)
            .hide(mRubFragment)
            .hide(mHomeFragment)
            .hide(mMeFragment)
            .commitAllowingStateLoss();
        break;
    }
  }
}
