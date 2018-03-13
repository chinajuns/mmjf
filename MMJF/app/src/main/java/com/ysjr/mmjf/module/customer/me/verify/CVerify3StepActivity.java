package com.ysjr.mmjf.module.customer.me.verify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.AuthResultBean;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.utils.Api;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-18.
 */

public class CVerify3StepActivity extends TopBarBaseActivity {
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.tvCity) TextView tvCity;
  @BindView(R.id.tvCardNumber) TextView tvCardNumber;
  @BindView(R.id.ivFront) ImageView ivFront;
  @BindView(R.id.ivBack) ImageView ivBack;
  @BindView(R.id.imgAuditState) ImageView imgAuditState;
  @BindView(R.id.tvAuditDesc) TextView tvAuditDesc;
  @BindView(R.id.btnAuditAgain) Button btnAuditAgain;
  @BindView(R.id.layoutMask) FrameLayout layoutMask;
  private AuthResultBean bean;

  @Override protected int getContentView() {
    return R.layout.c_activity_verify_3;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.verify_name));
    setTopLeftButton(R.drawable.btn_black_back, new OnClickListener() {
      @Override public void onClick() {
        ActivityUtils.finishToActivity(MainActivity.class, false);
      }
    });
    initData();
  }

  @OnClick(R.id.btnAuditAgain) public void onBtnAuditAgainClicked() {
    verifyAgain();
  }


  private void updateUser() {
    User user = DataSupport.findFirst(User.class);
    user.is_auth = bean.is_pass;
    user.save();
  }

  private void initData() {
    httpGetAuthInfo();
  }

  private void httpGetAuthInfo() {
    OkGo.<HttpResult<AuthResultBean>>get(Api.MOBILE_CLIENT_MEMBER_AUTH_DOCUMENT).tag(this)
        .execute(new JsonCallback<HttpResult<AuthResultBean>>() {
          @Override public void onSuccess(Response<HttpResult<AuthResultBean>> response) {
            bean = response.body().data;
            //更新本地数据库User
            updateUser();
            tvName.setText(bean.true_name);
            tvCardNumber.setText(bean.identity_number);
            tvCity.setText(bean.address);
            String frontUrl = Api.IMAGE_ROOT_URL + bean.front_identity;
            Glide.with(mContext).load(frontUrl).into(ivFront);
            String backUrl = Api.IMAGE_ROOT_URL + bean.back_identity;
            Glide.with(mContext).load(backUrl).into(ivBack);
            switch (bean.is_pass) {
              case 2://审核中
                layoutMask.setVisibility(View.VISIBLE);
                tvAuditDesc.setText(R.string.verifying);
                btnAuditAgain.setVisibility(View.GONE);
                imgAuditState.setImageResource(R.drawable.ic_auditing);
                break;
              case 3://审核通过
                break;
              case 4://审核拒绝
                layoutMask.setVisibility(View.VISIBLE);
                tvAuditDesc.setText(R.string.verify_fail);
                btnAuditAgain.setVisibility(View.VISIBLE);
                imgAuditState.setImageResource(R.drawable.ic_audit_fail);
                break;
            }
          }
        });
  }

  private void verifyAgain() {
      Intent intent = new Intent(mContext, CVerify1StepActivity.class);
      ActivityUtils.startActivity(intent);
      finish();
  }

  @Override public void onBackPressed() {
    ActivityUtils.finishToActivity(MainActivity.class, false);
  }
}
