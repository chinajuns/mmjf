package com.ysjr.mmjf.module.manager.throw_order;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import cn.iwgang.countdownview.CountdownView;
import com.blankj.utilcode.util.SizeUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MThrowOrderBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017-12-12.
 */

public class MThrowDetailActivity extends TopBarBaseActivity {
  public static final String KEY_THROW_ID = "throw_id";
  @BindView(R.id.countDownView) CountdownView countDownView;
  @BindView(R.id.layoutThrowResult) LinearLayout layoutThrowResult;
  @BindView(R.id.layoutCountDown) LinearLayout layoutCountDown;
  @BindView(R.id.tvSendTime) TextView tvSendTime;
  @BindView(R.id.tvOrderState) TextView tvOrderState;
  @BindView(R.id.tvThrowTime) TextView tvThrowTime;
  @BindView(R.id.tvCusName) TextView tvCusName;
  @BindView(R.id.tvDateAndAddress) TextView tvDateAndAddress;
  @BindView(R.id.ivVip) ImageView ivVip;
  @BindView(R.id.tvLoanNumber) TextView tvLoanNumber;
  @BindView(R.id.tvLoanType) TextView tvLoanType;
  @BindView(R.id.tvLoanLimit) TextView tvLoanLimit;
  @BindView(R.id.tvAge) TextView tvAge;
  @BindView(R.id.tvPhone) TextView tvPhone;
  @BindView(R.id.tvIsMarry) TextView tvIsMarry;
  @BindView(R.id.tvDesc) TextView tvDesc;
  @BindView(R.id.layoutWorkInfo) LinearLayout layoutWorkInfo;
  @BindView(R.id.layoutAssetInfo) LinearLayout layoutAssetInfo;
  private int id;

  @Override protected int getContentView() {
    return R.layout.m_activity_throw_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.throw_order_detail));
    id = getIntent().getIntExtra(KEY_THROW_ID, 0);
    initData();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (countDownView != null) countDownView.stop();
  }

  private void initData() {
    httpGetDetail();
  }

  private void httpGetDetail() {
    OkGo.<HttpResult<MThrowOrderBean>>post(Api.MOBILE_BUSINESS_ORDER_JUNK_DETAIL).tag(mContext)
        .params("id", id)
        .execute(new JsonCallback<HttpResult<MThrowOrderBean>>() {
          @Override public void onSuccess(Response<HttpResult<MThrowOrderBean>> response) {
            MThrowOrderBean bean = response.body().data;
            //设置是否显示倒计时和订单状态
            setCountDownAndOrderState(bean);
            tvCusName.setText(bean.customer);
            tvDateAndAddress.setText(
                SimpleDateUtils.getNoHours(bean.create_time * 1000) + "/" + bean.current_place);
            ivVip.setImageResource(
                bean.is_vip == 0 ? R.drawable.m_ic_common_vip : R.drawable.m_ic_vip);
            tvLoanNumber.setText(bean.apply_number + "万元");
            tvLoanType.setText(bean.loan_type);
            tvLoanLimit.setText(bean.period);
            tvAge.setText(bean.age);
            tvPhone.setText(bean.mobile);
            tvIsMarry.setText(bean.is_marry == 0 ? "未婚" : "已婚");
            dynamicAddView(bean.job, layoutWorkInfo);
            dynamicAddView(bean.assets, layoutAssetInfo);
            tvDesc.setText(bean.description);
          }
        });
  }

  private void dynamicAddView(List<MThrowOrderBean.InfoBean> array, LinearLayout layout) {
    if (array == null) {
      return;
    }
    for (int i = 0; i < array.size(); i++) {
      MThrowOrderBean.InfoBean infoBean = array.get(i);
      RelativeLayout rl = new RelativeLayout(mContext);
      LinearLayout.LayoutParams parentParams =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      rl.setPadding(0, SizeUtils.dp2px(15), 0, SizeUtils.dp2px(15));
      RelativeLayout.LayoutParams keyParams =
          new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      TextView keyTv = new TextView(mContext);
      keyTv.setText(infoBean.attr_key);
      keyTv.setTextColor(getResources().getColor(R.color.title_black_color));
      keyTv.setTextSize(13);
      rl.addView(keyTv, keyParams);
      RelativeLayout.LayoutParams valueParams =
          new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      TextView valueTv = new TextView(mContext);
      valueTv.setText(infoBean.attr_value);
      valueTv.setTextColor(getResources().getColor(R.color.text_3_4d_color));
      valueTv.setTextSize(13);
      valueParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      rl.addView(valueTv, valueParams);

      layout.addView(rl, parentParams);

      View lineView = new View(mContext);
      LinearLayout.LayoutParams lineParams =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(1));
      lineView.setBackgroundColor(getResources().getColor(R.color.line_color));
      if (i != array.size() - 1) {
        layout.addView(lineView, lineParams);
      }
    }
  }

  public int getAge(Date birthDay) {
    Calendar cal = Calendar.getInstance();

    if (cal.before(birthDay)) {
      throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
    }
    int yearNow = cal.get(Calendar.YEAR);
    int monthNow = cal.get(Calendar.MONTH);
    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
    cal.setTime(birthDay);

    int yearBirth = cal.get(Calendar.YEAR);
    int monthBirth = cal.get(Calendar.MONTH);
    int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

    int age = yearNow - yearBirth;

    if (monthNow <= monthBirth) {
      if (monthNow == monthBirth) {
        if (dayOfMonthNow < dayOfMonthBirth) age--;
      } else {
        age--;
      }
    }
    return age;
  }

  private void setCountDownAndOrderState(MThrowOrderBean bean) {
    if (bean.junk_status == 2) {
      layoutCountDown.setVisibility(View.VISIBLE);
      layoutThrowResult.setVisibility(View.GONE);
      countDownView.start(bean.expire_time*1000-System.currentTimeMillis());

    } else {
      layoutCountDown.setVisibility(View.GONE);
      layoutThrowResult.setVisibility(View.VISIBLE);
      tvSendTime.setText("发布时间：" + SimpleDateUtils.getHasHours(bean.create_time * 1000));
      tvThrowTime.setText("甩单时间：" + SimpleDateUtils.getHasHours(bean.create_time * 1000));
      if (bean.junk_status == -1) {
        tvOrderState.setText(R.string.audit_fail);
        tvOrderState.setTextColor(getResources().getColor(R.color.btn_disabled_color));
      } else if (bean.junk_status == 1) {
        tvOrderState.setTextColor(getResources().getColor(R.color.theme_color));
        tvOrderState.setText(R.string.auditing);
      } else if (bean.junk_status == 3) {
        tvOrderState.setText(R.string.throw_success);
        tvOrderState.setTextColor(Color.RED);
      } else if (bean.junk_status == 4) {
        tvOrderState.setText(R.string.expired);
        tvOrderState.setTextColor(getResources().getColor(R.color.btn_disabled_color));
      }
    }
  }
}
