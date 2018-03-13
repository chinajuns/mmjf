package com.ysjr.mmjf.module.launcher;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.base.BaseApplication;
import com.ysjr.mmjf.entity.JsonBean;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.widget.loading.KProgressHUD;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions public class WelcomeActivity extends BaseActivity {
  private Handler mHandler = new Handler();
  private boolean isFirstIn = true;
  private LocationClient mLocationClient;
  @Override protected void onResume() {
    super.onResume();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      WelcomeActivityPermissionsDispatcher.delayStartWithPermissionCheck(this);
    } else {
      delayStart();
    }
  }

  @Override public int getLayoutId() {
    return R.layout.activity_welcome;
  }

  @Override protected void setUIBeforeSetContentView() {
    //        设置全屏
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  @Override public void initialize(Bundle savedInstanceState) {

  }

  @NeedsPermission({
      Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  }) protected void delayStart() {
    //此方法会在权限同意后调用两次，让其只执行一次获取token
    if (!isFirstIn) {
      return;
    }
    isFirstIn = false;
    if (checkIfFirstInstall()) {
      SPUtils.getInstance().put(Constant.KEY_IMEI_SP, PhoneUtils.getIMEI());
    }
    initLocation();
  }

  @OnPermissionDenied({
      Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  }) void exitApp() {
    finish();
    Process.killProcess(Process.myPid());
    System.exit(1);
  }
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    WelcomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
        grantResults);
  }

  private void initLocation() {
    mLocationClient = new LocationClient(BaseApplication.mInstance);
    LocationClientOption option = new LocationClientOption();
    option.setOpenGps(true);
    option.setIsNeedAddress(true);
    mLocationClient.setLocOption(option);
    mLocationClient.registerLocationListener(mLocListener);
    mLocationClient.start();
  }

  private BDLocationListener mLocListener = new BDLocationListener() {
    @Override public void onReceiveLocation(BDLocation location) {
      if (location == null) {
        return;
      }
      if (location.getLocType() == 61 || location.getLocType() == 161) {
        String city = location.getCity();
        float mCurrentLat = (float) location.getLatitude();
        float mCurrentLon = (float) location.getLongitude();
        SPUtils.getInstance().put(Constant.KEY_M_LAT,mCurrentLat+"");
        SPUtils.getInstance().put(Constant.KEY_M_LNG,mCurrentLon+"");
        SPUtils.getInstance().put(Constant.KEY_LOC_CITY,city);
      }
      initToken();
    }
  };


  private void initToken() {
    //请求token
    new Thread(new Runnable() {
      @Override public void run() {
        if (BaseActivity.isRefreshTokenSuccess()) {
          String rootApi = SPUtils.getInstance().getString(Constant.ROOT_API_KEY);
          String rootImageApi = SPUtils.getInstance().getString(Constant.ROOT_IMAGE_API_KEY);
          Log.e("SP", "rootApi ="+rootApi);
          Log.e("SP", "imageApi ="+rootImageApi);
          if (!TextUtils.isEmpty(rootApi)) {
            Api.setApi(rootApi + "api/v1/");
          }
          if (!TextUtils.isEmpty(rootImageApi)) {
            Api.setImageApi(rootImageApi);
          }

          HttpHeaders headers = new HttpHeaders();
          String token = SPUtils.getInstance().getString(Constant.KEY_TOKEN_SP);
          String imei = SPUtils.getInstance().getString(Constant.KEY_IMEI_SP);
          headers.put("token", token);
          headers.put("deviceid",imei );
          BaseApplication.token = token;
          BaseApplication.deviceid = imei;
          OkGo.getInstance().addCommonHeaders(headers);
          mHandler.postDelayed(new Runnable() {
            @Override public void run() {
              if (isLogin()) {
                User user = DataSupport.findFirst(User.class);
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MainActivity.IS_CUSTOMER_LOGIN_KEY, user.type == 1);
                ActivityUtils.startActivity(intent);
              } else {
                ActivityUtils.startActivity(MainActivity.class);
              }
              finish();
            }
          }, 1000);
        } else {
          if (!NetworkUtils.isConnected()) {
            mHandler.post(new Runnable() {
              @Override public void run() {
                ToastUtils.showLong("网络连接已断开");
              }
            });
          }
        }
      }
    }).start();
  }

}
