package com.ysjr.mmjf.module.customer.me.set;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.utils.Api;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CFeedbackActivity extends TopBarBaseActivity {
  @BindView(R.id.etContent) EditText etContent;
  @BindView(R.id.tvNum) TextView tvNum;
  @BindView(R.id.btnCommit) Button btnCommit;

  @Override protected int getContentView() {
    return R.layout.c_activity_feedback;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.feed_back));
    etContent.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        int num = s.toString().trim().length();
        tvNum.setText(num+"/100");
        if (num >= 10) {
          btnCommit.setEnabled(true);
        } else {
          btnCommit.setEnabled(false);
        }
      }
    });
  }

  @OnClick(R.id.btnCommit) public void onViewClicked() {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MEMBER_FEEDBACK)
        .tag(this)
        .params("comment",etContent.getText().toString())
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            ToastUtils.setBgColor(Color.BLACK);
            ToastUtils.showShort("反馈成功");
            finish();
          }
        });
  }
}
