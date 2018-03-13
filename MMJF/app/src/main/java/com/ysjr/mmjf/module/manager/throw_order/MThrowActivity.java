package com.ysjr.mmjf.module.manager.throw_order;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.module.dialogfragment.SuccessDialogFragment;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import com.ysjr.mmjf.utils.TextWatcherChecker;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MThrowActivity extends TopBarBaseActivity {
  @BindView(R.id.tvCusName) TextView tvCusName;
  @BindView(R.id.etPrice) EditText etPrice;
  @BindView(R.id.btnThrowOrder) Button btnThrowOrder;
  @BindView(R.id.tvCreateTime) TextView tvCreateTime;
  @BindView(R.id.tvApplyNumber) TextView tvApplyNumber;
  @BindView(R.id.tvLoanType) TextView tvLoanType;
  @BindView(R.id.tvPeriod) TextView tvPeriod;
  @BindView(R.id.tvAge) TextView tvAge;
  @BindView(R.id.tvAddress) TextView tvAddress;
  @BindView(R.id.tvCareer) TextView tvCareer;
  @BindView(R.id.tvSalary) TextView tvSalary;
  @BindView(R.id.tvHouse) TextView tvHouse;
  @BindView(R.id.tvCar) TextView tvCar;
  @BindView(R.id.tvPhone) TextView tvPhone;
  @BindView(R.id.tvCredit) TextView tvCredit;
  private String assets_information;//资产里面的id拼接在一起
  private String houseValue;
  private String carValue;
  private String creditValue;
  private String job_information;//工作里面的id拼接在一起
  private String careerValue;
  private String monthIncomeValue;
  private String age;
  private String ageValue;
  private String loan_type;
  private String loan_type_value;
  private String period;
  private String periodValue;
  private String city_id;
  private String province_id;
  private String city_value;
  private String region_id;
  private String is_marry;
  private String customer;
  private String apply_number;
  private String description;
  private String mobile;
  private SuccessDialogFragment mDialogFg;

  @Override protected int getContentView() {
    return R.layout.m_activity_throw;
  }

  @Override protected void init(Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    setTitle(getString(R.string.throw_fetch_money));
    new TextWatcherChecker(btnThrowOrder).setRelevantView(etPrice);
    handleIntent();
    initView();
  }

  private void initView() {
    tvCusName.setText(customer);
    tvCreateTime.setText(SimpleDateUtils.getNoHours(System.currentTimeMillis()));
    tvApplyNumber.setText(apply_number+"万元");
    tvLoanType.setText(loan_type_value);
    tvPeriod.setText(periodValue);
    tvAge.setText("年龄："+ageValue);
    tvAddress.setText("现居："+ city_value);
    tvCareer.setText("职业："+careerValue);
    tvSalary.setText("工资："+monthIncomeValue);
    tvHouse.setText("房产："+houseValue);
    tvCar.setText("车产："+carValue);
    String phoneStr = "手机: " + mobile;
    SpannableString spannableString = new SpannableString(phoneStr);
    ForegroundColorSpan colorSpan =
        new ForegroundColorSpan(getResources().getColor(R.color.text_33abff_color));
    spannableString.setSpan(colorSpan,4,phoneStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvPhone.setText(spannableString);
    tvCredit.setText("信用：" + creditValue);
  }

  private void handleIntent() {
    mobile = getIntent().getStringExtra("mobile");
    customer = getIntent().getStringExtra("customer");
    apply_number = getIntent().getStringExtra("apply_number");
    ageValue = getIntent().getStringExtra("ageValue");
    description = getIntent().getStringExtra("description");
    assets_information = getIntent().getStringExtra("assets_information");
    houseValue = getIntent().getStringExtra("houseValue");
    carValue = getIntent().getStringExtra("carValue");
    creditValue = getIntent().getStringExtra("creditValue");
    job_information = getIntent().getStringExtra("job_information");
    careerValue = getIntent().getStringExtra("careerValue");
    monthIncomeValue = getIntent().getStringExtra("monthIncomeValue");
    age = getIntent().getStringExtra("age");
    loan_type = getIntent().getStringExtra("loan_type");
    loan_type_value = getIntent().getStringExtra("loan_type_value");
    period = getIntent().getStringExtra("period");
    periodValue = getIntent().getStringExtra("periodValue");
    city_id = getIntent().getStringExtra("city_id");
    province_id = getIntent().getStringExtra("province_id");
    city_value = getIntent().getStringExtra("city_value");
    region_id = getIntent().getStringExtra("region_id");
    is_marry = getIntent().getStringExtra("is_marry");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @OnClick(R.id.btnThrowOrder) public void onViewClicked() {
    ActivityUtils.startActivity(MThrowDetailActivity.class);
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_ORDER_JUNK_PUBLISH)
        .tag(this)
        .params("customer",customer)
        .params("apply_number",apply_number)
        .params("assets_information",assets_information)
        .params("job_information",job_information)
        .params("age",age)
        .params("loan_type",loan_type)
        .params("is_marry",is_marry)
        .params("period",period)
        //.params("description",description)
        .params("describe",description)
        .params("city_id",city_id)
        .params("province_id",province_id)
        .params("region_id",region_id)
        .params("mobile",mobile)
        .params("score",etPrice.getText().toString())
        .execute(new DialogCallback<HttpResult<Void>>(mContext,"请稍候...") {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            mDialogFg = SuccessDialogFragment.newInstance(R.drawable.c_ic_apply_success,getString(R.string.throw_order_success));
            mDialogFg.show(getSupportFragmentManager(),"dialog");
          }
        });

  }



}
