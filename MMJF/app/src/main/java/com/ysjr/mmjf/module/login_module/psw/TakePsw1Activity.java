package com.ysjr.mmjf.module.login_module.psw;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.TextWatcherChecker;

/**
 * Created by Administrator on 2017-11-27.
 */

public class TakePsw1Activity extends TopBarBaseActivity {
  @BindView(R.id.textPhone) EditText textPhone;
  @BindView(R.id.textCode) EditText textCode;
  @BindView(R.id.btnSmsCode) TextView btnSmsCode;
  @BindView(R.id.btnNext) Button btnNext;
  private CountDownTimer mTimer;
  public static final String KEY_PHONE_NUMBER = "phone_number";
  @Override protected int getContentView() {
    return R.layout.activity_take_psw_1;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.take_psw));
    setTopBarBackground(getResources().getDrawable(R.color.bg_white_color));
    setTopLeftButton(R.drawable.btn_black_back);
    new TextWatcherChecker(btnNext).setRelevantView(textPhone, textCode);
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
          .params("type", Constant.TYPE_CODE_TAKE_PSW)
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
  @OnClick(R.id.btnNext) void next() {
    if (!CommonUtils.checkPhone(textPhone)) return;
    //验证验证码
    OkGo.<HttpResult<Void>>post(Api.CHECK_CODE_URL)
        .tag(this)
        .params("mobile",textPhone.getText().toString())
        .params("code",textCode.getText().toString())
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            Intent intent = new Intent(mContext, TakePsw2Activity.class);
            intent.putExtra(KEY_PHONE_NUMBER, textPhone.getText().toString());
            ActivityUtils.startActivity(intent);
          }
        });
  }

}
