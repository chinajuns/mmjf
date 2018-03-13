package com.ysjr.mmjf.module.login_module.psw;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.login_module.login.LoginActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.TextWatcherChecker;

/**
 * Created by Administrator on 2017-11-27.
 */

public class TakePsw2Activity extends TopBarBaseActivity {
  @BindView(R.id.textPsw) EditText textPsw;
  @BindView(R.id.textConfirmPsw) EditText textConfirmPsw;
  @BindView(R.id.btnConfirm) Button btnConfirm;
  private String phoneNumber;
  @Override protected int getContentView() {
    return R.layout.activity_take_psw_2;
  }

  @Override protected void init(Bundle savedInstanceState) {
    phoneNumber = getIntent().getStringExtra(TakePsw1Activity.KEY_PHONE_NUMBER);
    setTitle(getString(R.string.take_psw));
    setTopBarBackground(getResources().getDrawable(R.color.bg_white_color));
    setTopLeftButton(R.drawable.btn_black_back);
    new TextWatcherChecker(btnConfirm).setRelevantView(textPsw,textConfirmPsw);
  }

  @OnClick(R.id.btnConfirm) void confirm() {
    if (CommonUtils.checkPsw(textPsw, textConfirmPsw)) {
      OkGo.<HttpResult<Void>>post(Api.TAKE_PSW)
          .tag(this)
          .params("password",textPsw.getText().toString())
          .params("password_confirmation",textConfirmPsw.getText().toString())
          .params("mobile",phoneNumber)
          .execute(new DialogCallback<HttpResult<Void>>(mContext) {
            @Override public void onSuccess(Response<HttpResult<Void>> response) {
              ActivityUtils.finishToActivity(LoginActivity.class, false);
            }
          });
    }
  }
}
