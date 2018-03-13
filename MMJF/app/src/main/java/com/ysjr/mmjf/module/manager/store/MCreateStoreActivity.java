package com.ysjr.mmjf.module.manager.store;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.me.order.COrderEvaluateActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-12-12.
 */

public class MCreateStoreActivity extends TopBarBaseActivity{
  @BindView(R.id.ivAvatar) ImageView ivAvatar;
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.tvCity) TextView tvCity;
  @BindView(R.id.tvCompany) TextView tvCompany;
  @BindView(R.id.etCareerTime) EditText etCareerTime;
  @BindView(R.id.etCusDesc) EditText etCusDesc;
  @BindView(R.id.tvNum) TextView tvNum;
  @BindView(R.id.btnCommit) Button btnCommit;
  @BindView(R.id.etLoanLimit) EditText etLoanLimit;
  @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;

  @Override protected int getContentView() {
    return R.layout.m_activity_create_store;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.create_store));
    setListener();
    httpGetCusInfo();
    CommonUtils.setEmojFilter(etCusDesc,50);
  }

  private void setListener() {
    etCareerTime.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(etLoanLimit.getText().toString().trim()) && !TextUtils.isEmpty(
            etCareerTime.getText().toString().trim())) {
          btnCommit.setEnabled(true);
        } else {
          btnCommit.setEnabled(false);
        }
      }
    });
    etLoanLimit.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(etLoanLimit.getText().toString().trim()) && !TextUtils.isEmpty(
            etCareerTime.getText().toString().trim())) {
          btnCommit.setEnabled(true);
        } else {
          btnCommit.setEnabled(false);
        }
      }
    });
    etCusDesc.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        String num = s.toString().trim().length() + "/300";
        tvNum.setText(num);
      }
    });
    nestedScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override public void onGlobalLayout() {
        Log.e(COrderEvaluateActivity.class.getSimpleName(), "onGlobalLayout()");
        Rect rect = new Rect();
        nestedScrollView.getWindowVisibleDisplayFrame(rect);
        int keyBoardHeight = ScreenUtils.getScreenHeight() - rect.bottom;
        Log.e(COrderEvaluateActivity.class.getName(), "keyBoardHeight:" + keyBoardHeight);
        if (keyBoardHeight > 150 && etCusDesc.hasFocus()) {
          nestedScrollView.smoothScrollTo(0,nestedScrollView.getScrollY()+keyBoardHeight);
        }
      }
    });
  }

  @OnClick(R.id.btnCommit) public void onBtnCommitClicked() {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_CREATE)
        .tag(mContext)
        .params("work_time",etCareerTime.getText().toString())
        .params("introduce",etCusDesc.getText().toString())
        .params("max_loan",etLoanLimit.getText().toString())
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            ActivityUtils.startActivity(MStoreActivity.class);
          }
        });
  }

  private void httpGetCusInfo() {
    OkGo.<JSONObject>post(Api.MOBILE_BUSINESS_SHOP_SHOW_CREATE)
        .tag(mContext)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              String header_img = response.body().getJSONObject("data").getString("header_img");
              String username = response.body().getJSONObject("data").getString("true_name");
              String province = response.body().getJSONObject("data").getJSONObject("address").getString("province");
              String city = response.body().getJSONObject("data").getJSONObject("address").getString("city");
              String district = response.body().getJSONObject("data").getJSONObject("address").getString("district");
              String agent_name = response.body().getJSONObject("data").getString("mechanism");
              Glide.with(mContext)
                  .load(Api.IMAGE_ROOT_URL + header_img)
                  .placeholder(R.drawable.img_default_avatar)
                  .into(ivAvatar);
              tvName.setText(username);
              tvCity.setText(province+"-"+city+"-"+district);
              tvCompany.setText(agent_name);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }
}
