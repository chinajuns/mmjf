package com.ysjr.mmjf.module.customer.me.verify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.AuthResultBean;
import com.ysjr.mmjf.entity.JsonBean;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.TextWatcherChecker;
import java.util.ArrayList;
import org.json.JSONArray;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CVerify1StepActivity extends TopBarBaseActivity {

  @BindView(R.id.etName) EditText etName;
  @BindView(R.id.tvAddress) TextView tvAddress;
  @BindView(R.id.etCard) EditText etCard;
  @BindView(R.id.btnNext) Button btnNext;
  private OptionsPickerView pvOptions;
  private ArrayList<JsonBean> options1Items = new ArrayList<>();
  private ArrayList<ArrayList<JsonBean.CityBean>> options2Items = new ArrayList<>();
  private ArrayList<ArrayList<ArrayList<JsonBean.CityBean.DistrictBean>>> options3Items = new ArrayList<>();
  private Thread thread;
  private static final int MSG_LOAD_DATA = 0x0001;
  private static final int MSG_LOAD_SUCCESS = 0x0002;
  private static final int MSG_LOAD_FAILED = 0x0003;

  private boolean isLoaded = false;
  private int cityId;
  private int provinceId;
  private String regionId;
  @Override protected int getContentView() {
    return R.layout.c_activity_verify_1_step;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.verify_name));
    new TextWatcherChecker(btnNext).setRelevantView(etName,etCard,tvAddress);
    CommonUtils.setEmojFilter(etName,4);
  }

  @OnClick(R.id.layoutAddress) public void onLayoutAddressClicked() {
    KeyboardUtils.hideSoftInput(mContext);
    if (isLoaded) {
      showPickerView();
    } else {
      mHandler.sendEmptyMessage(MSG_LOAD_DATA);
    }
  }

  @OnClick(R.id.btnNext) public void onBtnNextClicked() {
    if (RegexUtils.isIDCard18(etCard.getText().toString()) || RegexUtils.isIDCard15(
        etCard.getText().toString())) {
      Intent intent = new Intent(mContext, CVerify2StepActivity.class);
      intent.putExtra(CVerify2StepActivity.KEY_PROVINCE_ID, provinceId);
      intent.putExtra(CVerify2StepActivity.KEY_CITY_ID, cityId);
      intent.putExtra(CVerify2StepActivity.KEY_REGION_ID, regionId);
      intent.putExtra(CVerify2StepActivity.KEY_NAME, etName.getText().toString());
      intent.putExtra(CVerify2StepActivity.KEY_CARD_NUMBER, etCard.getText().toString());
      ActivityUtils.startActivity(intent);
    } else {
      ToastUtils.showShort(R.string.input_legal_card);
    }
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
        tvAddress.setText(tx);
        provinceId = options1Items.get(options1).getId();
        cityId = options2Items.get(options1).get(options2).getId();
        regionId = options3Items.get(options1).get(options2).get(options3).getId();
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
}
