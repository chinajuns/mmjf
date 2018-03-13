package com.ysjr.mmjf.module.manager.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseApplication;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.JsonBean;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.calculator.CalculatorActivity;
import com.ysjr.mmjf.module.customer.me.score.CScoreActivity;
import com.ysjr.mmjf.module.manager.VerifyDialogFragment;
import com.ysjr.mmjf.module.manager.home.msg.MMsgActivity;
import com.ysjr.mmjf.module.manager.home.news.MNewsActivity;
import com.ysjr.mmjf.module.manager.namecard.MNameCardActivity;
import com.ysjr.mmjf.module.manager.wallet.MWalletActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.zaaach.citypicker.CityPickerActivity;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-8.
 */

public class MHomeFragment extends BaseFragment {
  private static final int REQUEST_CODE_PICK_CODE = 1;
  public static final int RUB_ORDER = 2;
  public static final int THROW_ORDER = 3;
  private static final String TAG = MHomeFragment.class.getSimpleName();
  @BindView(R.id.viewBanner) Banner viewBanner;
  @BindView(R.id.tvCity) TextView tvCity;
  @BindView(R.id.ivMsg) ImageView ivMsg;
  @BindView(R.id.ivDot1) ImageView ivDot1;
  @BindView(R.id.ivDot2) ImageView ivDot2;
  @BindView(R.id.ivDot3) ImageView ivDot3;
  @BindView(R.id.layoutRoot) FrameLayout layoutRoot;
  @BindView(R.id.tvRubNum) TextView tvRubNum;
  private LocationClient mLocationClient;
  private String city;
  private float mCurrentLat;
  private float mCurrentLon;
  private VerifyDialogFragment verifyDialogFragment;
  private boolean isFirstIn = true;

