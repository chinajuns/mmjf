package com.ysjr.mmjf.module.login_module.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.module.login_module.code_login.CodeLoginActivity;
import com.ysjr.mmjf.module.login_module.psw.TakePsw1Activity;
import com.ysjr.mmjf.module.login_module.register.RegisterActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.HttpError;
import com.ysjr.mmjf.utils.TextWatcherChecker;
import com.ysjr.mmjf.widget.loading.KProgressHUD;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

public class LoginActivity extends BaseActivity {
  private static final String TAG = LoginActivity.class.getSimpleName();
  public static String KEY_LOGIN_TYPE = "type_login";
  private String mTypeLogin;
  @BindView(R.id.imgAvatar) ImageView imgAvatar;
  @BindView(R.id.textLoginPhone) MaterialEditText textPhone;
  @BindView(R.id.textPsw) MaterialEditText textPsw;
  @BindView(R.id.btnSwitchRegister) Button btnSwitchRegister;
  @BindView(R.id.btnLogin) Button btnLogin;
  private KProgressHUD hud;
  @Override protected void setUIBeforeSetContentView() {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  @Override public int getLayoutId() {
    return R.layout.activity_login;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    initDialog();
    mTypeLogin = getIntent().getStringExtra(KEY_LOGIN_TYPE);
    //监听登录按钮是否可点击
    new TextWatcherChecker(btnLogin).setRelevantView(textPhone,textPsw);
    textPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
          if (textPhone != null && CommonUtils.checkPhone(textPhone)) {
            String avatarPath = SPUtils.getInstance().getString(textPhone.getText().toString());
            if (!TextUtils.isEmpty(avatarPath)) {
              Glide.with(mContext).load(avatarPath).into(imgAvatar);
            }
          }
        }
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();
    if (hud != null) hud.dismiss();
  }

  private void initDialog() {
    hud = KProgressHUD.create(mContext)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel("登录中...")
        .setDimAccount(0.6f)
        .setSize(SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3),
            SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3))
        .setCornerRadius(30)
        .setCancelable(false)
        .setGraceTime(500);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
  }

  @OnClick(R.id.imgBack) void back() {
    finish();
  }
  @OnClick(R.id.btnForgetPsw) void startForgetPsw() {
    ActivityUtils.startActivity(TakePsw1Activity.class);
  }
  @OnClick(R.id.btnSwitchRegister) void switchRegister() {
    Bundle bundle = new Bundle();
    bundle.putString(KEY_LOGIN_TYPE,mTypeLogin);
    ActivityUtils.startActivity(bundle,RegisterActivity.class);
  }
  @OnClick(R.id.btnLogin) void login() {
    if (!CommonUtils.checkPhone(textPhone) ||
        !CommonUtils.checkPsw(textPsw, textPsw)) {
      return;
    }
    OkGo.<HttpResult<User>>post(Api.LOGIN_URL)
        .tag(this)
        .params("mobile",textPhone.getText().toString())
        .params("password",textPsw.getText().toString())
        .params("platform",Constant.PLATFORM)
        .params("type",mTypeLogin)
        .execute(new DialogCallback<HttpResult<User>>(mContext,"登录中...") {
          @Override public void onSuccess(Response<HttpResult<User>> response) {
            updateUserInfo(response);
          }
        });
  }

  @OnClick(R.id.imgSms) void smsLogin() {
    Intent intent = new Intent(mContext, CodeLoginActivity.class);
    intent.putExtra(LoginActivity.KEY_LOGIN_TYPE, mTypeLogin);
    ActivityUtils.startActivity(intent);
  }
  @OnClick(R.id.imgWeixin) void weixinLogin(){
    UMShareAPI.get(this).getPlatformInfo(mContext,SHARE_MEDIA.WEIXIN,umAuthListener);
  }
  private UMAuthListener umAuthListener = new UMAuthListener() {
    @Override public void onStart(SHARE_MEDIA media) {
      hud.show();
    }

    @Override public void onComplete(SHARE_MEDIA media, int i, Map<String, String> map) {
      final String uid = map.get("uid");
      final String iconUrl = map.get("iconurl");
      OkGo.<HttpResult<User>>post(Api.SET_AUTH)
          .tag(mContext)
          .params("type","wechat")
          .params("unionid",uid)
          .params("user_type",mTypeLogin)
          .execute(new JsonCallback<HttpResult<User>>() {
            @Override public void onSuccess(Response<HttpResult<User>> response) {
                updateUserInfo(response);
            }

            @Override public void onError(Response<HttpResult<User>> response) {
              super.onError(response);
              if (hud != null) hud.dismiss();
              Throwable exception = response.getException();
              String erroStatus = null;
              if (exception != null) {
                erroStatus = exception.getMessage();
              }
              if (HttpError.EMPTY.equals(erroStatus)) {//未绑定，需要填手机号
                Intent intent = new Intent(mContext, BindPhoneActivity.class);
                intent.putExtra(KEY_LOGIN_TYPE, mTypeLogin);
                intent.putExtra(BindPhoneActivity.KEY_UID, uid);
                if (!TextUtils.isEmpty(iconUrl)) {
                  intent.putExtra(BindPhoneActivity.KEY_HEADER_IMG, iconUrl);
                }
                ActivityUtils.startActivity(intent);
              }
            }
          });
    }

    @Override public void onError(SHARE_MEDIA media, int i, Throwable throwable) {
      if (hud != null) hud.dismiss();
      ToastUtils.showShort("登录失败");
    }

    @Override public void onCancel(SHARE_MEDIA media, int i) {
      if (hud != null) hud.dismiss();
      ToastUtils.showShort("登录取消");
    }

  };
  private void updateUserInfo(Response<HttpResult<User>> response) {
    User user = DataSupport.findFirst(User.class);
    if (user != null) {
      user.delete();
    }
    user = response.body().data;
    user.save();
    EventBus.getDefault().post(user);
    Intent intent = new Intent(mContext, MainActivity.class);
    if (mTypeLogin.equals(Constant.TYPE_ROLE_CUSTOMER)) {
      intent.putExtra(MainActivity.IS_CUSTOMER_LOGIN_KEY, true);
      if (ActivityUtils.isActivityExistsInStack(MainActivity.class)) {
        ActivityUtils.finishToActivity(NavigationActivity.class, true);
      } else {
        ActivityUtils.startActivity(intent);
      }
    } else {
      intent.putExtra(MainActivity.IS_CUSTOMER_LOGIN_KEY, false);
      ActivityUtils.startActivity(intent);
    }
  }
}
