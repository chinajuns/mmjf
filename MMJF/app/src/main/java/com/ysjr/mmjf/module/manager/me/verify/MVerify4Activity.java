package com.ysjr.mmjf.module.manager.me.verify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import de.hdodenhof.circleimageview.CircleImageView;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MVerify4Activity extends TopBarBaseActivity {
  @BindView(R.id.ivAvatar) CircleImageView ivAvatar;
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.tvCardNumber) TextView tvCardNumber;
  @BindView(R.id.tvCity) TextView tvCity;
  @BindView(R.id.tvCompanyType) TextView tvCompanyType;
  @BindView(R.id.tvCompany) TextView tvCompany;
  @BindView(R.id.tvBelongDepartment) TextView tvBelongDepartment;
  @BindView(R.id.imgAuditState) ImageView imgAuditState;
  @BindView(R.id.tvAuditDesc) TextView tvAuditDesc;
  @BindView(R.id.btnAuditAgain) Button btnAuditAgain;
  @BindView(R.id.layoutMask) FrameLayout layoutMask;
  @BindView(R.id.ivFontCard) ImageView ivFontCard;
  @BindView(R.id.ivBackCard) ImageView ivBackCard;
  @BindView(R.id.ivGongpai) ImageView ivGongpai;
  @BindView(R.id.ivMingpian) ImageView ivMingpian;
  @BindView(R.id.ivHetong) ImageView ivHetong;
  @BindView(R.id.ivLogo) ImageView ivLogo;

  @Override protected int getContentView() {
    return R.layout.m_activity_verify_4;
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

  private void initData() {
    httpGetAuthResult();
  }

  private void httpGetAuthResult() {
    OkGo.<HttpResult<AuthResultBean>>get(Api.MOBILE_BUSINESS_MANAGER_PROFILE).tag(mContext)
        .execute(new JsonCallback<HttpResult<AuthResultBean>>() {
          @Override public void onSuccess(Response<HttpResult<AuthResultBean>> response) {
            AuthResultBean bean = response.body().data;
            User user = DataSupport.findFirst(User.class);
            user.is_auth = bean.is_pass;
            user.save();
            tvName.setText(bean.true_name);
            tvCardNumber.setText(bean.identity_number);
            tvCity.setText(bean.address);
            tvCompanyType.setText(bean.mechanism_type);
            tvCompany.setText(bean.mechanism);
            tvBelongDepartment.setText(bean.department);
            Glide.with(mContext).load(Api.IMAGE_ROOT_URL + bean.front_identity).into(ivFontCard);
            Glide.with(mContext).load(Api.IMAGE_ROOT_URL + bean.back_identity).into(ivBackCard);
            if (TextUtils.isEmpty(bean.work_card)) {
              ivGongpai.setVisibility(View.GONE);
            } else {
              Glide.with(mContext).load(Api.IMAGE_ROOT_URL + bean.work_card).into(ivGongpai);
            }
            if (TextUtils.isEmpty(bean.card)) {
              ivMingpian.setVisibility(View.GONE);
            } else {
              Glide.with(mContext).load(Api.IMAGE_ROOT_URL + bean.card).into(ivMingpian);
            }
            if (TextUtils.isEmpty(bean.contract_page)) {
              ivHetong.setVisibility(View.GONE);
            } else {
              Glide.with(mContext).load(Api.IMAGE_ROOT_URL + bean.contract_page).into(ivHetong);
            }
            if (TextUtils.isEmpty(bean.logo_personal)) {
              ivLogo.setVisibility(View.GONE);
            } else {
              Glide.with(mContext).load(Api.IMAGE_ROOT_URL + bean.logo_personal).into(ivLogo);
            }
            Glide.with(mContext).load(Api.IMAGE_ROOT_URL + bean.header_img).error(R.drawable.img_default_avatar).into(ivAvatar);
            if (bean.is_pass != 3) {
              layoutMask.setVisibility(View.VISIBLE);
              if (bean.is_pass == 2) {
                tvAuditDesc.setText(R.string.verifying);
                imgAuditState.setImageResource(R.drawable.ic_auditing);
              } else if (bean.is_pass == 4) {
                tvAuditDesc.setText(R.string.verify_fail);
                btnAuditAgain.setVisibility(View.VISIBLE);
                imgAuditState.setImageResource(R.drawable.ic_audit_fail);
              }
            }
          }
        });
  }

  @OnClick(R.id.btnAuditAgain) public void onBtnAuditAgainClicked() {
    ActivityUtils.startActivity(MVerify1Activity.class);
    finish();
  }

  @Override public void onBackPressed() {
    ActivityUtils.finishToActivity(MainActivity.class, false);
  }
}
