package com.ysjr.mmjf.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.request.PostRequest;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.Token;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.GsonConvert;
import com.ysjr.mmjf.utils.Constant;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Response;
import org.litepal.crud.DataSupport;

public abstract class BaseActivity extends AppCompatActivity {
  private static final String BASE_TOKEN_URL = "http://api.kuanjiedai.com/api/v1/token";
  protected BaseActivity mContext;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
      finish();
      return;
    }
    setUIBeforeSetContentView();
    mContext = this;
    setContentView(getLayoutId());
    initialize(savedInstanceState);
  }
  @Override protected void onDestroy() {
    super.onDestroy();
    OkGo.getInstance().cancelTag(this);
  }
  protected void setUIBeforeSetContentView() {

  }
  public abstract int getLayoutId();
  public abstract void initialize(Bundle savedInstanceState);
  /**
   * 是否第一次安装应用
   */
  protected boolean checkIfFirstInstall() {
    if (SPUtils.getInstance().getBoolean(Constant.KEY_IF_INSTALLED_SP, false)) {
      return false;
    } else {
      SPUtils.getInstance().put(Constant.KEY_IF_INSTALLED_SP, true);
      return true;
    }
  }
  /**
   * 根据uid判断用户是否登录
   * caution:
   * 1、退出登录清空Sp中的uid
   * 2、登录成功更新Sp中的uid
   */
  public boolean isLogin() {
    User user = DataSupport.findFirst(User.class);
    if (user != null) {
      return true;
    }
    return false;
  }
  /**
   * 同步请求，需要在子线程执行
   */
  public static boolean isRefreshTokenSuccess() {
    String imei = SPUtils.getInstance().getString(Constant.KEY_IMEI_SP);
    if (TextUtils.isEmpty(imei)) {
      return false;
    }
    try {
      Response response = OkGo.post(BASE_TOKEN_URL)
          .params("platform", "android")
          .params("sys_version", Build.VERSION.RELEASE)
          .params("app_version", AppUtils.getAppVersionName())
          //由于用户可以在授权后关闭授权，所以在第一次安装时授权后将手机IMEI号存入Sp
          .params("deviceid", imei)
          .execute();

      String result = response.body().string();

      if (TextUtils.isEmpty(result)) return false;
      //            解析实体
      HttpResult<Token> tokenResult =
          GsonConvert.fromJson(result, new TypeToken<HttpResult<Token>>() {
          }.getType());
      Log.e("Token", tokenResult + "");
      //            判断token是否为空
      if (tokenResult == null || TextUtils.isEmpty(tokenResult.data.token)) {
        return false;
      }
      Log.e("Token", tokenResult.data.toString());
      //            获取token成功后,添加公共请求头

      SPUtils.getInstance().put(Constant.KEY_TOKEN_SP, tokenResult.data.token);
      //            设置所有请求接口值
      SPUtils.getInstance().put(Constant.ROOT_API_KEY, tokenResult.data.api_url);
      SPUtils.getInstance().put(Constant.ROOT_IMAGE_API_KEY, tokenResult.data.image_url);

      //         如果已经登录，清空数据库，因为登录失效
      if (TextUtils.isEmpty(tokenResult.data.uid)) {
        if (DataSupport.findFirst(User.class) != null) {
          DataSupport.deleteAll(User.class);
        }
      }
      //            获取token成功
      return true;
    } catch (JsonSyntaxException e) {
      if (!NetworkUtils.isConnected()) {
        ToastUtils.showShort("网络未连接");
      } else {
        ToastUtils.showShort("服务器打盹了,请重新进入");
      }
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

}
