package com.ysjr.mmjf.module;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.CUpdateMsgState;
import com.ysjr.mmjf.entity.HomeFilterItem;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.home.CHomeFragment;
import com.ysjr.mmjf.module.customer.loan.CLoanFragment;
import com.ysjr.mmjf.module.customer.me.CMeFragment;
import com.ysjr.mmjf.module.customer.msg.CMsgFragment;
import com.ysjr.mmjf.module.customer.news.NewsFragment;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.utils.Api;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-11-28.
 */

public class CustomerMainFragment extends BaseFragment {
  public static final int HIDE = 1;
  public static final int SHOW = 2;
  @BindView(R.id.ivHome) ImageView ivHome;
  @BindView(R.id.ivLoan) ImageView ivLoan;
  @BindView(R.id.ivNews) ImageView ivNews;
  @BindView(R.id.ivMsg) ImageView ivMsg;
  @BindView(R.id.ivMsgState) ImageView ivMsgState;
  @BindView(R.id.ivMe) ImageView ivMe;
  @BindView(R.id.layoutBottom) LinearLayout layoutBottom;
  private CHomeFragment mHomeFragment;
  private CLoanFragment mLoanFragment;
  private CMeFragment mMeFragment;
  private CMsgFragment mMsgFragment;
  private NewsFragment mNewsFragment;

  public static CustomerMainFragment newInstance() {
    CustomerMainFragment fg = new CustomerMainFragment();
    return fg;
  }

  @Override public int getLayoutId() {
    return R.layout.c_fragment_main;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    initFragments(savedInstanceState);
  }

  @Override public void onResume() {
    super.onResume();
    if (isLogin()) {
      checkMsg();
    } else {
      if (ivMsgState != null) ivMsgState.setVisibility(View.GONE);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    OkGo.getInstance().cancelTag(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void updateMsgState(CUpdateMsgState bean) {
    checkMsg();
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void hideOrDisplayBottom(HomeFilterItem item) {
    switch (item.id) {
      case HIDE:
        layoutBottom.setVisibility(View.GONE);
        break;
      case SHOW:
        layoutBottom.setVisibility(View.VISIBLE);
        break;
    }
  }
  private void initFragments(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mHomeFragment = (CHomeFragment) FragmentUtils.findFragment(getChildFragmentManager(),
          CHomeFragment.class);
      mLoanFragment = (CLoanFragment) FragmentUtils.findFragment(getChildFragmentManager(),
          CLoanFragment.class);
      mMsgFragment =
          (CMsgFragment) FragmentUtils.findFragment(getChildFragmentManager(), CMsgFragment.class);
      mNewsFragment = (NewsFragment) FragmentUtils.findFragment(getChildFragmentManager(),
          NewsFragment.class);
      mMeFragment =
          (CMeFragment) FragmentUtils.findFragment(getChildFragmentManager(), CMeFragment.class);
      getChildFragmentManager().beginTransaction()
          .show(mHomeFragment)
          .hide(mLoanFragment)
          .hide(mMsgFragment)
          .hide(mNewsFragment)
          .hide(mMeFragment)
          .commitAllowingStateLoss();
    } else {
      mHomeFragment = CHomeFragment.newInstance();
      mLoanFragment = CLoanFragment.newInstance();
      mMsgFragment = CMsgFragment.newInstance();
      mNewsFragment = NewsFragment.newInstance(NewsFragment.C);
      mMeFragment = CMeFragment.newInstance();
      getChildFragmentManager().beginTransaction()
          .add(R.id.frameContent, mHomeFragment, CHomeFragment.class.getName())
          .add(R.id.frameContent, mLoanFragment, CLoanFragment.class.getName())
          .add(R.id.frameContent, mMsgFragment, CMsgFragment.class.getName())
          .add(R.id.frameContent, mNewsFragment, NewsFragment.class.getName())
          .add(R.id.frameContent, mMeFragment, CMeFragment.class.getName())
          .show(mHomeFragment)
          .hide(mLoanFragment)
          .hide(mMsgFragment)
          .hide(mNewsFragment)
          .hide(mMeFragment)
          .commitAllowingStateLoss();
    }
  }


  private void checkMsg() {
    OkGo.<JSONObject>get(Api.MOBILE_CLIENT_CHECK_NOTICE).tag(this)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              String hasMsg = response.body().getJSONObject("data").getString("no_read");
              int sysNum = response.body().getJSONObject("data").getInt("system");
              int order = response.body().getJSONObject("data").getInt("order");
              if (hasMsg.equals("1")) {//有未读消息
                ivMsgState.setVisibility(View.VISIBLE);
                if (mMsgFragment != null) {
                  if (order == 0) {
                    mMsgFragment.setTabPositionToSys();
                  }
                }
              } else {
                ivMsgState.setVisibility(View.GONE);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  @OnClick({ R.id.layoutHome, R.id.layoutLoan, R.id.layoutNews, R.id.layoutMsg, R.id.layoutMe })
  public void onViewClicked(View view) {
    resetAllTab(view.getId());
    switch (view.getId()) {
      case R.id.layoutHome:
        ivHome.setImageResource(R.drawable.c_home_selected_img);
        getChildFragmentManager().beginTransaction()
            .show(mHomeFragment)
            .hide(mLoanFragment)
            .hide(mMsgFragment)
            .hide(mNewsFragment)
            .hide(mMeFragment)
            .commitAllowingStateLoss();
        break;
      case R.id.layoutLoan:
        ivLoan.setImageResource(R.drawable.c_loan_sel_img);
        getChildFragmentManager().beginTransaction()
            .show(mLoanFragment)
            .hide(mHomeFragment)
            .hide(mMsgFragment)
            .hide(mNewsFragment)
            .hide(mMeFragment)
            .commitAllowingStateLoss();
        break;
      case R.id.layoutNews:
        ivNews.setImageResource(R.drawable.c_news_sel_img);
        getChildFragmentManager().beginTransaction()
            .show(mNewsFragment)
            .hide(mLoanFragment)
            .hide(mHomeFragment)
            .hide(mMsgFragment)
            .hide(mMeFragment)
            .commitAllowingStateLoss();
        break;
      case R.id.layoutMsg:
        if (isLogin()) {
          ivMsg.setImageResource(R.drawable.c_msg_sel_img);
          getChildFragmentManager().beginTransaction()
              .show(mMsgFragment)
              .hide(mLoanFragment)
              .hide(mNewsFragment)
              .hide(mHomeFragment)
              .hide(mMeFragment)
              .commitAllowingStateLoss();
        } else {
          ActivityUtils.startActivity(NavigationActivity.class);
        }
        break;
      case R.id.layoutMe:
        ivMe.setImageResource(R.drawable.c_me_sel_img);
        getChildFragmentManager().beginTransaction()
                    .show(mMeFragment)
                    .hide(mLoanFragment)
                    .hide(mMsgFragment)
                    .hide(mNewsFragment)
                    .hide(mHomeFragment)
                    .commitAllowingStateLoss();
        break;
    }
  }

  private void resetAllTab(int id) {
    if (!isLogin() && id == R.id.layoutMsg) {
      return;
    }
    ivHome.setImageResource(R.drawable.c_home_unslected_img);
    ivLoan.setImageResource(R.drawable.c_loan_unsel_img);
    ivNews.setImageResource(R.drawable.c_ic_news_unsel);
    ivMsg.setImageResource(R.drawable.c_msg_unsel);
    ivMe.setImageResource(R.drawable.c_me_unsel_img);
  }
}
