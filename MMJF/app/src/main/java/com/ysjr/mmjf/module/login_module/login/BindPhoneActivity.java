package com.ysjr.mmjf.module.login_module.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.TextWatcherChecker;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018-1-24.
 */

public class BindPhoneActivity extends TopBarBaseActivity {
  public static final String KEY_UID = "uid";
  public static final String KEY_HEADER_IMG = "header_img";
  @BindView(R.id.textPhone) EditText textPhone;
  @BindView(R.id.textCode) EditText textCode;
  @BindView(R.id.btnSmsCode) TextView btnSmsCode;
  @BindView(R.id.btnCommit) Button btnCommit;
  private CountDownTimer mTimer;
  private String uid;
  private String header_img;
  public static String KEY_LOGIN_TYPE = "type_login";
  private String mTypeLogin;
  @Override protected int getContentView() {
    return R.layout.activity_bind_phone;
  }

  @Override protected void init(Bundle savedInstanceState) {
    uid = getIntent().getStringExtra(KEY_UID);
    header_img = getIntent().getStringExtra(KEY_HEADER_IMG);
    mTypeLogin = getIntent().getStringExtra(KEY_LOGIN_TYPE);
    setTitle(getString(R.string.bind_phone));
    setTopBarBackground(getResources().getDrawable(R.color.bg_white_color));
    setTopLeftButton(R.drawable.btn_black_back);
    new TextWatcherChecker(btnCommit).setRelevantView(textPhone, textCode);
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
  @OnClick(R.id.btnSmsCode) void getCode() {
    if (CommonUtils.checkPhone(textPhone)) {
      if (mTimer != null) mTimer.start();
      OkGo.<HttpResult<Void>>post(Api.GET_CODE_URL).params("mobile", textPhone.getText().toString())
          .tag(this)
          .params("type", Constant.TYPE_CODE_OAUTH_BIND)
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
  @OnClick(R.id.btnCommit) void commit() {
    if (!CommonUtils.checkPhone(textPhone)) return;
    //验证验证码
    OkGo.<HttpResult<Void>>post(Api.CHECK_CODE_URL)
        .tag(this)
        .params("mobile",textPhone.getText().toString())
        .params("code",textCode.getText().toString())
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            OkGo.<HttpResult<User>>post(Api.SET_AUTH_BIND)
                .tag(mContext)
                //写死的微信
                .params("type",Constant.TYPE_3_BIND_PLATFORM_WEIXIN)
                .params("unionid",uid)
                .params("header_img", TextUtils.isEmpty(header_img)?"":header_img)
                .params("mobile",textPhone.getText().toString())
                .params("user_type",mTypeLogin)
                .params("platform",Constant.PLATFORM)
                .execute(new DialogCallback<HttpResult<User>>(mContext) {
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
        });
  }
}