  public static MHomeFragment newInstance() {
    MHomeFragment fg = new MHomeFragment();
    return fg;
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden) {
      BarUtils.setStatusBarColor(mContext, getResources().getColor(R.color.theme_color), 0, true);
    } else {
      BarUtils.setStatusBarColor(mContext, Color.BLACK, 0, true);
    }
  }
  @Override public int getLayoutId() {
    return R.layout.m_fragment_home;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    BarUtils.setStatusBarColor(mContext, Color.BLACK, 0, true);
    initLocation();
  }
  @Override public void onResume() {
    super.onResume();
    initData();
  }
  @Override public void onDestroyView() {
    OkGo.getInstance().cancelTag(this);
    if (viewBanner != null) viewBanner.stopAutoPlay();
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mLocationClient != null) mLocationClient.stop();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) return;
    switch (requestCode) {
      case REQUEST_CODE_PICK_CODE:
        city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
        tvCity.setText(city);
        break;
    }
  }

  private void initData() {
    httpCheckIfRead();
  }

  private void httpCheckIfRead() {
    OkGo.<JSONObject>get(Api.MOBILE_CLIENT_CHECK_NOTICE)
        .tag(this)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              String hasMsg = response.body().getJSONObject("data").getString("no_read");
              if (hasMsg.equals("1")) {//有未读消息
               if (ivMsg != null)ivMsg.setVisibility(View.VISIBLE);
              } else {
                if (ivMsg != null) ivMsg.setVisibility(View.GONE);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  private void httpGetData(String city) {
    OkGo.<JSONObject>post(Api.MOBILE_BUSINESS_INDEX).tag(mContext)
        .params("city_name", city)
        .params("ad_position_id", 1)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              JSONObject object = response.body().getJSONObject("data");
              int is_auth = object.getInt("is_auth");
              User user = DataSupport.findFirst(User.class);
              user.is_auth = is_auth;
              user.save();
              int orderNum = object.getInt("total_order");
             if (tvRubNum != null) tvRubNum.setText("今日可抢" + orderNum + "单");
              if (isFirstIn) {
                isFirstIn = false;
                JSONArray banner = object.getJSONArray("banner");
                List<String> bannerList = new ArrayList<>();
                for (int i = 0; i < banner.length(); i++) {
                  String url = banner.getString(i);
                  bannerList.add(url);
                }
                initBanner(bannerList);

                switch (banner.length()) {
                  case 0:
                  case 1:
                   if (ivDot1 != null)ivDot1.setVisibility(View.GONE);
                    if (ivDot2 != null)ivDot2.setVisibility(View.GONE);
                    if (ivDot3 != null)ivDot3.setVisibility(View.GONE);
                    break;
                  case 2:
                    if (ivDot3 != null)ivDot3.setVisibility(View.GONE);
                    break;
                }
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }


  private void initLocation() {
    String cityName = SPUtils.getInstance().getString(Constant.KEY_LOC_CITY);
    if (TextUtils.isEmpty(cityName)) {
      tvCity.setClickable(false);
      tvCity.setText("定位中...");
      mLocationClient = new LocationClient(BaseApplication.mInstance);
      LocationClientOption option = new LocationClientOption();
      option.setOpenGps(true);
      option.setIsNeedAddress(true);
      mLocationClient.setLocOption(option);
      mLocationClient.registerLocationListener(mLocListener);
      mLocationClient.start();
    } else {
      city = cityName;
      httpGetData(cityName);
    }
  }

  private void initBanner(List<String> bannerList) {
    if (viewBanner == null) return;
    viewBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
    viewBanner.setImageLoader(new MHomeBannerImageLoader());
    viewBanner.isAutoPlay(true);
    viewBanner.setDelayTime(1500);
    viewBanner.setIndicatorGravity(BannerConfig.CENTER);
    viewBanner.setImages(bannerList);
    viewBanner.start();
    viewBanner.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
        resetAllDotIndicator();
        switch (position) {
          case 0:
            ivDot1.setImageResource(R.drawable.dot_indicator_sel);
            break;
          case 1:
            ivDot2.setImageResource(R.drawable.dot_indicator_sel);
            break;
          case 2:
            ivDot3.setImageResource(R.drawable.dot_indicator_sel);
            break;
        }
      }
    });
  }

  private void resetAllDotIndicator() {
    ivDot1.setImageResource(R.drawable.dot_indicator_unsel);
    ivDot2.setImageResource(R.drawable.dot_indicator_unsel);
    ivDot3.setImageResource(R.drawable.dot_indicator_unsel);
  }

  @OnClick(R.id.layoutMsg) public void onIvMsgClicked() {
    ActivityUtils.startActivity(MMsgActivity.class);
  }

  @OnClick(R.id.tvScore) public void onTvScoreClicked() {
    ActivityUtils.startActivity(CScoreActivity.class);
  }

  @OnClick(R.id.tvRubOrder) public void onTvRubOrderClicked() {
    EventBus.getDefault().post(new Order(RUB_ORDER));
  }

  @OnClick(R.id.layoutRubOrder) public void onLayoutRubOrderClicked() {
    EventBus.getDefault().post(new Order(RUB_ORDER));
  }

  @OnClick(R.id.tvThrowOrder) public void onTvThrowOrderClicked() {
    User user = DataSupport.findFirst(User.class);
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
      EventBus.getDefault().post(new Order(THROW_ORDER));
    }
  }

  @OnClick(R.id.tvWallet) public void onTvWalletClicked() {
    ActivityUtils.startActivity(MWalletActivity.class);
  }

  @OnClick(R.id.tvNews) public void onTvNewsClicked() {
    ActivityUtils.startActivity(MNewsActivity.class);
  }

  @OnClick(R.id.tvSmallCard) public void onTvSmallCardClicked() {
    OkGo.<JSONObject>post(Api.MOBILE_BUSINESS_INDEX).tag(mContext)
        .params("city_name", city)
        .params("ad_position_id", 1)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              JSONObject object = response.body().getJSONObject("data");
              int is_shop = object.getInt("is_shop");
              if (is_shop == 1) {
                int loaner_id = object.getInt("loaner_id");
                Intent intent = new Intent(mContext, MNameCardActivity.class);
                intent.putExtra(MNameCardActivity.KEY_LOANER_ID, loaner_id);
                ActivityUtils.startActivity(intent);
              } else {
                ToastUtils.showShort("请先创建店铺");
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  @OnClick(R.id.tvCalculator) public void onTvCalculatorClicked() {
    ActivityUtils.startActivity(CalculatorActivity.class);
  }

  private BDLocationListener mLocListener = new BDLocationListener() {
    @Override public void onReceiveLocation(BDLocation location) {
      if (location == null) {
        return;
      }
      if (location.getLocType() == 61 || location.getLocType() == 161) {
        city = location.getCity();
        tvCity.setText(city);
        mCurrentLat = (float) location.getLatitude();
        mCurrentLon = (float) location.getLongitude();
        SPUtils.getInstance().put(Constant.KEY_M_LAT,mCurrentLat+"");
        SPUtils.getInstance().put(Constant.KEY_M_LNG,mCurrentLon+"");
        httpGetData(city);
        EventBus.getDefault().post(new JsonBean.CityBean(city));
      } else {
        tvCity.setText(getString(R.string.locate_fail));
      }
      tvCity.setClickable(true);
    }
  };
}
