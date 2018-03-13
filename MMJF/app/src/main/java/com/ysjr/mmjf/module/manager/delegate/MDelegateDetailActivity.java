package com.ysjr.mmjf.module.manager.delegate;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MDelegateDetailBean;
import com.ysjr.mmjf.entity.ProBean;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.dialogfragment.SureOrCancelFragment;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MDelegateDetailActivity extends TopBarBaseActivity {
  public static final String KEY_IS_AGENTED = "agented";
  public static final String KEY_ID = "id";
  @BindView(R.id.btnDelegate) Button btnDelegate;
  @BindView(R.id.tvLoanType) TextView tvLoanType;
  @BindView(R.id.tvLoanNumber) TextView tvLoanNumber;
  @BindView(R.id.tvLoanLimit) TextView tvLoanLimit;
  @BindView(R.id.tvRate) TextView tvRate;
  @BindView(R.id.tvApplyPeople) TextView tvApplyPeople;
  @BindView(R.id.tvTime) TextView tvTime;
  @BindView(R.id.layoutApplyOptions) LinearLayout layoutApplyOptions;
  @BindView(R.id.layoutNeedInfo) LinearLayout layoutNeedInfo;
  private int id;
  private int agented;

  @Override protected int getContentView() {
    return R.layout.m_activity_delegate_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    agented = getIntent().getIntExtra(KEY_IS_AGENTED, 0);
    id = getIntent().getIntExtra(KEY_ID, 0);
    setTitle(getString(R.string.delegate_pro_detail));
    if (agented == 0) {
      setDelegateDisplay();
    } else {
      setDelegatedDisplay();
    }
    httpGetData();
  }

  private void setDelegatedDisplay() {
    btnDelegate.setText(R.string.cancel_delegate);
    btnDelegate.setBackgroundResource(R.color.text_3e6_color);
  }

  private void setDelegateDisplay() {
    btnDelegate.setText(R.string.i_want_delegate);
    btnDelegate.setBackgroundResource(R.color.theme_color);
  }

  private void httpGetData() {
    OkGo.<HttpResult<ProBean>>post(Api.MOBILE_BUSINESS_PRODUCT_DETAIL).tag(mContext)
        .params("id", id)
        .execute(new JsonCallback<HttpResult<ProBean>>() {
          @Override public void onSuccess(Response<HttpResult<ProBean>> response) {
            ProBean bean = response.body().data;
            if (bean == null) {
              return;
            }
            tvLoanType.setText(bean.title);
            tvLoanNumber.setText(bean.loan_number);
            tvLoanLimit.setText(bean.time_limit);
            tvRate.setText(bean.rate);
            tvApplyPeople.setText("已代理：" + bean.apply_peoples + "人");
            tvTime.setText("发布时间：" + SimpleDateUtils.getNoHours(bean.create_time * 1000));
            List<ProBean.OptionsBean> condition = response.body().data.options;
            addOptionsView(condition);
            String[] list = response.body().data.need_data.split(",");
            if (list != null) {
              addNeedDataView(list);
            }
          }
        });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @OnClick(R.id.btnDelegate) public void onViewClicked() {
    if (agented == 0) {
      OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_PRODUCT_SET_AGENT).tag(mContext)
          .params("id", id)
          .params("action", "add")
          .execute(new DialogCallback<HttpResult<Void>>(mContext) {
            @Override public void onSuccess(Response<HttpResult<Void>> response) {
              //变为取消代理
              agented = 1;
              setDelegatedDisplay();
            }
          });
    } else {
      SureOrCancelFragment fg =
          SureOrCancelFragment.newInstance(getString(R.string.sure_cancel_delegate));
      fg.show(getSupportFragmentManager(), "dialog");
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onSureClicked(String desc) {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_PRODUCT_SET_AGENT).tag(mContext)
        .params("id", id)
        .params("action", "cancel")
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            //变为我要代理
            agented = 0;
            setDelegateDisplay();
          }
        });
  }

  private void addNeedDataView(String[] data) {
    for (int i = 0; i < data.length; i++) {
      TextView textView = new TextView(mContext);
      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      textView.setTextSize(12);
      textView.setTextColor(getResources().getColor(R.color.text_3_4d_color));
      textView.setText((i + 1) + "," + data[i] + ";");
      params.setMargins(0, SizeUtils.dp2px(10), 0, 0);
      layoutNeedInfo.addView(textView, params);
    }
  }

  private void addOptionsView(List<ProBean.OptionsBean> options) {
    if (options != null && options.size() > 0) {
      for (ProBean.OptionsBean option : options) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams layoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams.setMargins(0, SizeUtils.dp2px(10), 0, 0);
        TextView tvTitle = new TextView(mContext);
        LinearLayout.LayoutParams titleParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTitle.setText(option.option_name + ":");
        tvTitle.setTextSize(12);
        tvTitle.setTextColor(getResources().getColor(R.color.text_3_4d_color));
        TextView tvContent = new TextView(mContext);
        LinearLayout.LayoutParams contentParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        contentParams.setMargins(SizeUtils.dp2px(5), 0, 0, 0);
        StringBuilder sb = new StringBuilder();
        tvContent.setText(option.option_values);
        tvContent.setTextSize(12);
        tvContent.setTextColor(getResources().getColor(R.color.text_3_4d_color));
        linearLayout.addView(tvTitle, titleParams);
        linearLayout.addView(tvContent, contentParams);
        layoutApplyOptions.addView(linearLayout, layoutParams);
      }
    }
  }
}
