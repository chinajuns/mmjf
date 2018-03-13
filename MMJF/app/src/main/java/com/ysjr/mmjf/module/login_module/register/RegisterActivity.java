package com.ysjr.mmjf.module.login_module.register;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.ImageBean;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
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
import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017-11-22.
 */

public class RegisterActivity extends BaseActivity {
  private static final int REQUEST_CODE_CHOOSE = 1;
  @BindView(R.id.btnRegister) Button btnRegister;
  @BindView(R.id.btnSwitchLogin) Button btnSwitchLogin;
  @BindView(R.id.btnSmsCode) TextView btnSmsCode;
  @BindView(R.id.textPhone) EditText textPhone;
  @BindView(R.id.textCode) EditText textCode;
  @BindView(R.id.textPsw) MaterialEditText textPsw;
  @BindView(R.id.textConfirmPsw) MaterialEditText textConfirmPsw;
  @BindView(R.id.chxProtocol) CheckBox chxProtocol;
  @BindView(R.id.textProtocol) TextView textProtocol;
  @BindView(R.id.imgAvatar) CircleImageView imgAvatar;
  private CountDownTimer mTimer;
  private TextWatcherChecker mTextWatcherChecker;
  private String mAvatarPath;
  private String mTypeRegister;

  @Override protected void setUIBeforeSetContentView() {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  @Override public int getLayoutId() {
    return R.layout.activity_register;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    if (getIntent().getExtras() != null) {
      mTypeRegister = getIntent().getExtras().getString(LoginActivity.KEY_LOGIN_TYPE);
    }
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
    //  设置协议显示和点击事件
    setProtocolSpan();
    //  设置注册按钮是否可点击监听
    mTextWatcherChecker = new TextWatcherChecker(btnRegister);
    mTextWatcherChecker.setRelevantView(textPhone, textCode, textPsw, textConfirmPsw);
  }

  private void setProtocolSpan() {
    CommonUtils.setProtocolSpan(textProtocol,R.string.protocol, ProtocolActivity.class);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //取消消息发送
    mTimer.cancel();
  }

  @OnClick(R.id.imgBack) void back() {
    finish();
  }

  @OnClick(R.id.btnSwitchLogin) void switchLogin() {
    finish();
  }

  @OnClick(R.id.btnSmsCode) void getCode() {
    //检查电话号码
    if (!CommonUtils.checkPhone(textPhone)) return;
    mTimer.start();
    Log.e("Url", Api.GET_CODE_URL);
    OkGo.<HttpResult<Void>>post(Api.GET_CODE_URL).params("mobile", textPhone.getText().toString())
        .params("type", Constant.TYPE_CODE_REGISTER)
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

  @OnClick(R.id.btnRegister) void register() {
    //  检查手机号、密码、协议是否勾选
    if (!CommonUtils.checkPhone(textPhone)
        || !CommonUtils.checkPsw(textPsw, textConfirmPsw)
        || !CommonUtils.checkProtocol(chxProtocol)
        || !CommonUtils.checkNotNull(textCode, true)) {
      return;
    }
    OkGo.<HttpResult<Void>>post(Api.CHECK_CODE_URL)
        .tag(this)
        .params("mobile",textPhone.getText().toString())
        .params("code",textCode.getText().toString())
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {

            if (!TextUtils.isEmpty(mAvatarPath)) {
              File file = new File(mAvatarPath);
              OkGo.<HttpResult<ImageBean>>post(Api.UPLOAD_IMAGE_URL)
                  .tag(this)
                  .params("image", file)
                  .execute(new JsonCallback<HttpResult<ImageBean>>() {
                    @Override public void onSuccess(Response<HttpResult<ImageBean>> response) {
                      //上传成功获取到图片url再注册
                      String url = response.body().data.src;
                      OkGo.<HttpResult<Void>>post(Api.REGISTER_URL).params("mobile",
                          textPhone.getText().toString())
                          .params("password", textPsw.getText().toString())
                          .params("platform", Constant.PLATFORM)
                          .params("type", mTypeRegister)
                          .params("url",url)
                          .execute(new DialogCallback<HttpResult<Void>>(mContext) {
                            @Override public void onSuccess(Response<HttpResult<Void>> response) {
                              //注册成功后才设置
                              SPUtils.getInstance().put(textPhone.getText().toString(), mAvatarPath);
                              //返回登录页面
                              finish();
                            }
                          });
                    }
                  });
            } else {
              //未设置头像，直接注册，头像url未null
              OkGo.<HttpResult<Void>>post(Api.REGISTER_URL).params("mobile",
                  textPhone.getText().toString())
                  .params("password", textPsw.getText().toString())
                  .params("platform", Constant.PLATFORM)
                  .params("type", mTypeRegister)
                  .execute(new DialogCallback<HttpResult<Void>>(mContext) {
                    @Override public void onSuccess(Response<HttpResult<Void>> response) {
                      //返回登录页面
                      finish();
                    }
                  });
            }

          }
        });
  }

  @OnClick(R.id.imgAvatar) void selectAvatar() {
    Matisse.from(mContext)
        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_CHOOSE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
      List<String> result = Matisse.obtainPathResult(data);
      if (result != null && result.size() > 0) {
        mAvatarPath = result.get(0);
        Glide.with(mContext).load(mAvatarPath).into(imgAvatar);
      }
    }
  }

}
