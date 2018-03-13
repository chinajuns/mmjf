package com.ysjr.mmjf.module.customer.me.bind;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.OauthBindBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.dialogfragment.SureOrCancelFragment;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.HttpError;
import com.ysjr.mmjf.widget.loading.KProgressHUD;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-6.
 */

public class Bind3PlatActivity extends TopBarBaseActivity {
  private static final String TAG = Bind3PlatActivity.class.getSimpleName();
  @BindView(R.id.tvState) TextView tvState;
  private SureOrCancelFragment mDialogFragment;
  private boolean isWxBind;
  private int wxId;
  private KProgressHUD hud;
  @Override protected int getContentView() {
    return R.layout.c_activity_bind_3_plat;
  }

  @Override protected void init(Bundle savedInstanceState) {
    initDialog();
    setTitle(getString(R.string.bind_account));
    EventBus.getDefault().register(this);
    httpGetBindInfo();
  }
  private void initDialog() {
    hud = KProgressHUD.create(mContext)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setDimAccount(0.6f)
        .setSize(SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3),
            SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3))
        .setCornerRadius(30)
        .setGraceTime(500)
    .setCancelable(false);
  }
  private void httpGetBindInfo() {
    OkGo.<HttpResult<List<OauthBindBean>>>get(Api.CLIENT_MEMBER_OAUTH_BIND)
        .tag(mContext)
        .execute(new JsonCallback<HttpResult<List<OauthBindBean>>>() {
          @Override public void onSuccess(Response<HttpResult<List<OauthBindBean>>> response) {
            List<OauthBindBean> list = response.body().data;
            for (OauthBindBean bean : list) {
              switch (bean.type) {
                case Constant.TYPE_3_BIND_PLATFORM_WEIXIN:
                  wxId = bean.id;
                  if (bean.status == 1) {
                    isWxBind = true;
                    tvState.setText(R.string.has_bind);
                  } else {
                    isWxBind = false;
                    tvState.setText(R.string.no_bind);
                  }
                  break;
              }
            }
          }

          @Override public void onError(Response<HttpResult<List<OauthBindBean>>> response) {
            super.onError(response);
            Throwable exception = response.getException();
            String errorStatus = null;
            if (exception != null) {
              errorStatus = exception.getMessage();
            }
            if (HttpError.EMPTY.equals(errorStatus)) {
              isWxBind = false;
              tvState.setText(R.string.no_bind);
            }
          }
        });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
  }

  @OnClick(R.id.layoutWx) public void onViewClicked() {
    if (!isWxBind) {
      UMShareAPI.get(this).getPlatformInfo(mContext,SHARE_MEDIA.WEIXIN,umAuthListener);
    } else {
      mDialogFragment = SureOrCancelFragment.newInstance(getString(R.string.sure_unbind_weixin));
      mDialogFragment.show(getSupportFragmentManager(), "dialog");
    }
  }
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(String s) {
    UMShareAPI.get(this).getPlatformInfo(mContext,SHARE_MEDIA.WEIXIN,umAuthListener);
  }

  /**
   * key= unionid and value= owXtgw5g9BuS7kX2G4s9cMYKE3TY
   key= screen_name and value= Snow
   key= city and value=
   key= accessToken and value= 6_14s44QR6MYfwc0BgR3w-qfoDEW7lI4VRAI3FbuoQrGhtbBFykDVYLCn56IMmJlqYhvXlh0pCAzoARW2lpQqpZK8ZNVfzoPkchhQGTO1hoAI
   key= refreshToken and value= 6_Ymg44N0Vk9qFdbEGmxhYPxpip0bHNDkReK-HDxKXHuxrgzgrUpMbjVrPJOW-VjK4GSw67fpiIL7moosvt2D1Ai8TUzWJJc82ATYXVMBrht8
   key= gender and value= 男
   key= province and value= 重庆
   key= openid and value= o3xpwwxKyBr6jj6DXSQAEuK6SvMs
   key= profile_image_url and value= http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJjGOJZDVZKOho4S1eAXdCQp4DypVawntRUicxDXGOC6MNpLYQcPjSE9lwKdm1UJSUNib6rVelZGLeA/132
   key= country and value= 中国
   key= access_token and value= 6_14s44QR6MYfwc0BgR3w-qfoDEW7lI4VRAI3FbuoQrGhtbBFykDVYLCn56IMmJlqYhvXlh0pCAzoARW2lpQqpZK8ZNVfzoPkchhQGTO1hoAI
   key= iconurl and value= http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJjGOJZDVZKOho4S1eAXdCQp4DypVawntRUicxDXGOC6MNpLYQcPjSE9lwKdm1UJSUNib6rVelZGLeA/132
   key= name and value= Snow
   key= uid and value= owXtgw5g9BuS7kX2G4s9cMYKE3TY
   key= expiration and value= 1516702472290
   key= language and value= zh_CN
   */
  private UMAuthListener umAuthListener = new UMAuthListener() {
    @Override public void onStart(SHARE_MEDIA media) {
      if (hud != null) hud.show();
    }

    @Override public void onComplete(SHARE_MEDIA media, int i, Map<String, String> map) {
      String uid = map.get("uid");
      OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MEMBER_SET_OAUTH_BIND)
          .tag(mContext)
          .params("type",Constant.TYPE_3_BIND_PLATFORM_WEIXIN)
          .params("unionid",uid)
          .params("action",isWxBind?"cancel":"add")
          .params("id",wxId)
          .execute(new JsonCallback<HttpResult<Void>>() {
            @Override public void onSuccess(Response<HttpResult<Void>> response) {
              if (hud != null) hud.dismiss();
              ToastUtils.showShort(!isWxBind?R.string.bind_success:R.string.unbind_success);
              httpGetBindInfo();
            }
            @Override public void onError(Response<HttpResult<Void>> response) {
              super.onError(response);
              if (hud != null) hud.dismiss();
            }
          });
    }

    @Override public void onError(SHARE_MEDIA media, int i, Throwable throwable) {
      if (hud != null) hud.dismiss();
      ToastUtils.showShort(!isWxBind?R.string.bind_fail:R.string.unbind_fail);
    }

    @Override public void onCancel(SHARE_MEDIA media, int i) {
      if (hud != null) hud.dismiss();
      ToastUtils.showShort(!isWxBind?R.string.bind_cacel:R.string.unbind_cancel);
    }
  };




}
