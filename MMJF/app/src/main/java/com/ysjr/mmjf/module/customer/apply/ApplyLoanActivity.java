package com.ysjr.mmjf.module.customer.apply;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.ConfigBean;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.JsonBean;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.me.order.COrderEvaluateActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-11-29.
 */

public class ApplyLoanActivity extends TopBarBaseActivity implements TextWatcher {
  public static final String KEY_MANAGER_ID = "manager_id";
  public static final String KEY_PRO_ID = "pro_id";
  public static final int CAREER = 19;//职业身份
  public static final int MONTH_INCOME_WAY =50;//工资发放形式
  public static final int FUND = 20;//是否拥有公积金
  public static final int SOCIAL_SECURITY = 21;//是否有本地社保
  public static final int CAR = 25;//车产情况
  public static final int HOUSE = 26;//房产情况
  public static final int CREDIT = 29;//信用情况
  public static final int LOAN_TYPE = 1;//贷款类型
  public static final int LOAN_LIMIT = 30;//贷款期限
  private static final int IS_MARRY = 100;
  @BindView(R.id.tvAge) TextView tvAge;
  @BindView(R.id.etMoney) MaterialEditText etMoney;
  @BindView(R.id.tvLoanLimit) TextView tvLoanLimit;
  @BindView(R.id.tvLoanType) TextView tvLoanType;
  @BindView(R.id.etName) MaterialEditText etName;
  @BindView(R.id.tvCity) TextView tvCity;
  @BindView(R.id.tvIsSingle) TextView tvIsSingle;
  @BindView(R.id.tvCareer) TextView tvCareer;
  @BindView(R.id.etCareerTime) EditText etCareerTime;
  @BindView(R.id.etSalary) EditText etSalary;
  @BindView(R.id.tvMonthIncomeWay) TextView tvMonthIncomeWay;
  @BindView(R.id.tvFund) TextView tvFund;
  @BindView(R.id.tvSocialSecurity) TextView tvSocialSecurity;
  @BindView(R.id.tvHouse) TextView tvHouse;
  @BindView(R.id.tvCar) TextView tvCar;
  @BindView(R.id.tvCredit) TextView tvCredit;
  @BindView(R.id.etCusDesc) EditText etCusDesc;
  @BindView(R.id.tvNum) TextView tvNum;
  @BindView(R.id.btnCommit) Button btnCommit;
  @BindView(R.id.nestedScrollView) ScrollView nestedScrollView;
  private OptionsPickerView pvCustomOptions;
  private List<ConfigBean.ValuesBean> mLoanLimitList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mLoanTypeList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mIsSingleList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mCareerList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mMonthIcomeWayList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mFundList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mSocialSecurityList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mCarList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mHouseList = new ArrayList<>();
  private List<ConfigBean.ValuesBean> mCreditList = new ArrayList<>();
  private int loaner_id;
  private String product_id = "";
  private String assets_information = "";//资产里面的id拼接在一起
  private String fundId = "";
  private String housId = "";
  private String carId = "";
  private String creditId = "";
  private String socialId = "";
  private String job_information = "";//工作里面的id拼接在一起
  private String careerId = "";
  private String monthIcomeWayId = "";
  private String age = "";
  private String loan_type = "";
  private String time_limit = "";
  private String city_id = "";
  private String province_id = "";
  private String region_id = "";
  private String is_marry = "";

  private OptionsPickerView pvOptions;
  private ArrayList<JsonBean> options1Items = new ArrayList<>();
  private ArrayList<ArrayList<JsonBean.CityBean>> options2Items = new ArrayList<>();
  private ArrayList<ArrayList<ArrayList<JsonBean.CityBean.DistrictBean>>> options3Items = new ArrayList<>();
  private Thread thread;
  private static final int MSG_LOAD_DATA = 0x0001;
  private static final int MSG_LOAD_SUCCESS = 0x0002;
  private static final int MSG_LOAD_FAILED = 0x0003;

  private boolean isLoaded = false;
  @Override protected int getContentView() {
    return R.layout.c_activity_apply_loan;
  }

