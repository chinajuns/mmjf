package com.ysjr.mmjf.module.manager.rub_order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MRubSuccess;
import com.ysjr.mmjf.entity.Success;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.manager.VerifyDialogFragment;
import com.ysjr.mmjf.module.manager.cus_order.MCusOrderActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MCusDetailActivity extends TopBarBaseActivity {
  public static final String KEY_CUS_ID = "cus_id";
  @BindView(R.id.tvScore) TextView tvScore;
  @BindView(R.id.tvCusName) TextView tvCusName;
  @BindView(R.id.tvDateAndAddress) TextView tvDateAndAddress;
  @BindView(R.id.ivVip) ImageView ivVip;
  @BindView(R.id.tvLoanNumber) TextView tvLoanNumber;
  @BindView(R.id.tvLoanType) TextView tvLoanType;
  @BindView(R.id.tvLoanLimit) TextView tvLoanLimit;
  @BindView(R.id.tvAge) TextView tvAge;
  @BindView(R.id.tvIsMarry) TextView tvIsMarry;
  @BindView(R.id.tvMobile) TextView tvMobile;
  @BindView(R.id.btnRub) Button btnRub;
  @BindView(R.id.layoutWorkInfo) LinearLayout layoutWorkInfo;
  @BindView(R.id.layoutAssetInfo) LinearLayout layoutAssetInfo;
  @BindView(R.id.tvCusDesc) TextView tvCusDesc;
  private int id;
  private Customer bean;
  private RubPayDialogFragment mRubPayFragment;
  private RubScoreDialogFragment mRubScoreFragment;
  private RubPaySuccessDialogFragment mRubPaySuccessFragment;
  private boolean isRubSuccess;
  @Override protected int getContentView() {
    return R.layout.m_activity_cus_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    id = getIntent().getIntExtra(KEY_CUS_ID, 0);
    setTitle(getString(R.string.cus_detail));
    initData();
    EventBus.getDefault().register(this);
  }

  @Override protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }


  @Subscribe(threadMode = ThreadMode.MAIN) public void rubSuccessed(MRubSuccess success) {
    mRubPaySuccessFragment = RubPaySuccessDialogFragment.newInstance(success.score);
    mRubPaySuccessFragment.show(getSupportFragmentManager(),"success_dialog");
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void successClicked(Boolean isScuccess) {
    mRubPaySuccessFragment.dismiss();
    isRubSuccess = true;
    httpGetCusInfo();
  }

  private void initData() {
    httpGetCusInfo();
  }

  private void httpGetCusInfo() {
    OkGo.<HttpResult<Customer>>post(Api.MOBILE_BUSINESS_ORDER_DETAIL)
        .tag(mContext)
        .params("id",id)
        .execute(new JsonCallback<HttpResult<Customer>>() {
          @Override public void onSuccess(Response<HttpResult<Customer>> response) {
            bean = response.body().data;
            if (bean.purchased == 0) {
              btnRub.setVisibility(View.VISIBLE);
              btnRub.setText(bean.score + "积分抢单");
            } else {
              btnRub.setVisibility(View.GONE);
            }
            tvScore.setText(bean.score+"积分");
            tvCusName.setText(bean.customer);
            tvCusDesc.setText(bean.description);
            tvDateAndAddress.setText(SimpleDateUtils.getNoHours(bean.create_time*1000)+"/"+bean.current_place);
            ivVip.setVisibility(bean.is_vip==0?View.GONE:View.VISIBLE);
            tvLoanNumber.setText(bean.apply_number+"万元");
            tvLoanType.setText(bean.loan_type);
            tvLoanLimit.setText(bean.period);
            tvAge.setText(bean.basic.age);
            tvMobile.setText(bean.basic.mobile);
            tvIsMarry.setText(bean.basic.is_marry==0?"未婚":"已婚");
            dynamicAddView(bean.job, layoutWorkInfo);
            dynamicAddView(bean.assets,layoutAssetInfo);
            if (isRubSuccess) {
              Intent intent = new Intent(mContext, MCusOrderActivity.class);
              intent.putExtra(MCusOrderActivity.KEY_REFER, "junk");
              ActivityUtils.startActivity(intent);
            }
          }
        });
  }

  private void dynamicAddView(List<Customer.InfoBean> array, LinearLayout layout) {
    layout.removeAllViews();
    for (int i = 0; i < array.size(); i++) {
      Customer.InfoBean infoBean = array.get(i);
      RelativeLayout rl = new RelativeLayout(mContext);
      LinearLayout.LayoutParams parentParams =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      rl.setPadding(0, SizeUtils.dp2px(15),0,SizeUtils.dp2px(15));
      RelativeLayout.LayoutParams keyParams =
          new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      TextView keyTv = new TextView(mContext);
      keyTv.setText(infoBean.attr_key);
      keyTv.setTextColor(getResources().getColor(R.color.title_black_color));
      keyTv.setTextSize(13);
      rl.addView(keyTv,keyParams);
      RelativeLayout.LayoutParams valueParams =
          new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      TextView valueTv = new TextView(mContext);
      valueTv.setText(infoBean.attr_value);
      valueTv.setTextColor(getResources().getColor(R.color.text_3_4d_color));
      valueTv.setTextSize(13);
      valueParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      rl.addView(valueTv,valueParams);

      layout.addView(rl,parentParams);

      View lineView = new View(mContext);
      LinearLayout.LayoutParams lineParams =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(1));
      lineView.setBackgroundColor(getResources().getColor(R.color.line_color));
      if (i != array.size() - 1) {
        layout.addView(lineView,lineParams);
      }
    }
  }

  @OnClick(R.id.btnRub) public void onViewClicked() {
    User user = DataSupport.findFirst(User.class);
    if (user.is_auth == 1) {
      VerifyDialogFragment verifyDialogFragment = VerifyDialogFragment.newInstance();
      verifyDialogFragment.show(getSupportFragmentManager(), "dialog");
    } else if (user.is_auth == 2) {
      ToastUtils.setBgColor(Color.BLACK);
      ToastUtils.showShort(getString(R.string.verifying));
    } else if (user.is_auth == 4) {
      ToastUtils.setBgColor(Color.BLACK);
      ToastUtils.showShort(getString(R.string.verify_fail));
    } else {
      OkGo.<JSONObject>post(Api.MOBILE_BUSINESS_ORDER_CHECK_PURCHASE)
          .tag(mContext)
          .params("id",id)
          .execute(new JsonCallback<JSONObject>() {
            @Override public void onSuccess(Response<JSONObject> response) {
              try {
                int myScore = response.body().getJSONObject("data").getInt("my_score");
                int rubScore = bean.score;
                int isMine = response.body().getJSONObject("data").getInt("is_mine");
                if (isMine == 1) {//自己的单子
                  ToastUtils.setGravity(Gravity.CENTER, 0, 0);
                  ToastUtils.showShort("不能抢自己的甩单");
                  return;
                }
                if (myScore >= rubScore) {
                  mRubPayFragment =
                      RubPayDialogFragment.newInstance(rubScore, bean.id);
                  mRubPayFragment.show(getSupportFragmentManager(), "pay_dialog");
                } else {
                  mRubScoreFragment = RubScoreDialogFragment.newInstance();
                  mRubScoreFragment.show(getSupportFragmentManager(), "score_dialog");
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          });
    }
  }

}
