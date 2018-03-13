package com.ysjr.mmjf.module.customer.me.set;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import com.ysjr.mmjf.utils.TextWatcherChecker;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CModifyPswActivity extends TopBarBaseActivity {
  @BindView(R.id.etOldPsw) EditText etOldPsw;
  @BindView(R.id.etNewPsw) EditText etNewPsw;
  @BindView(R.id.etConfirmPsw) EditText etConfirmPsw;
  @BindView(R.id.btnConfirm) Button btnConfirm;
  private CModifyPswDialogFragment mDialogFragment;
  @Override protected int getContentView() {
    return R.layout.c_activity_modify_psw;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.modify_psw));
    new TextWatcherChecker(btnConfirm).setRelevantView(etOldPsw,etNewPsw,etConfirmPsw);
  }

  @OnClick(R.id.btnConfirm) public void onViewClicked() {
    String oldPsw = etOldPsw.getText().toString();
    String newPsw = etNewPsw.getText().toString();
    String confirmPsw = etConfirmPsw.getText().toString();
    if (oldPsw.length() < 6) {
      ToastUtils.showShort("旧密码错误");
      return;
    }
    if (newPsw.length() < 6) {
      ToastUtils.showShort("新密码长度小于6");
      return;
    }
    if (!newPsw.equals(confirmPsw)) {
      ToastUtils.showShort("密码不一致");
      return;
    }
    OkGo.<HttpResult<Void>>post(Api.RESET_PSW)
        .tag(this)
        .params("old_password",etOldPsw.getText().toString())
        .params("password",etNewPsw.getText().toString())
        .params("password_confirmation",etConfirmPsw.getText().toString())
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            boolean isCustomer = false;
            User user = DataSupport.findFirst(User.class);
            if (user != null && user.type == 1) {
              isCustomer = true;
            }
            mDialogFragment = CModifyPswDialogFragment.newInstance(isCustomer);
            mDialogFragment.setCancelable(false);
            mDialogFragment.show(getSupportFragmentManager(),"dialog");
          }

          @Override public void onError(Response<HttpResult<Void>> response) {
            super.onError(response);
            String errorStatus = null;
            if (response.getException() != null) {
              errorStatus = response.getException().getMessage();
            }
            if (HttpError.PARAMS_ERROR.equals(errorStatus)) {
              ToastUtils.showShort("旧密码错误");
            }
          }
        });
  }
}
