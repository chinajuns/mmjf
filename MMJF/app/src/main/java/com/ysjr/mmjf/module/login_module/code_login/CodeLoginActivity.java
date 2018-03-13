package com.ysjr.mmjf.module.login_module.code_login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.module.login_module.login.LoginActivity;
import com.ysjr.mmjf.module.login_module.protocol.ProtocolActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.TextWatcherChecker;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-11-27.
 */

public class CodeLoginActivity extends BaseActivity {
  @BindView(R.id.textPhone) EditText textPhone;
  @BindView(R.id.textCode) EditText textCode;
  @BindView(R.id.btnSmsCode) TextView btnSmsCode;
  @BindView(R.id.btnLogin) Button btnLogin;
  @BindView(R.id.chxProtocol) CheckBox chxProtocol;
  @BindView(R.id.textProtocol) TextView textProtocol;
  private CountDownTimer mTimer;
  private String mTypeLogin;

  @Override protected void setUIBeforeSetContentView() {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  @Override public int getLayoutId() {
    return R.layout.activity_code_login;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    mTypeLogin = getIntent().getStringExtra(LoginActivity.KEY_LOGIN_TYPE);
    ButterKnife.bind(this);
    new TextWatcherChecker(btnLogin).setRelevantView(textPhone, textCode);
    CommonUtils.setProtocolSpan(textProtocol, R.string.protocol, ProtocolActivity.class);
    //初始化验证码Timer
    mTimer = new CountDownTimer(Constant.CODE_TOTAL_TIME, Constant.CODE_INTERVAL_TIME) {
      @Override public void onTick(long millisUntilFinished) {
        btnSmsCode.setClickable(false);
        btnSmsCode.setTextColor(getResources().getColor(R.color.code_text_color));
        String codeDesc = "请输入验证码(" + millisUntilFinished / 1000 + "S)";
        btnSmsCode.setText(codeDesc);
      }

      @Override public void onFinish() {
        btnSmsCode.setTextColor(getResources().getColor(R.color.btn_enabled_color));
        btnSmsCode.setText(getResources().getString(R.string.sms_code));
        btnSmsCode.setClickable(true);
      }
    };
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mTimer != null) mTimer.cancel();
  }

  @OnClick(R.id.imgBack) void back() {
    finish();
  }

  @OnClick(R.id.btnSmsCode) void getCode() {
    if (CommonUtils.checkPhone(textPhone)) {
      mTimer.start();
      Log.e("Url", Api.GET_CODE_URL);
      OkGo.<HttpResult<Void>>post(Api.GET_CODE_URL).params("mobile", textPhone.getText().toString())
          .params("type", Constant.TYPE_CODE_CODE_LOGIN)
          .execute(new JsonCallback<HttpResult<Void>>() {
            @Override public void onSuccess(Response<HttpResult<Void>> response) {
              SnackbarUtils.with(btnSmsCode)
                  .setMessage(getResources().getString(R.string.code_has_send))
                  .setBgColor(getResources().getColor(R.color.btn_enabled_color))
                  .setMessageColor(getResources().getColor(R.color.bg_white_color))
                  .setDuration(SnackbarUtils.LENGTH_LONG)
                  .show();
            }
          });
    }
  }

  @OnClick(R.id.btnLogin) void login() {
    if (!CommonUtils.checkPhone(textPhone) || !CommonUtils.checkPsw(textCode, textCode)) {
      return;
    }
    OkGo.<HttpResult<User>>post(Api.LOGIN_URL).params("mobile", textPhone.getText().toString())
        .params("platform", Constant.PLATFORM)
        .params("type", mTypeLogin)
        .params("code", textCode.getText().toString())
        .params("login_way", Constant.TYPE_CODE_CODE_LOGIN)
        .execute(new DialogCallback<HttpResult<User>>(mContext, getString(R.string.logining)) {
          @Override public void onSuccess(Response<HttpResult<User>> response) {
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
        });
  }
}
