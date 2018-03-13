package com.ysjr.mmjf.module.customer.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.CProBean;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.apply.ApplyLoanActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-1.
 */

public class ProductDetailActivity extends TopBarBaseActivity {
  public static final String KEY_MANAGER_ID = "manager_id";
  public static final String KEY_PRO_ID = "pro_id";
  @BindView(R.id.tvCategory) TextView tvCategory;
  @BindView(R.id.tvLoanNumber) TextView tvLoanNumber;
  @BindView(R.id.tvTimeLimit) TextView tvTimeLimit;
  @BindView(R.id.tvRate) TextView tvRate;
  @BindView(R.id.tvAgentTime) TextView tvAgentTime;
  @BindView(R.id.tvApplyNumber) TextView tvApplyNumber;
  @BindView(R.id.layoutApplyOptions) LinearLayout layoutApplyOptions;
  @BindView(R.id.layoutNeedInfo) LinearLayout layoutNeedInfo;
  private int loaner_id;
  private String id;
  @Override protected int getContentView() {
    return R.layout.c_activity_product_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    loaner_id = getIntent().getIntExtra(KEY_MANAGER_ID,0);
    id = getIntent().getStringExtra(KEY_PRO_ID);
    setTitle(getString(R.string.product_detail));
    setTopLeftButton(R.drawable.btn_black_back);
    initData();
  }

  private void initData() {
    OkGo.<HttpResult<CProBean>>post(Api.C_PRODUCT_DETAIL)
        .tag(this)
        .params("loaner_id",loaner_id)
        .params("id",id)
        .params("platform", "system")
        .execute(new JsonCallback<HttpResult<CProBean>>() {
          @Override public void onSuccess(Response<HttpResult<CProBean>> response) {
            CProBean bean = response.body().data;
            tvCategory.setText(bean.category);
            tvAgentTime.setText(SimpleDateUtils.getNoHours(bean.agent_time*1000));
            tvLoanNumber.setText(bean.loan_number);
            tvTimeLimit.setText(bean.time_limit);
            tvRate.setText(bean.rate);
            tvApplyNumber.setText(bean.apply_people+"人");
            addOptionsView(bean.options);
            addNeedDataView(bean.need_data);
          }
        });
  }

  private void addNeedDataView(String data) {
    if (data != null && data.contains(",")) {
      String[] infos = data.split(",");
      for (int i = 0;i<infos.length;i++) {
        TextView textView = new TextView(mContext);
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.text_3_4d_color));
        textView.setText((i+1)+","+infos[i]+";");
        params.setMargins(0,SizeUtils.dp2px(10),0,0);
        layoutNeedInfo.addView(textView,params);
      }
    }
  }

  private void addOptionsView(List<CProBean.OptionsBean> options) {
    if (options != null && options.size() > 0) {
      for (CProBean.OptionsBean option:options) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams layoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams.setMargins(0,SizeUtils.dp2px(10),0,0);
        TextView tvTitle = new TextView(mContext);
        LinearLayout.LayoutParams titleParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTitle.setText(option.option+":");
        tvTitle.setTextSize(12);
        tvTitle.setTextColor(getResources().getColor(R.color.text_3_4d_color));
        TextView tvContent = new TextView(mContext);
        LinearLayout.LayoutParams contentParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        contentParams.setMargins(SizeUtils.dp2px(5),0,0,0);
        tvContent.setText(option.option_values);
        tvContent.setTextSize(12);
        tvContent.setTextColor(getResources().getColor(R.color.text_3_4d_color));
        linearLayout.addView(tvTitle,titleParams);
        linearLayout.addView(tvContent,contentParams);
        layoutApplyOptions.addView(linearLayout,layoutParams);
      }
    }
  }

  @OnClick(R.id.btnLoan) public void onViewClicked() {
    if (isLogin()) {
      Intent intent = new Intent(mContext, ApplyLoanActivity.class);
      intent.putExtra(ApplyLoanActivity.KEY_MANAGER_ID, loaner_id);
      intent.putExtra(ApplyLoanActivity.KEY_PRO_ID, id);
      ActivityUtils.startActivity(intent);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

}