  @Override protected void init(Bundle savedInstanceState) {
    product_id = getIntent().getStringExtra(KEY_PRO_ID);
    loaner_id = getIntent().getIntExtra(KEY_MANAGER_ID, 0);
    setTitle(getString(R.string.apply_loan));
    setTopLeftButton(R.drawable.btn_black_back);
    initData();
    etName.addTextChangedListener(this);
    etMoney.addTextChangedListener(this);
    CommonUtils.setEmojFilter(etName,4);
    CommonUtils.setEmojFilter(etCusDesc,300);
    etCusDesc.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        int num = s.toString().trim().length();
        tvNum.setText(num+"/300");
      }
    });
    nestedScrollView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            Rect rect = new Rect();
            if (nestedScrollView != null) nestedScrollView.getWindowVisibleDisplayFrame(rect);
            int keyBoardHeight = ScreenUtils.getScreenHeight() - rect.bottom;
            if (keyBoardHeight > 150 && etCusDesc.hasFocus()) {
              nestedScrollView.smoothScrollTo(0, nestedScrollView.getScrollY() + keyBoardHeight);
            }
          }
        });
  }
  private void initData() {
    httpGetConfig();
  }

  private void httpGetConfig() {
    mIsSingleList.add(new ConfigBean.ValuesBean("1", "是"));
    mIsSingleList.add(new ConfigBean.ValuesBean("0", "否"));
    OkGo.<HttpResult<ConfigBean>>get(Api.CLIENT_CONFIG)
        .tag(this)
        .execute(new JsonCallback<HttpResult<ConfigBean>>() {
          @Override public void onSuccess(Response<HttpResult<ConfigBean>> response) {
            List<ConfigBean.WorkBean> workBeanList = response.body().data.work;
            List<ConfigBean.AssetsBean> assetsBeanList = response.body().data.assets;
            List<ConfigBean.BasicBean> basicBeanList = response.body().data.basic;
            for (ConfigBean.WorkBean workBean:workBeanList) {
              switch (workBean.id) {
                case CAREER:
                  mCareerList = workBean.values;
                  break;
                case MONTH_INCOME_WAY:
                  mMonthIcomeWayList = workBean.values;
                  break;
              }
            }

            for (ConfigBean.AssetsBean assetsBean:assetsBeanList) {
              switch (assetsBean.id) {
                case FUND:
                  mFundList = assetsBean.values;
                  break;
                case SOCIAL_SECURITY:
                  mSocialSecurityList = assetsBean.values;
                  break;
                case CAR:
                  mCarList = assetsBean.values;
                  break;
                case HOUSE:
                  mHouseList = assetsBean.values;
                  break;
                case CREDIT:
                  mCreditList = assetsBean.values;
                  break;
              }
            }

            for (ConfigBean.BasicBean basicBean : basicBeanList) {
              switch (basicBean.id) {
                case LOAN_TYPE:
                  mLoanTypeList = basicBean.values;
                  break;
                case LOAN_LIMIT:
                  mLoanLimitList = basicBean.values;
                  break;
              }
            }

          }
        });
  }

  @OnClick(R.id.layoutAge) public void onLayoutAgeClicked() {
    KeyboardUtils.hideSoftInput(mContext);
    displayDatePick();
  }
  private boolean checkIfSelectAll() {
    if (TextUtils.isEmpty(housId)
        || TextUtils.isEmpty(carId)
        || TextUtils.isEmpty(careerId)
        || TextUtils.isEmpty(monthIcomeWayId)
        || TextUtils.isEmpty(age)
        || TextUtils.isEmpty(loan_type)
        || TextUtils.isEmpty(time_limit)
        || TextUtils.isEmpty(province_id)
        || TextUtils.isEmpty(is_marry)
        || TextUtils.isEmpty(etName.getText().toString())
        ||TextUtils.isEmpty(etMoney.getText().toString())
        ||TextUtils.isEmpty(etCareerTime.getText().toString())
        ||TextUtils.isEmpty(etSalary.getText().toString())) {
      return false;
    }
    return true;
  }
  private void initPickerView(final TextView tv, final String title, final List<ConfigBean.ValuesBean> datas,
      final int type) {
    KeyboardUtils.hideSoftInput(mContext);
    pvCustomOptions =
        new OptionsPickerView.Builder(mContext, new OptionsPickerView.OnOptionsSelectListener() {
          @Override public void onOptionsSelect(int options1, int options2, int options3, View v) {
            ConfigBean.ValuesBean bean = datas.get(options1);
            tv.setText(bean.name);
            tv.setTextColor(Color.parseColor("#4d4d4d"));
            switch (type) {
              case LOAN_LIMIT:
                time_limit = bean.id+"";
                break;
              case LOAN_TYPE:
                loan_type = bean.id + "";
                break;
              case IS_MARRY:
                is_marry = bean.id + "";
                break;
              case CAREER:
                careerId = bean.id+"";
                break;
              case MONTH_INCOME_WAY:
                monthIcomeWayId = bean.id+"";
                break;
              case FUND:
                fundId = bean.id+"";
                break;
              case SOCIAL_SECURITY:
                socialId = bean.id+"";
                break;
              case HOUSE:
                housId = bean.id+"";
                break;
              case CAR:
                carId = bean.id + "";
                break;
              case CREDIT:
                creditId = bean.id + "";
                break;
            }
          }
        }).setLayoutRes(R.layout.c_pickerview_custom, new CustomListener() {
          @Override public void customLayout(View v) {
            ImageButton btnCancel = v.findViewById(R.id.btnCancel);
            ImageButton btnSelect = v.findViewById(R.id.btnSelect);
            TextView tvTitle = v.findViewById(R.id.tvTitle);
            tvTitle.setText(title);
            btnCancel.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                pvCustomOptions.dismiss();
              }
            });
            btnSelect.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                pvCustomOptions.returnData();
                pvCustomOptions.dismiss();
                setBtnEnabled();
              }
            });
          }
        }).setOutSideCancelable(false).build();
    pvCustomOptions.setPicker(datas);
    pvCustomOptions.show();
  }

  private void displayDatePick() {
    Calendar startDate = Calendar.getInstance();
    startDate.set(1950, 1, 1);
    TimePickerView pvTime =
        new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
          @Override public void onTimeSelect(Date date, View v) {//选中事件回调
            age = SimpleDateUtils.getNoHours(date.getTime());
            tvAge.setText(getAge(date) + "岁");
            tvAge.setTextColor(Color.parseColor("#4d4d4d"));
            setBtnEnabled();
            KeyboardUtils.hideSoftInput(mContext);
          }
        })

            .setType(new boolean[] { true, true, true, false, false, false })// 默认全部显示
            .setCancelText("取消")//取消按钮文字
            .setSubmitText("确定")//确认按钮文字
            .setContentSize(18)//滚轮文字大小
            .setTitleSize(20)//标题文字大小
            .setTitleText("出生日期")//标题文字
            .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(false)//是否循环滚动
            .setTitleColor(Color.BLACK)//标题文字颜色
            .setSubmitColor(Color.BLACK)//确定按钮文字颜色
            .setCancelColor(Color.RED)//取消按钮文字颜色
            .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
            .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
            .setLabel("年", "月", "日", "", "", "")//默认设置为年月日时分秒
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .setDate(Calendar.getInstance()).setRangDate(startDate, Calendar.getInstance()).build();
    pvTime.show();
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



  @OnClick(R.id.layoutLoanLimit) public void onLayoutLoanLimitClicked() {
    initPickerView(tvLoanLimit,"贷款期限",mLoanLimitList,LOAN_LIMIT);
  }

  @OnClick(R.id.layoutLoanType) public void onLayoutLoanTypeClicked() {
    initPickerView(tvLoanType,"贷款类型",mLoanTypeList,LOAN_TYPE);
  }

  @OnClick(R.id.layoutCity) public void onLayoutCityClicked() {
    KeyboardUtils.hideSoftInput(mContext);
    if (isLoaded) {
      showPickerView();
    } else {
      mHandler.sendEmptyMessage(MSG_LOAD_DATA);
    }
  }

  @OnClick(R.id.layoutIsSingle) public void onLayoutIsSingleClicked() {
    initPickerView(tvIsSingle,"是否结婚",mIsSingleList,IS_MARRY);
  }

  @OnClick(R.id.layoutCareer) public void onLayoutCareerClicked() {
    initPickerView(tvCareer,"职业身份",mCareerList,CAREER);
  }

  @OnClick(R.id.layoutMonthIncomeWay) public void onLayoutMonthIncomeWayClicked() {
    initPickerView(tvMonthIncomeWay,"工资发放形式",mMonthIcomeWayList,MONTH_INCOME_WAY);
  }

  @OnClick(R.id.layoutFund) public void onLayoutFundClicked() {
    initPickerView(tvFund,"是否有本地公积金",mFundList,FUND);
  }

  @OnClick(R.id.layoutSocialSecurity) public void onLayoutSocialSecurityClicked() {
    initPickerView(tvSocialSecurity,"是否有本地社保",mSocialSecurityList,SOCIAL_SECURITY);
  }

  @OnClick(R.id.layoutHouse) public void onLayoutHouseClicked() {
    initPickerView(tvHouse,"名下房产类型",mHouseList,HOUSE);
  }

  @OnClick(R.id.layoutCar) public void onLayoutCarClicked() {
    initPickerView(tvCar,"名下车产类型",mCarList,CAR);
  }

  @OnClick(R.id.layoutCredit) public void onLayoutCreditClicked() {
    initPickerView(tvCredit,"个人信用情况",mCreditList,CREDIT);
  }




  @OnClick(R.id.btnCommit) public void onBtnCommitClicked() {
    //assets_information = fundId + "," + socialId + "," + housId + "," + carId + "," + creditId;
    assets_information = housId + "," + carId;
    if (!TextUtils.isEmpty(fundId)) {
      assets_information += "," + fundId;
    }
    if (!TextUtils.isEmpty(socialId)) {
      assets_information += "," + socialId;
    }
    if (!TextUtils.isEmpty(creditId)) {
      assets_information += "," + creditId;
    }
    job_information = careerId + "," + monthIcomeWayId;
    PostRequest<JSONObject> request = OkGo.<JSONObject>post(Api.APPLY_LOAN).tag(this)
        .params("name", etName.getText().toString())
        .params("apply_number", etMoney.getText().toString())
        .params("assets_information", assets_information)
        .params("job_information", job_information)
        .params("age", age)
        .params("loaner_id", loaner_id)
        .params("loan_type", loan_type)
        .params("is_marry", is_marry)
        .params("time_limit", time_limit)
        .params("describe", etCusDesc.getText().toString())
        .params("city_id", city_id)
        .params("province_id", province_id)
        .params("region_id", region_id)
        .params("income",etSalary.getText().toString())
        .params("work_time",etCareerTime.getText().toString()+"年");
    if (!TextUtils.isEmpty(product_id)) {
      request.params("product_id", product_id);
    }
       request.execute(new DialogCallback<JSONObject>(mContext,"请稍候...") {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              String mobile = response.body().getJSONObject("data").getString("mobile");
              int id = response.body().getJSONObject("data").getInt("id");
              Intent intent = new Intent(mContext, ApplySuccessActivity.class);
              intent.putExtra(ApplySuccessActivity.KEY_MANAGER_PHONE, mobile);
              intent.putExtra(ApplySuccessActivity.KEY_ID, id);
              ActivityUtils.startActivity(intent);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }
  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_LOAD_DATA:
          if (thread==null){//如果已创建就不再重新创建子线程了

            thread = new Thread(new Runnable() {
              @Override
              public void run() {
                // 写子线程中的操作,解析省市区数据
                initJsonData();
              }
            });
            thread.start();
          }
          break;

        case MSG_LOAD_SUCCESS:
          isLoaded = true;
          showPickerView();
          break;

        case MSG_LOAD_FAILED:
          isLoaded = false;
          break;

      }
    }
  };
  private void showPickerView() {// 弹出选择器

    pvOptions= new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
      @Override
      public void onOptionsSelect(int options1, int options2, int options3, View v) {
        //返回的分别是三个级别的选中位置
        String tx = options1Items.get(options1).getPickerViewText()+"-"+
            options2Items.get(options1).get(options2).getPickerViewText()+"-"+
            options3Items.get(options1).get(options2).get(options3).getPickerViewText();
        tvCity.setText(tx);
        province_id = options1Items.get(options1).getId() + "";
        city_id = options2Items.get(options1).get(options2).getId()+"";
        region_id = options3Items.get(options1).get(options2).get(options3).getId() + "";
      }
    })
        .setLayoutRes(R.layout.c_pickerview_custom, new CustomListener() {
          @Override public void customLayout(View v) {
            ImageButton btnCancel = v.findViewById(R.id.btnCancel);
            ImageButton btnSelect = v.findViewById(R.id.btnSelect);
            TextView tvTitle = v.findViewById(R.id.tvTitle);
            tvTitle.setText("地址");
            btnCancel.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                pvOptions.dismiss();
              }
            });
            btnSelect.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                pvOptions.returnData();
                pvOptions.dismiss();
              }
            });
          }
        })
        .setDividerColor(Color.BLACK)
        .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
        .setContentTextSize(20)
        .setOutSideCancelable(false)
        .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
    pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
    pvOptions.show();
  }

  private void initJsonData() {//解析数据

    /**
     * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
     * 关键逻辑在于循环体
     *
     * */
    String JsonData = CommonUtils.getJson(this,"province.json");//获取assets目录下的json文件数据

    ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

    /**
     * 添加省份数据
     *
     * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
     * PickerView会通过getPickerViewText方法获取字符串显示出来。
     */
    options1Items = jsonBean;

    for (int i=0;i<jsonBean.size();i++){//遍历省份
      ArrayList<JsonBean.CityBean> CityList = new ArrayList<>();//该省的城市列表（第二级）
      ArrayList<ArrayList<JsonBean.CityBean.DistrictBean>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

      for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
        JsonBean.CityBean CityName = jsonBean.get(i).getCityList().get(c);
        CityList.add(CityName);//添加城市

        ArrayList<JsonBean.CityBean.DistrictBean> City_AreaList = new ArrayList<>();//该城市的所有地区列表

        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
        if (jsonBean.get(i).getCityList().get(c).getDistrict() == null
            ||jsonBean.get(i).getCityList().get(c).getDistrict().size()==0) {
          City_AreaList.add(new JsonBean.CityBean.DistrictBean(""));
        }else {

          for (int d=0; d < jsonBean.get(i).getCityList().get(c).getDistrict().size(); d++) {//该城市对应地区所有数据
            JsonBean.CityBean.DistrictBean AreaName = jsonBean.get(i).getCityList().get(c).getDistrict().get(d);

            City_AreaList.add(AreaName);//添加该城市所有地区数据
          }
        }
        Province_AreaList.add(City_AreaList);//添加该省所有地区数据
      }

      /**
       * 添加城市数据
       */
      options2Items.add(CityList);

      /**
       * 添加地区数据
       */
      options3Items.add(Province_AreaList);
    }

    mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

  }


  public ArrayList<JsonBean> parseData(String result) {//Gson 解析
    ArrayList<JsonBean> detail = new ArrayList<>();
    try {
      JSONArray data = new JSONArray(result);
      Gson gson = new Gson();
      for (int i = 0; i < data.length(); i++) {
        JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
        detail.add(entity);
      }
    } catch (Exception e) {
      e.printStackTrace();
      mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
    }
    return detail;
  }

  @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override public void afterTextChanged(Editable s) {
    setBtnEnabled();
  }
  //判断是否填完所有必填项
  private void setBtnEnabled() {
    if (checkIfSelectAll()) {
      btnCommit.setEnabled(true);
    } else {
      btnCommit.setEnabled(false);
    }
  }
}
